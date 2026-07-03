package com.pazzioliweb.tercerosmodule.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.repositorio.DepartamentoRepositori;
import com.pazzioliweb.commonbacken.repositorio.MunicipioRepositori;
import com.pazzioliweb.tercerosmodule.dtos.RutExtraidoDTO;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.dtos.TerceroResumenDTO;
import com.pazzioliweb.tercerosmodule.service.TercerosService;

import javax.xml.parsers.DocumentBuilderFactory;
@Component
@RestController
@RequestMapping("/api/terceros")
public class TercerosController {
    private final TercerosService terceroService;
    private final DepartamentoRepositori departamentoRepo;
    private final MunicipioRepositori municipioRepo;

    @Autowired
    public TercerosController(TercerosService terceroService,
                               DepartamentoRepositori departamentoRepo,
                               MunicipioRepositori municipioRepo) {
        this.terceroService = terceroService;
        this.departamentoRepo = departamentoRepo;
        this.municipioRepo = municipioRepo;
    }

    /*
     * Listado Basico para panel consulta terceros.
     *
     */
    @GetMapping("/listarTercerosBasicos")
    public ResponseEntity<Map<String, Object>> listarTodosBasico(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        System.out.println("metodo listar tercero");

        Page<TerceroResumenDTO> tercerosPage = terceroService.listarTerceroBasicos(page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/idtercero/{terceroid}")
    public TerceroDTOImpl obtenerporid(@PathVariable int terceroid){
        return terceroService.buscarconconctatoid(terceroid);

    }


    /*
     * Listado Completo para consulta de terceros, trae todo los detalles (paginado).
     *
     */
    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "razonSocial") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        System.out.println("Método listar terceros ejecutado");

        Map<String, Object> response = terceroService.listar(page, size, sortField, sortDirection);

        return ResponseEntity.ok(response);
    }

