package com.pazzioliweb.cajerosmodule.service;

import com.pazzioliweb.cajerosmodule.dtos.InformeDiarioVentasDTO;
import com.pazzioliweb.cajerosmodule.dtos.InformeDiarioVentasDTO.*;
import com.pazzioliweb.cajerosmodule.entity.DetalleCajero;
import com.pazzioliweb.cajerosmodule.entity.MovimientoCajero;
import com.pazzioliweb.cajerosmodule.repositori.DetalleCajeroRepository;
import com.pazzioliweb.cajerosmodule.repositori.InformeDiarioRepository;
import com.pazzioliweb.cajerosmodule.repositori.MovimientoCajeroRepository;

import com.pazzioliweb.commonbacken.dtos.DatosSesiones;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;
import com.pazzioliweb.commonbacken.repositorio.TipoidentificacionRepository;
import com.pazzioliweb.commonbacken.util.Jwcommon;
import com.pazzioliweb.empresasback.entity.Empresa;
import com.pazzioliweb.empresasback.entity.Regimen;
import com.pazzioliweb.empresasback.repositori.EmpresaRepositori;
import com.pazzioliweb.empresasback.repositori.RegimenRepositori;
import com.pazzioliweb.productosmodule.entity.Bodegas;
import com.pazzioliweb.productosmodule.entity.Usuariobodega;
import com.pazzioliweb.productosmodule.repositori.BodegasRepository;
import com.pazzioliweb.productosmodule.repositori.UsuariobodegaRepository;
import com.pazzioliweb.usuariosbacken.entity.Usuario;
import com.pazzioliweb.usuariosbacken.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio que construye el Informe Diario de Ventas (Reporte Z / Cuadre de Caja).
 *
 * Sin dependencia circular: usa SQL nativo (InformeDiarioRepository)
 * para consultar tablas de ventas sin importar entidades de ventasmodule.
 *
 * Secciones del reporte impreso:
 *   1. Encabezado (cajero, fecha, hora, transacción inicial/final, # transacciones, Z)
 *   2. Movimiento de Cuentas
 *   3. Ventas por Línea
 *   4. Formas de Pago
 *   5. Recibos de Caja  (tipo ABONO en MovimientoCajero)
 *   6. Comprobantes de Egreso  (tipo EGRESO en MovimientoCajero)
 *   7. Vales
 *   8. Devoluciones
 *   9. Resumen Final (Neto Caja, UPT, VPT, VPU)
 */
@Service
public class InformeDiarioService {

    private static final ZoneId            ZONA_BOGOTA = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter HORA_FMT    = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final DetalleCajeroRepository    detalleCajeroRepo;
    private final MovimientoCajeroRepository movimientoRepo;
    private final InformeDiarioRepository    informeDiarioRepo;
    private final EmpresaRepositori           empresaRepo;
    private final RegimenRepositori           regimenRepo;
    private final TipoidentificacionRepository tipoIdentificacionRepo;
    private final UsuariobodegaRepository      usuariobodegaRepo;
    private final BodegasRepository           bodegasRepo;
    private final UsuarioRepository           usuarioRepo;
    private final RedisTemplate<String, DatosSesiones> redisTemplate;
    private final Jwcommon                     jwcommon;

    // Map para iniciales de tipo de identificación
    private static final java.util.Map<Integer, String> TIPO_IDENTIFICACION_INICIALES = java.util.Map.of(
        1, "CC",
        2, "NIT",
        3, "CE",
        4, "TI",
        5, "PP",
        6, "IDC",
        7, "CD",
        8, "RC",
        9, "DE"
    );

    @Autowired
    public InformeDiarioService(DetalleCajeroRepository detalleCajeroRepo,
                                MovimientoCajeroRepository movimientoRepo,
                                InformeDiarioRepository informeDiarioRepo,
                                EmpresaRepositori empresaRepo,
                                RegimenRepositori regimenRepo,
                                TipoidentificacionRepository tipoIdentificacionRepo,
                                UsuariobodegaRepository usuariobodegaRepo,
                                BodegasRepository bodegasRepo,
                                UsuarioRepository usuarioRepo,
                                RedisTemplate<String, DatosSesiones> redisTemplate,
                                Jwcommon jwcommon) {
        this.detalleCajeroRepo      = detalleCajeroRepo;
        this.movimientoRepo         = movimientoRepo;
        this.informeDiarioRepo     = informeDiarioRepo;
        this.empresaRepo           = empresaRepo;
        this.regimenRepo           = regimenRepo;
        this.tipoIdentificacionRepo = tipoIdentificacionRepo;
        this.usuariobodegaRepo      = usuariobodegaRepo;
        this.bodegasRepo           = bodegasRepo;
        this.usuarioRepo           = usuarioRepo;
        this.redisTemplate         = redisTemplate;
        this.jwcommon              = jwcommon;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MÉTODO PRINCIPAL
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Genera el Informe Diario de Ventas para una sesión y un día específico.
     *
     * @param detalleCajeroId ID de la sesión de caja
     * @param fecha           Día del informe (si es null → hoy)
     */
    @Transactional(readOnly = true)
    public InformeDiarioVentasDTO generarInforme(Long detalleCajeroId, LocalDate fecha, String token) {

        DetalleCajero sesion = detalleCajeroRepo.findById(detalleCajeroId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada: " + detalleCajeroId));

        // Fecha del informe: parámetro recibido, o la fecha de hoy
        LocalDate fechaInforme = (fecha != null) ? fecha : LocalDate.now(ZONA_BOGOTA);

        return construirInforme(sesion, fechaInforme, token);
    }

    /**
     * Genera el Informe Diario buscando automáticamente la sesión (o sesiones) correctas
     * a partir del cajeroId y la fecha.
     *
     * Si hay más de una sesión con movimientos en esa fecha (por cierre/reapertura de
     * medianoche), consolida los datos de todas las sesiones en un solo informe.
     *
     * @param cajeroId ID del cajero
     * @param fecha    Día del informe
     */
    @Transactional(readOnly = true)
    public InformeDiarioVentasDTO generarInformePorCajero(Integer cajeroId, LocalDate fecha, String token) {
        System.out.println("=== generarInformePorCajero INICIADO: cajeroId=" + cajeroId + ", fecha=" + fecha);

        List<DetalleCajero> sesiones = detalleCajeroRepo.findByCajeroIdAndFechaMovimiento(cajeroId, fecha);

        if (sesiones.isEmpty()) {
            throw new RuntimeException(
                    "No se encontró una sesión del cajero " + cajeroId
                  + " con movimientos en la fecha " + fecha);
        }

        System.out.println("Sesiones encontradas: " + sesiones.size());

        // Si solo hay una sesión, generar informe normal
        if (sesiones.size() == 1) {
            System.out.println("Llamando a construirInforme con sesión única");
            return construirInforme(sesiones.get(0), fecha, token);
        }

        // Si hay múltiples sesiones, consolidar datos de todas
        System.out.println("Llamando a construirInformeConsolidado con múltiples sesiones");
        return construirInformeConsolidado(sesiones, fecha, token);
    }

    /**
     * Construye un informe consolidado combinando datos de múltiples sesiones
     * de un mismo cajero en la misma fecha.
     */
    private InformeDiarioVentasDTO construirInformeConsolidado(List<DetalleCajero> sesiones, LocalDate fechaInforme, String token) {
        // Usamos la sesión más reciente (primera en la lista, ya ordenada DESC) para datos de encabezado
        DetalleCajero sesionPrincipal = sesiones.get(0);

        // Recopilar todos los movimientos del día de todas las sesiones
        List<MovimientoCajero> todosMovimientosDia = new ArrayList<>();
        for (DetalleCajero sesion : sesiones) {
            List<MovimientoCajero> movs = movimientoRepo
                    .findByDetalleCajero_DetalleCajeroIdOrderByConsecutivoAsc(sesion.getDetalleCajeroId())
                    .stream()
                    .filter(m -> m.getFechaMovimiento() != null
                              && m.getFechaMovimiento().toLocalDate().equals(fechaInforme))
                    .collect(Collectors.toList());
            todosMovimientosDia.addAll(movs);
        }

        InformeDiarioVentasDTO dto = new InformeDiarioVentasDTO();

        // Encabezado con datos de la sesión principal
        buildEncabezado(dto, sesionPrincipal, todosMovimientosDia, fechaInforme);

        // Información de la empresa (tercero)
        buildInformacionEmpresa(dto, token);

        // Para las queries nativas, consolidamos datos de todas las sesiones
        List<Long> sesionIds = sesiones.stream()
                .map(DetalleCajero::getDetalleCajeroId)
                .collect(Collectors.toList());

        buildMovimientoCuentasConsolidado(dto, sesionIds, fechaInforme);
        buildVentasPorLineaConsolidado(dto, sesionIds, fechaInforme);
        buildFormasDePagoConsolidado(dto, sesionIds, fechaInforme);
        buildRecibosCaja(dto, todosMovimientosDia, sesionIds, fechaInforme);
        buildEgresos(dto, todosMovimientosDia, sesionIds, fechaInforme);
        buildVales(dto, todosMovimientosDia);
        buildTotalCxCConsolidado(dto, sesionIds, fechaInforme);
        buildDevolucionesConsolidado(dto, sesionIds, fechaInforme, todosMovimientosDia);
        buildResumenFinalConsolidado(dto, sesionIds, fechaInforme, todosMovimientosDia);

        return dto;
    }

    /**
     * Lógica común para construir el informe a partir de una sesión y una fecha.
     */
    private InformeDiarioVentasDTO construirInforme(DetalleCajero sesion, LocalDate fechaInforme, String token) {
        System.out.println("=== construirInforme INICIADO ===");

        Long detalleCajeroId = sesion.getDetalleCajeroId();

        // Cargar todos los movimientos de la sesión y filtrar solo los del día solicitado
        List<MovimientoCajero> todosMovimientos =
                movimientoRepo.findByDetalleCajero_DetalleCajeroIdOrderByConsecutivoAsc(detalleCajeroId);

        List<MovimientoCajero> movimientosDia = todosMovimientos.stream()
                .filter(m -> m.getFechaMovimiento() != null
                          && m.getFechaMovimiento().toLocalDate().equals(fechaInforme))
                .collect(Collectors.toList());

        InformeDiarioVentasDTO dto = new InformeDiarioVentasDTO();
        System.out.println("DTO creado: " + dto);

        buildEncabezado(dto, sesion, movimientosDia, fechaInforme);
        System.out.println("buildEncabezado completado");

        System.out.println("ANTES de buildInformacionEmpresa");
        buildInformacionEmpresa(dto, token);
        System.out.println("DESPUES de buildInformacionEmpresa");

        buildMovimientoCuentas(dto, detalleCajeroId, fechaInforme);
        buildVentasPorLinea(dto, detalleCajeroId, fechaInforme);
        buildFormasDePago(dto, detalleCajeroId, fechaInforme);
        buildRecibosCaja(dto, movimientosDia, List.of(detalleCajeroId), fechaInforme);
        buildEgresos(dto, movimientosDia, List.of(detalleCajeroId), fechaInforme);
        buildVales(dto, movimientosDia);
        buildTotalCxC(dto, detalleCajeroId, fechaInforme);
        buildDevoluciones(dto, detalleCajeroId, fechaInforme, movimientosDia);
        buildResumenFinal(dto, detalleCajeroId, fechaInforme, movimientosDia);

        return dto;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  1. ENCABEZADO
    // ════════════════════════════════════════════════════════════════════════
    private void buildEncabezado(InformeDiarioVentasDTO dto,
                                  DetalleCajero sesion,
                                  List<MovimientoCajero> movimientos,
                                  LocalDate fecha) {
        dto.setDetalleCajeroId(sesion.getDetalleCajeroId());
        dto.setCajeroId(sesion.getCajero().getCajeroId());
        dto.setCajeroNombre(sesion.getCajero().getNombre());
        dto.setFecha(fecha);
        dto.setHora(LocalDateTime.now(ZONA_BOGOTA).format(HORA_FMT));
        dto.setFechaApertura(sesion.getFechaApertura());
        dto.setFechaCierre(sesion.getFechaCierre());
        dto.setEstadoSesion(sesion.getEstado().name());

        int transInicial = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.VENTA)
                .mapToInt(MovimientoCajero::getConsecutivoTipo)
                .min().orElse(0);
        int transFinal = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.VENTA)
                .mapToInt(MovimientoCajero::getConsecutivoTipo)
                .max().orElse(0);
        long numVentas = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.VENTA)
                .count();

        dto.setTransaccionInicial(transInicial);
        dto.setTransaccionFinal(transFinal);
        dto.setNumeroTransacciones((int) numVentas);
        // Zeta = número de cierres acumulados del cajero (consecutivo de la sesión)
        dto.setZeta(sesion.getConsecutivo());

        // Resumen de movimientos agrupados por tipo
        List<InformeDiarioVentasDTO.MovimientoTipo> resumen = movimientos.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getTipoMovimiento().name(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(e -> new InformeDiarioVentasDTO.MovimientoTipo(e.getKey(), e.getValue().intValue()))
                .collect(Collectors.toList());
        dto.setResumenMovimientos(resumen);

        // Cuadre de caja: incluir datos declarados solo si la sesión está cerrada
        if (sesion.getEstado() == DetalleCajero.EstadoDetalleCajero.CERRADA) {
            BigDecimal efectivoEsperado = sesion.getBaseCaja().add(sesion.getTotalEfectivo());
            dto.setEfectivoEsperado(efectivoEsperado);
            dto.setEfectivoDeclarado(sesion.getEfectivoDeclarado());
            dto.setDiferenciaEfectivo(sesion.getDiferenciaEfectivo());
            dto.setMediosElectronicosDeclarado(sesion.getMediosElectronicosDeclarado());
            dto.setDiferenciaMediosElectronicos(sesion.getDiferenciaMediosElectronicos());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  INFORMACIÓN DE LA EMPRESA (TERCERO)
    // ════════════════════════════════════════════════════════════════════════
    private void buildInformacionEmpresa(InformeDiarioVentasDTO dto, String token) {
        try {
            System.out.println("=== INICIANDO buildInformacionEmpresa ===");

            if (token == null || token.isEmpty()) {
                System.err.println("Token vacío, no se puede obtener información de empresa");
                return;
            }

            // Extraer idsecion del claim JWT
            io.jsonwebtoken.Claims claims = jwcommon.extraerClaims(token);
            String idSesion = claims.get("idsecion", String.class);
            System.out.println("idsecion desde token: " + idSesion);

            if (idSesion == null || idSesion.isEmpty()) {
                System.err.println("No se encontró claim 'idsecion' en el token");
                return;
            }

            // Obtener datos de sesión desde Redis
            DatosSesiones datosSesion = redisTemplate.opsForValue().get(idSesion);
            System.out.println("DatosSesiones desde Redis: " + datosSesion);

            if (datosSesion == null) {
                System.err.println("No se encontraron datos de sesión en Redis para idsecion: " + idSesion);
                return;
            }

            // Obtener usuario usando el ID de la sesión
            Integer usuarioId = datosSesion.getIdusuario();
            System.out.println("Usuario ID desde sesión: " + usuarioId);

            Usuario usuario = usuarioRepo.findById(usuarioId).orElse(null);
            System.out.println("Usuario: " + usuario + ", ID: " + (usuario != null ? usuario.getCodigo() : "null"));

            if (usuario == null) {
                System.err.println("No se encontró usuario con ID: " + usuarioId);
                return;
            }

            // Obtener empresa (asumiendo que hay solo una empresa por tenant)
            Empresa empresa = empresaRepo.findAll().stream().findFirst().orElse(null);
            System.out.println("Empresa encontrada: " + empresa);

            if (empresa == null) {
                System.err.println("No se encontró empresa en el tenant");
                return;
            }

            // Obtener bodega del usuario
            String direccionFinal = null;
            String celularFinal = null;

            java.util.List<Usuariobodega> usuarioBodegas = usuariobodegaRepo.findByUsuarioid(usuario);
            System.out.println("Bodegas del usuario: " + (usuarioBodegas != null ? usuarioBodegas.size() : "null"));

            if (usuarioBodegas != null && !usuarioBodegas.isEmpty()) {
                Bodegas bodega = usuarioBodegas.get(0).getBodegaid();
                System.out.println("Bodega: " + bodega);
                if (bodega != null) {
                    // Usar dirección de la bodega si existe y no está vacía
                    if (bodega.getDireccion() != null && !bodega.getDireccion().trim().isEmpty()) {
                        direccionFinal = bodega.getDireccion();
                        System.out.println("Usando dirección de bodega: " + direccionFinal);
                    }
                    // Usar celular de la bodega si existe y no está vacío
                    if (bodega.getCelular() != null && !bodega.getCelular().trim().isEmpty()) {
                        celularFinal = bodega.getCelular();
                        System.out.println("Usando celular de bodega: " + celularFinal);
                    }
                }
            }

            // Fallback: usar dirección de la empresa si la bodega no tiene dirección
            if (direccionFinal == null || direccionFinal.trim().isEmpty()) {
                direccionFinal = empresa.getDireccion();
                System.out.println("Usando dirección de empresa (fallback): " + direccionFinal);
            }
            // Fallback: usar celular de la empresa si la bodega no tiene celular
            if (celularFinal == null || celularFinal.trim().isEmpty()) {
                celularFinal = empresa.getCelularempresa();
                System.out.println("Usando celular de empresa (fallback): " + celularFinal);
            }

            // Setear información de la empresa en el DTO
            dto.setRazonSocialEmpresa(empresa.getRazonsocial());
            dto.setDireccionEmpresa(direccionFinal);
            dto.setCelularEmpresa(celularFinal);
            dto.setDigitoVerificacionEmpresa(empresa.getDigitoverificacion());
            dto.setNumeroIdentificacionEmpresa(empresa.getNumeroidentificacion());

            System.out.println("Razón social: " + empresa.getRazonsocial());
            System.out.println("Dirección final: " + direccionFinal);
            System.out.println("Dígito verificación:" + empresa.getDigitoverificacion());
            System.out.println("Número identificación:" + empresa.getNumeroidentificacion());

            // Mapear regimen: cargar explícitamente del repositorio
            java.util.List<Regimen> regimenes = regimenRepo.findAll();
            if (!regimenes.isEmpty()) {
                Regimen regimen = regimenes.get(0);
                dto.setRegimenEmpresa(regimen.getDescripcion());
                System.out.println("Descripción regimen: " + regimen.getDescripcion());
            } else {
                System.out.println("No se encontraron regimenes en la base de datos");
            }

            // Mapear tipo de identificación a iniciales
            if (empresa.getCodigotipoidentificacion() != null) {
                Integer tipoIdCodigo = empresa.getCodigotipoidentificacion().getCodigo();
                String iniciales = TIPO_IDENTIFICACION_INICIALES.getOrDefault(tipoIdCodigo, "");
                dto.setTipoIdentificacionEmpresa(iniciales);
                System.out.println("Tipo identificación código: " + tipoIdCodigo + ", iniciales: " + iniciales);
            }

            System.out.println("=== buildInformacionEmpresa COMPLETADO ===");

        } catch (Exception e) {
            System.err.println("Error al construir información de empresa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  2. MOVIMIENTO DE CUENTAS
    // ════════════════════════════════════════════════════════════════════════
    private void buildMovimientoCuentas(InformeDiarioVentasDTO dto,
                                         Long detalleCajeroId, LocalDate fecha) {
        MovimientoCuentas mc = new MovimientoCuentas();
        Object[] row = informeDiarioRepo.getTotalesVentas(detalleCajeroId, fecha);

        if (row != null && row[0] != null) {
            BigDecimal bruta      = toBD(row[0]);
            BigDecimal gravada    = toBD(row[1]);
            BigDecimal iva        = toBD(row[2]);
            BigDecimal descuentos = toBD(row[3]);
            // Exentas = bruta − gravada − iva  (ventas sin IVA que no son gravadas)
            BigDecimal exentas = bruta.subtract(gravada).subtract(iva).max(BigDecimal.ZERO);

            mc.setTotalVentaBruta(bruta);
            mc.setTotalDescuentos(descuentos);
            mc.setTotalRetenciones(BigDecimal.ZERO);
            mc.setVentasGravadas(gravada);
            mc.setVentasExentas(exentas);
            mc.setTotalIVA(iva);
            mc.setTotalVentas(bruta.subtract(descuentos));
        }
        dto.setMovimientoCuentas(mc);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  3. VENTAS POR LÍNEA
    // ════════════════════════════════════════════════════════════════════════
    private void buildVentasPorLinea(InformeDiarioVentasDTO dto,
                                      Long detalleCajeroId, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getVentasPorLinea(detalleCajeroId, fecha);
        List<VentaLinea> lineas = new ArrayList<>();
        for (Object[] row : rows) {
            lineas.add(new VentaLinea(
                    row[0] != null ? row[0].toString() : "SIN LÍNEA",
                    toBD(row[1])));
        }
        dto.setVentasPorLinea(lineas);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  4. FORMAS DE PAGO
    // ════════════════════════════════════════════════════════════════════════
    private void buildFormasDePago(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getFormasPago(detalleCajeroId, fecha);
        List<FormaPago> formas = new ArrayList<>();
        for (Object[] row : rows) {
            formas.add(new FormaPago(
                    row[0] != null ? row[0].toString() : "OTRO",
                    toBD(row[1])));
        }
        dto.setFormasDePago(formas);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Documentos (recibos/egresos/ventas) anulados durante el día.
    //  Cuando se anula un recibo o egreso, NO se borra el MovimientoCajero
    //  original — solo se agrega uno nuevo de tipo ANULACION apuntando al
    //  mismo referencia_documento_id. Para que el informe no cuente dos veces,
    //  excluimos los IDs que tienen una ANULACION asociada.
    // ════════════════════════════════════════════════════════════════════════
    private Set<Long> idsDocumentosAnulados(List<MovimientoCajero> movimientos) {
        return movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.ANULACION
                          && m.getReferenciaDocumentoId() != null)
                .map(MovimientoCajero::getReferenciaDocumentoId)
                .collect(Collectors.toCollection(HashSet::new));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  5. RECIBOS DE CAJA  (RECIBO_CAJA)
    // ════════════════════════════════════════════════════════════════════════
    private void buildRecibosCaja(InformeDiarioVentasDTO dto,
                                   List<MovimientoCajero> movimientos,
                                   List<Long> sesionIds,
                                   LocalDate fecha) {
        SeccionRecibosCaja seccion = new SeccionRecibosCaja();
        Set<Long> anulados = idsDocumentosAnulados(movimientos);

        List<ReciboCaja> recibos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.RECIBO_CAJA)
                .filter(m -> m.getReferenciaDocumentoId() == null
                          || !anulados.contains(m.getReferenciaDocumentoId()))
                .map(m -> new ReciboCaja(
                        m.getTerceroNombre() != null ? m.getTerceroNombre() : "",
                        m.getMonto(),
                        m.getDescripcion()))
                .collect(Collectors.toList());
        seccion.setRecibos(recibos);

        BigDecimal totalRecibos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.RECIBO_CAJA)
                .filter(m -> m.getReferenciaDocumentoId() == null
                          || !anulados.contains(m.getReferenciaDocumentoId()))
                .map(MovimientoCajero::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        seccion.setTotalRecibosCaja(totalRecibos);

        // Desglose real por método de pago desde la tabla recibo_caja_medio_pago
        List<Object[]> formasPagoRows = informeDiarioRepo.getFormasPagoRecibosCaja(sesionIds, fecha);
        List<FormaPago> formasPago = new ArrayList<>();
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalElectronico = BigDecimal.ZERO;
        for (Object[] row : formasPagoRows) {
            String nombre = row[0] != null ? row[0].toString() : "OTRO";
            BigDecimal total = toBD(row[1]);
            formasPago.add(new FormaPago(nombre, total));
            if (nombre.toLowerCase().startsWith("ef")) {
                totalEfectivo = totalEfectivo.add(total);
            } else {
                totalElectronico = totalElectronico.add(total);
            }
        }
        seccion.setFormasPago(formasPago);
        seccion.setTotalEfectivo(totalEfectivo);
        seccion.setTotalTCredito(totalElectronico);

        dto.setRecibosCaja(seccion);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  6. COMPROBANTES DE EGRESO
    // ════════════════════════════════════════════════════════════════════════
    private void buildEgresos(InformeDiarioVentasDTO dto,
                               List<MovimientoCajero> movimientos,
                               List<Long> sesionIds,
                               LocalDate fecha) {
        SeccionEgresos seccion = new SeccionEgresos();
        Set<Long> anulados = idsDocumentosAnulados(movimientos);

        List<ComprobanteEgreso> egresos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.EGRESO)
                .filter(m -> m.getReferenciaDocumentoId() == null
                          || !anulados.contains(m.getReferenciaDocumentoId()))
                .map(m -> new ComprobanteEgreso(
                        m.getTerceroNombre() != null ? m.getTerceroNombre() : "",
                        m.getMonto(),
                        m.getDescripcion()))
                .collect(Collectors.toList());

        BigDecimal totalEgresos = egresos.stream()
                .map(ComprobanteEgreso::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        seccion.setEgresos(egresos);
        seccion.setTotalEgresos(totalEgresos);

        // Desglose real por método de pago desde la tabla comprobante_egreso_medio_pago
        List<Object[]> formasPagoRows = informeDiarioRepo.getFormasPagoEgresos(sesionIds, fecha);
        List<FormaPago> formasPago = new ArrayList<>();
        for (Object[] row : formasPagoRows) {
            String nombre = row[0] != null ? row[0].toString() : "OTRO";
            BigDecimal total = toBD(row[1]);
            formasPago.add(new FormaPago(nombre, total));
        }
        seccion.setFormasPago(formasPago);

        dto.setEgresos(seccion);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  7. VALES
    // ════════════════════════════════════════════════════════════════════════
    private void buildVales(InformeDiarioVentasDTO dto,
                             List<MovimientoCajero> movimientos) {
        // Se detectan como INGRESO_EFECTIVO con descripción que contiene "VALE"
        BigDecimal totalVales = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.INGRESO_EFECTIVO
                          && m.getDescripcion() != null
                          && m.getDescripcion().toUpperCase().contains("VALE"))
                .map(MovimientoCajero::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalVales(totalVales);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  7b. TOTAL CxC (Cuentas por Cobrar — ventas a crédito)
    // ════════════════════════════════════════════════════════════════════════
    private void buildTotalCxC(InformeDiarioVentasDTO dto,
                                Long detalleCajeroId, LocalDate fecha) {
        buildTotalCxCConsolidado(dto, List.of(detalleCajeroId), fecha);
    }

    private void buildTotalCxCConsolidado(InformeDiarioVentasDTO dto,
                                           List<Long> sesionIds, LocalDate fecha) {
        dto.setTotalCxC(informeDiarioRepo.getTotalCxCConsolidado(sesionIds, fecha));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  8. DEVOLUCIONES
    // ════════════════════════════════════════════════════════════════════════
    private void buildDevoluciones(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha,
                                    List<MovimientoCajero> movimientos) {
        buildDevolucionesConsolidado(dto, List.of(detalleCajeroId), fecha, movimientos);
    }

    private void buildDevolucionesConsolidado(InformeDiarioVentasDTO dto,
                                               List<Long> sesionIds, LocalDate fecha,
                                               List<MovimientoCajero> movimientos) {
        SeccionDevoluciones seccion = new SeccionDevoluciones();

        Object[] row = informeDiarioRepo.getTotalesDevolucionesConsolidado(sesionIds, fecha);
        if (row != null) {
            seccion.setDevGravada(toBD(row[0]));
            seccion.setIvaDevGravada(toBD(row[1]));
            seccion.setTotDevoluciones(toBD(row[2]));
        }

        // Total contado = efectivo restituido en devoluciones
        BigDecimal totalContado = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.DEVOLUCION)
                .map(MovimientoCajero::getMontoEfectivo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total medios electrónicos = tarjeta/transferencia restituidos en devoluciones
        BigDecimal totalMedElec = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientoCajero.TipoMovimiento.DEVOLUCION)
                .map(MovimientoCajero::getMontoElectronico)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        seccion.setDevExentas(BigDecimal.ZERO);
        seccion.setTotalContado(totalContado);
        seccion.setTotalMedElectronico(totalMedElec);
        seccion.setTotalDsc(BigDecimal.ZERO);
        dto.setDevoluciones(seccion);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  9. RESUMEN FINAL  (Neto Caja, UPT, VPT, VPU)
    // ════════════════════════════════════════════════════════════════════════
    private void buildResumenFinal(InformeDiarioVentasDTO dto,
                                    Long detalleCajeroId, LocalDate fecha,
                                    List<MovimientoCajero> movimientos) {
        buildResumenFinalConsolidado(dto, List.of(detalleCajeroId), fecha, movimientos);
    }

    private void buildResumenFinalConsolidado(InformeDiarioVentasDTO dto,
                                               List<Long> sesionIds, LocalDate fecha,
                                               List<MovimientoCajero> movimientos) {
        ResumenFinal resumen = new ResumenFinal();

        BigDecimal totalVentas  = dto.getMovimientoCuentas().getTotalVentas();
        BigDecimal totalEgresos = dto.getEgresos().getTotalEgresos();
        BigDecimal totalRecibos = dto.getRecibosCaja().getTotalRecibosCaja();
        BigDecimal totalDev     = dto.getDevoluciones().getTotDevoluciones();

        // NETO CAJA = TotalVentas − TotalEgresos + TotalRecibosCaja − TotDevoluciones
        resumen.setNetoCaja(totalVentas
                .subtract(totalEgresos)
                .add(totalRecibos)
                .subtract(totalDev));

        int numTransacciones = dto.getNumeroTransacciones() != null ? dto.getNumeroTransacciones() : 0;
        int totalUnidades    = informeDiarioRepo.getTotalUnidadesConsolidado(sesionIds, fecha);
        resumen.setTotalUnidades(totalUnidades);

        // UPT = (unidades / transacciones) * 100  → expresado como %
        if (numTransacciones > 0) {
            resumen.setUpt(BigDecimal.valueOf(totalUnidades)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(numTransacciones), 1, RoundingMode.HALF_UP));
        }
        // VPT = totalVentas / transacciones
        if (numTransacciones > 0) {
            resumen.setVpt(totalVentas
                    .divide(BigDecimal.valueOf(numTransacciones), 2, RoundingMode.HALF_UP));
        }
        // VPU = totalVentas / unidades
        if (totalUnidades > 0) {
            resumen.setVpu(totalVentas
                    .divide(BigDecimal.valueOf(totalUnidades), 3, RoundingMode.HALF_UP));
        }

        dto.setResumenFinal(resumen);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MÉTODOS CONSOLIDADOS PARA MOVIMIENTO DE CUENTAS, VENTAS POR LÍNEA
    //  Y FORMAS DE PAGO (múltiples sesiones)
    // ════════════════════════════════════════════════════════════════════════
    private void buildMovimientoCuentasConsolidado(InformeDiarioVentasDTO dto,
                                                    List<Long> sesionIds, LocalDate fecha) {
        MovimientoCuentas mc = new MovimientoCuentas();
        Object[] row = informeDiarioRepo.getTotalesVentasConsolidado(sesionIds, fecha);

        if (row != null && row[0] != null) {
            BigDecimal bruta      = toBD(row[0]);
            BigDecimal gravada    = toBD(row[1]);
            BigDecimal iva        = toBD(row[2]);
            BigDecimal descuentos = toBD(row[3]);
            BigDecimal exentas = bruta.subtract(gravada).subtract(iva).max(BigDecimal.ZERO);

            mc.setTotalVentaBruta(bruta);
            mc.setTotalDescuentos(descuentos);
            mc.setTotalRetenciones(BigDecimal.ZERO);
            mc.setVentasGravadas(gravada);
            mc.setVentasExentas(exentas);
            mc.setTotalIVA(iva);
            mc.setTotalVentas(bruta.subtract(descuentos));
        }
        dto.setMovimientoCuentas(mc);
    }

    private void buildVentasPorLineaConsolidado(InformeDiarioVentasDTO dto,
                                                  List<Long> sesionIds, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getVentasPorLineaConsolidado(sesionIds, fecha);
        List<VentaLinea> lineas = new ArrayList<>();
        for (Object[] row : rows) {
            lineas.add(new VentaLinea(
                    row[0] != null ? row[0].toString() : "SIN LÍNEA",
                    toBD(row[1])));
        }
        dto.setVentasPorLinea(lineas);
    }

    private void buildFormasDePagoConsolidado(InformeDiarioVentasDTO dto,
                                                List<Long> sesionIds, LocalDate fecha) {
        List<Object[]> rows = informeDiarioRepo.getFormasPagoConsolidado(sesionIds, fecha);
        List<FormaPago> formas = new ArrayList<>();
        for (Object[] row : rows) {
            formas.add(new FormaPago(
                    row[0] != null ? row[0].toString() : "OTRO",
                    toBD(row[1])));
        }
        dto.setFormasDePago(formas);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HELPER
    // ════════════════════════════════════════════════════════════════════════
    private BigDecimal toBD(Object value) {
        if (value == null)                return BigDecimal.ZERO;
        if (value instanceof BigDecimal)  return (BigDecimal) value;
        if (value instanceof Number)      return BigDecimal.valueOf(((Number) value).doubleValue());
        return BigDecimal.ZERO;
    }
}