    /*
     * Listado de terceros basicos, por filtro que aplica para identificacion o razonSocial.
     *
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,

            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int tipoprovedor) {

        Page<TerceroResumenDTO> tercerosPage = terceroService.buscar(termino, tipoprovedor, page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/buscarconconcta")
    public ResponseEntity<Map<String, Object>> buscarconconacto(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,

            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int tipoprovedor) {

        Page<TerceroDTOImpl> tercerosPage = terceroService.buscarconconcta(termino, tipoprovedor, page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscarconconctanormal")
    public ResponseEntity<Map<String, Object>> buscarconconactonormal(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "terceroId") String sortField,

            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int tipoprovedor) {

        Page<TerceroDTOImpl> tercerosPage = terceroService.buscarconconctanormal(termino, tipoprovedor, page, size, sortField, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("content", tercerosPage.getContent());
        response.put("currentPage", tercerosPage.getNumber());
        response.put("totalItems", tercerosPage.getTotalElements());
        response.put("totalPages", tercerosPage.getTotalPages());

        return ResponseEntity.ok(response);
    }



    @PostMapping("/crear")
    public ResponseEntity<TerceroDTOImpl> crear(@RequestBody TerceroDTOImpl terceroDTO) {
        return ResponseEntity.ok(terceroService.guardar(terceroDTO));
    }


    @PutMapping("/actualizar/{TerceroId}")
    public ResponseEntity<TerceroDTOImpl> Actulizartercero(@PathVariable Integer TerceroId , @RequestBody TerceroDTOImpl terceroDTO) {
        return ResponseEntity.ok(terceroService.actualizar(TerceroId,terceroDTO));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        terceroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Integer id,
            @RequestParam String estado) {
        try {
            com.pazzioliweb.tercerosmodule.entity.EstadoTercero estadoEnum =
                com.pazzioliweb.tercerosmodule.entity.EstadoTercero.valueOf(estado.toUpperCase());
            terceroService.actualizarEstado(id, estadoEnum);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/aumentar-saldofavor-cliente")
    public ResponseEntity<Double> aumentarSaldofavorCliente(
            @PathVariable Integer id,
            @RequestBody com.pazzioliweb.tercerosmodule.dtos.MontoUpdateRequest request) {
        try {
            Double nuevoSaldo = terceroService.aumentarSaldofavorCliente(id, request.getMonto(), request.getSaldoAFavorUsado());
            return ResponseEntity.ok(nuevoSaldo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/saldofavor-cliente")
    public ResponseEntity<Double> obtenerSaldofavorCliente(@PathVariable Integer id) {
        try {
            Double saldo = terceroService.obtenerSaldofavorCliente(id);
            return ResponseEntity.ok(saldo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/aumentar-saldofavor-empresa")
    public ResponseEntity<Double> aumentarSaldofavorEmpresa(
            @PathVariable Integer id,
            @RequestBody com.pazzioliweb.tercerosmodule.dtos.MontoUpdateRequest request) {
        try {
            Double nuevoSaldo = terceroService.aumentarSaldofavorEmpresa(id, request.getMonto(), request.getSaldoAFavorUsado());
            return ResponseEntity.ok(nuevoSaldo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/saldofavor-empresa")
    public ResponseEntity<Double> obtenerSaldofavorEmpresa(@PathVariable Integer id) {
        try {
            Double saldo = terceroService.obtenerSaldofavorEmpresa(id);
            return ResponseEntity.ok(saldo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/extraer-rut")
    public ResponseEntity<?> extraerRut(@RequestParam("archivo") MultipartFile archivo) {
        String nombre = archivo.getOriginalFilename() != null
                        ? archivo.getOriginalFilename().toLowerCase() : "";
        try {
            if (nombre.endsWith(".xml")) {
                return ResponseEntity.ok(parsearXmlRut(archivo));
            } else if (nombre.endsWith(".pdf")) {
                return ResponseEntity.ok(parsearPdfRut(archivo));
            }
            return ResponseEntity.badRequest().body("Formato no soportado: solo PDF o XML");
        } catch (Exception e) {
            System.err.println("[extraer-rut] Error procesando RUT: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al procesar el RUT: " + e.getMessage());
        }
    }

    private RutExtraidoDTO parsearXmlRut(MultipartFile archivo) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        Document doc = factory.newDocumentBuilder().parse(archivo.getInputStream());
        doc.getDocumentElement().normalize();

        RutExtraidoDTO dto = new RutExtraidoDTO();

        String nit = getNode(doc, "NumeroIdentificacion");
        dto.setNit(nit.replaceAll("[^\\d]", ""));
        dto.setDv(getNode(doc, "DigitoVerificacion"));
        dto.setRazonSocial(getNode(doc, "RazonSocial"));
        dto.setDireccion(getNode(doc, "Direccion"));
        dto.setCodigoPostal(getNode(doc, "CodigoPostal"));
        dto.setTipoPersona(getNode(doc, "TipoPersona"));
        dto.setRegimen(getNode(doc, "CodigoRegimen"));

        String codTipoId = getNode(doc, "CodigoTipoIdentificacion");
        if (!codTipoId.isEmpty()) {
            try { dto.setCodigoTipoIdentificacion(Integer.parseInt(codTipoId)); } catch (NumberFormatException ignored) {}
        }

        String actividadStr = getNode(doc, "ActividadEconomica");
        if (!actividadStr.isEmpty()) {
            try { dto.setActividadEconomica(Integer.parseInt(actividadStr)); } catch (NumberFormatException ignored) {}
        }

        // Responsabilidades (retenciones) desde nodos XML
        List<Integer> retenciones = new ArrayList<>();
        NodeList respNodes = doc.getElementsByTagName("CodigoResponsabilidad");
        for (int i = 0; i < respNodes.getLength(); i++) {
            String cod = respNodes.item(i).getTextContent().trim().replaceAll("[^\\d]", "");
            try { retenciones.add(Integer.parseInt(cod)); } catch (NumberFormatException ignored) {}
        }
        if (!retenciones.isEmpty()) dto.setRetenciones(retenciones);

        return dto;
    }

    private RutExtraidoDTO parsearPdfRut(MultipartFile archivo) throws Exception {
        PDDocument pdf = PDDocument.load(archivo.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);
        String texto = stripper.getText(pdf);
        pdf.close();

        RutExtraidoDTO dto = new RutExtraidoDTO();

        // NIT + DV: dígitos con espacios entre ellos (ej: "9 0 0 6 8 9 8 9 9 6")
        // El último dígito es el DV, los anteriores son el NIT
        String nitArea = extraerConRegex(texto,
            "(?:N[uú]mero\\s+de\\s+Identificaci[oó]n[^\\n]*)[^\\d]{0,30}([\\d][\\d\\s\\.]{6,})");
        if (!nitArea.isEmpty()) {
            String digits = nitArea.replaceAll("[^\\d]", "");
            if (digits.length() >= 2) {
                dto.setNit(digits.substring(0, digits.length() - 1));
                dto.setDv(digits.substring(digits.length() - 1));
            }
        }

        // Razón Social (campo 35): salta líneas que sean etiquetas de campo (\d+\. texto)
        dto.setRazonSocial(extraerSiguienteLineaValida(texto,
            "35\\.\\s*Raz[oó]n\\s+[Ss]ocial|Raz[oó]n\\s+[Ss]ocial"));

        // Apellidos y nombres (personas naturales, campos 31-34)
        dto.setApellido1(extraerNombreCampo(texto, "31"));
        dto.setApellido2(extraerNombreCampo(texto, "32"));
        dto.setNombre1(extraerNombreCampo(texto, "33"));
        dto.setNombre2(extraerNombreCampo(texto, "34"));

        // Tipo de contribuyente (campo 24): Persona jurídica / natural
        String tipoPers = extraerSiguienteLineaValida(texto,
            "24\\.\\s*Tipo\\s+(?:de\\s+)?[Cc]ontribuyente");
        if (tipoPers.isEmpty())
            tipoPers = extraerSiguienteLineaValida(texto, "Tipo\\s+(?:de\\s+)?[Pp]ersona");
        if (!tipoPers.isEmpty()) {
            String tp = tipoPers.toLowerCase();
            if (tp.contains("jur")) dto.setTipoPersona("Juridica");
            else if (tp.contains("nat")) dto.setTipoPersona("Natural");
            else dto.setTipoPersona(tipoPers.trim());
        }

        // Tipo de documento (campo 25): 31=NIT, 13=CC, etc.
        String codTipoTexto = extraerSiguienteLineaValida(texto,
            "25\\.\\s*Tipo\\s+(?:de\\s+)?[Dd]ocumento|Tipo\\s+(?:de\\s+)?[Ii]dentificaci[oó]n");
        dto.setCodigoTipoIdentificacion(resolverCodigoTipoId(codTipoTexto, dto.getNit()));

        // Régimen
        dto.setRegimen(resolverRegimen(texto));

        // Dirección principal (campo 41): busca formato dirección colombiana primero
        dto.setDireccion(extraerDireccionColombia(texto));

        // Departamento y Ciudad: la línea de datos de UBICACION contiene todo junto
        // ("COLOMBIA 1 6 9 Antioquia 0 5 Medellín 0 0 1"), se parsea por tokens de letras
        extraerUbicacionEnBD(texto, dto);

        // Código postal (campo 43)
        String cp = extraerConRegex(texto, "43\\.\\s*C[oó]digo\\s+[Pp]ostal[^\\n]*?\\n?\\s*(\\d{4,6})");
        if (cp.isEmpty())
            cp = extraerConRegex(texto, "[Cc][oó]digo\\s+[Pp]ostal[:\\s]*(\\d{4,6})");
        dto.setCodigoPostal(cp);

        // Actividad económica (campo 46): toma los primeros 4 dígitos de la línea de datos
        String actLine = extraerConRegex(texto, "46\\.\\s*C[oó]digo[^\\n]*\\n([^\\n]{0,80})");
        if (!actLine.isEmpty()) {
            String actDigits = actLine.replaceAll("[^\\d]", "");
            if (actDigits.length() >= 4) {
                try { dto.setActividadEconomica(Integer.parseInt(actDigits.substring(0, 4))); }
                catch (NumberFormatException ignored) {}
            }
        }

        // Retenciones / responsabilidades DIAN
        dto.setRetenciones(extraerCodigosResponsabilidad(texto));

        return dto;
    }

    /**
     * Tras encontrar la etiqueta indicada, recorre las líneas siguientes y devuelve
     * la primera que no esté vacía y no sea una etiqueta de campo DIAN (ej: "31. Primer apellido").
     */
    private String extraerSiguienteLineaValida(String texto, String patronEtiqueta) {
        // Envuelve el patrón en (?:...) para que el | interno no rompa el grupo capturador
        Pattern p = Pattern.compile("(?:" + patronEtiqueta + ")[^\\n]*\\n([\\s\\S]{0,600})",
            Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(texto);
        if (!m.find()) return "";
        String capturado = m.group(1);
        if (capturado == null) return "";
        for (String linea : capturado.split("\\n")) {
            linea = linea.trim();
            if (!linea.isEmpty() && !linea.matches("^\\d+\\.\\s+.*")) {
                return linea;
            }
        }
        return "";
    }

    /**
     * Extrae el valor de un campo de nombre/apellido (31-34) saltando la propia etiqueta.
     * Para persona jurídica estos campos estarán vacíos.
     */
    private String extraerNombreCampo(String texto, String numeroCampo) {
        String after = extraerConRegex(texto,
            numeroCampo + "\\.\\s*[^\\n]*\\n([\\s\\S]{0,200})");
        if (after.isEmpty()) return "";
        for (String linea : after.split("\\n")) {
            linea = linea.trim();
            if (!linea.isEmpty() && !linea.matches("^\\d+\\.\\s+.*")) {
                return linea;
            }
        }
        return "";
    }

    /**
     * Busca la dirección en formato colombiano (CR, CL, AV, DG, TV…).
     * Excluye \r\n del grupo capturador para evitar trailing newlines.
     */
    private String extraerDireccionColombia(String texto) {
        String dir = extraerConRegex(texto,
            "\\b((?:CR|CRA|KR|CL|CLL|AV|AK|DG|TV|AC|CALLE|CARRERA|AVENIDA|TRANSVERSAL|DIAGONAL|AUTOPISTA|CIRCULAR|VRD|VIA)[ \\t][A-Z\\d][A-Z\\d \\t#°\\-\\.]{3,70})");
        if (!dir.isEmpty()) return dir.trim();
        String after = extraerSiguienteLineaValida(texto, "41\\.\\s*Direcci[oó]n");
        if (!after.isEmpty()) return after;
        return extraerSiguienteLineaValida(texto, "Direcci[oó]n\\s+[Pp]rincipal");
    }

    /**
     * Parsea la línea de UBICACION del RUT ("COLOMBIA 1 6 9 Antioquia 0 5 Medellín 0 0 1")
     * dividiendo por secuencias de dígitos para aislar los nombres de texto,
     * luego los busca en la BD para obtener los IDs de departamento y ciudad.
     */
    private void extraerUbicacionEnBD(String texto, RutExtraidoDTO dto) {
        // Captura la línea de datos bajo el label "38. País" o "39. Departamento"
        String linea = extraerConRegex(texto,
            "(?:38\\.\\s*Pa[íi]s)[^\\n]*\\n([^\\n\\r]{5,200})");
        if (linea.isEmpty())
            linea = extraerConRegex(texto,
                "(?:39\\.\\s*Departamento)[^\\n]*\\n([^\\n\\r]{5,200})");
        if (linea.isEmpty()) return;

        // Divide por grupos de dígitos (con espacios entre ellos) → tokens de texto puro
        // Ej: "COLOMBIA 1 6 9 Antioquia 0 5 Medellín 0 0 1" → ["COLOMBIA","Antioquia","Medellín",""]
        String[] tokens = linea.trim().split("\\s*\\d[\\d\\s]*");

        Departamento deptoEncontrado = null;
        for (String token : tokens) {
            String t = token.trim();
            if (t.length() < 3) continue;
            deptoEncontrado = departamentoRepo
                .findFirstByDepartamentoContainingIgnoreCase(t).orElse(null);
            if (deptoEncontrado != null) {
                dto.setDepartamentoId(deptoEncontrado.getCodigo());
                break;
            }
        }
        if (deptoEncontrado == null) return;

        final int codDepto = deptoEncontrado.getCodigoDepartamento();
        for (String token : tokens) {
            String t = token.trim();
            if (t.length() < 2) continue;
            Municipio muni = municipioRepo
                .findFirstByMunicipioContainingIgnoreCaseAndCodigoDepartamento(t, codDepto)
                .or(() -> municipioRepo.findFirstByMunicipioContainingIgnoreCase(t))
                .orElse(null);
            if (muni != null) {
                dto.setCiudadId(muni.getCodigo());
                break;
            }
        }
    }

    /**
     * Mapea el texto de tipo de identificación al código DIAN:
     * 31=NIT, 13=Cédula ciudadanía, 22=Cédula extranjería, 11=Registro civil, 12=Tarjeta identidad, 41=Pasaporte
     */
    private Integer resolverCodigoTipoId(String texto, String nit) {
        if (texto != null && !texto.isEmpty()) {
            String t = texto.toLowerCase();
            if (t.contains("31") || t.contains("nit")) return 31;
            if (t.contains("13") || t.contains("cédula de ciudadan")) return 13;
            if (t.contains("22") || t.contains("cédula de extranjer")) return 22;
            if (t.contains("11") || t.contains("registro civil")) return 11;
            if (t.contains("12") || t.contains("tarjeta de identidad")) return 12;
            if (t.contains("41") || t.contains("pasaporte")) return 41;
            try { return Integer.parseInt(texto.trim()); } catch (NumberFormatException ignored) {}
        }
        // Si hay NIT asumimos código 31
        return (nit != null && !nit.isEmpty()) ? 31 : null;
    }

    /**
     * Determina el código de régimen según las responsabilidades DIAN presentes:
     * 48 = Responsable del IVA (antes régimen común), 12 = No responsable de IVA (antes simplificado)
     */
    private String resolverRegimen(String texto) {
        // Códigos nuevos DIAN: 48=responsable IVA, 49=no responsable
        if (Pattern.compile("\\b48\\s*[-–]").matcher(texto).find()) return "48";
        if (Pattern.compile("\\b49\\s*[-–]").matcher(texto).find()) return "49";
        // Códigos antiguos DIAN: 11=ventas régimen común (responsable IVA), 12=régimen simplificado
        if (Pattern.compile("\\b11\\s*[-–]").matcher(texto).find()) return "11";
        if (Pattern.compile("\\b12\\s*[-–]").matcher(texto).find()) return "12";
        // Fallback: código solo como palabra completa
        if (Pattern.compile("\\b48\\b").matcher(texto).find()) return "48";
        if (Pattern.compile("\\b11\\b").matcher(texto).find()) return "11";
        if (Pattern.compile("\\b12\\b").matcher(texto).find()) return "12";
        String r = extraerConRegex(texto, "(?:R[eé]gimen)[:\\s]+([^\\n\\r]+)");
        return r.trim();
    }

    /**
     * Extrae los códigos de responsabilidad DIAN del texto del PDF.
     * Formato típico en el RUT: "05-Agente de retención en el impuesto sobre las ventas"
     * Códigos de retención principales: 05, 06, 07
     */
    private List<Integer> extraerCodigosResponsabilidad(String texto) {
        Set<Integer> codigos = new LinkedHashSet<>();

        // Patrón principal: código seguido de guión y texto descriptivo
        Matcher m = Pattern.compile("\\b(\\d{2})\\s*[-–]\\s*[A-ZÁÉÍÓÚa-záéíóú]")
            .matcher(texto);
        while (m.find()) {
            try {
                int cod = Integer.parseInt(m.group(1));
                if (cod >= 5 && cod <= 99) codigos.add(cod);
            } catch (NumberFormatException ignored) {}
        }

        // Fallback: buscar dentro de la sección de responsabilidades
        if (codigos.isEmpty()) {
            String seccion = extraerConRegex(texto,
                "(?:Responsabilidades[^\\n]*|RESPONSABILIDADES[^\\n]*)\\n([\\s\\S]{10,600})");
            if (!seccion.isEmpty()) {
                Matcher m2 = Pattern.compile("\\b(\\d{2})\\b").matcher(seccion);
                while (m2.find()) {
                    try {
                        int cod = Integer.parseInt(m2.group(1));
                        if (cod >= 5 && cod <= 99) codigos.add(cod);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        return new ArrayList<>(codigos);
    }

    private String getNode(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent().trim() : "";
    }

    private String extraerConRegex(String texto, String patron) {
        Matcher matcher = Pattern.compile(patron, Pattern.CASE_INSENSITIVE).matcher(texto);
        if (!matcher.find()) return "";
        String g = matcher.group(1);
        return g != null ? g.trim() : "";
    }
}
