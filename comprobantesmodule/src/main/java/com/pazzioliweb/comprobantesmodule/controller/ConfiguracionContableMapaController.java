package com.pazzioliweb.comprobantesmodule.controller;

import com.pazzioliweb.comprobantesmodule.entity.ConfiguracionContableMapa;
import com.pazzioliweb.comprobantesmodule.repositori.ConfiguracionContableMapaRepository;
import com.pazzioliweb.comprobantesmodule.service.ConfiguracionContableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/configuracion-contable")
public class ConfiguracionContableMapaController {

    private final ConfiguracionContableMapaRepository mapaRepo;

    // Mapa de clave → descripción legible para el frontend
    private static final Map<String, String> DESCRIPCIONES = new LinkedHashMap<>();
    static {
        DESCRIPCIONES.put("COD_CAJA_GENERAL",         "Caja general (entrada/salida de efectivo)");
        DESCRIPCIONES.put("COD_CXC_CLIENTES",         "Cuentas por cobrar - Clientes");
        DESCRIPCIONES.put("COD_INVENTARIOS",          "Inventarios (mercancías)");
        DESCRIPCIONES.put("COD_IVA_DESCONTABLE",      "IVA descontable (compras)");
        DESCRIPCIONES.put("COD_CXP_PROVEEDORES",      "Cuentas por pagar - Proveedores");
        DESCRIPCIONES.put("COD_IVA_GENERADO",         "IVA generado (ventas)");
        DESCRIPCIONES.put("COD_INGRESOS_VENTAS",      "Ingresos por ventas");
        DESCRIPCIONES.put("COD_GASTOS_GENERALES",     "Gastos generales");
        DESCRIPCIONES.put("COD_DEVOLUCION_VENTAS",    "Devoluciones en ventas");
        DESCRIPCIONES.put("COD_COSTO_VENTAS",         "Costo de ventas (COGS)");
        DESCRIPCIONES.put("COD_AJUSTE_ENTRADA_INV",   "Ajuste entrada inventario (sobrantes)");
        DESCRIPCIONES.put("COD_AJUSTE_SALIDA_INV",    "Ajuste salida inventario (pérdidas)");
        DESCRIPCIONES.put("COD_RETEFUENTE_PAGAR",     "Retención en la fuente por pagar");
        DESCRIPCIONES.put("COD_RETEIVA_PAGAR",        "Retención de IVA por pagar");
        DESCRIPCIONES.put("COD_RETEICA_PAGAR",        "Retención de ICA por pagar");
        DESCRIPCIONES.put("COD_RESULTADO_EJERCICIO",  "Resultado del ejercicio (cierre anual)");
        DESCRIPCIONES.put("COD_ANTICIPO_RETEFUENTE",  "Anticipo retefuente sufrida (activo)");
        DESCRIPCIONES.put("COD_ANTICIPO_RETEIVA",     "Anticipo reteIVA sufrida (activo)");
        DESCRIPCIONES.put("COD_ANTICIPO_RETEICA",     "Anticipo reteICA sufrida (activo)");
    }

    // Defaults del PUC colombiano
    private static final Map<String, String> DEFAULTS = new LinkedHashMap<>();
    static {
        DEFAULTS.put("COD_CAJA_GENERAL",         ConfiguracionContableService.COD_CAJA_GENERAL);
        DEFAULTS.put("COD_CXC_CLIENTES",         ConfiguracionContableService.COD_CXC_CLIENTES);
        DEFAULTS.put("COD_INVENTARIOS",          ConfiguracionContableService.COD_INVENTARIOS);
        DEFAULTS.put("COD_IVA_DESCONTABLE",      ConfiguracionContableService.COD_IVA_DESCONTABLE);
        DEFAULTS.put("COD_CXP_PROVEEDORES",      ConfiguracionContableService.COD_CXP_PROVEEDORES);
        DEFAULTS.put("COD_IVA_GENERADO",         ConfiguracionContableService.COD_IVA_GENERADO);
        DEFAULTS.put("COD_INGRESOS_VENTAS",      ConfiguracionContableService.COD_INGRESOS_VENTAS);
        DEFAULTS.put("COD_GASTOS_GENERALES",     ConfiguracionContableService.COD_GASTOS_GENERALES);
        DEFAULTS.put("COD_DEVOLUCION_VENTAS",    ConfiguracionContableService.COD_DEVOLUCION_VENTAS);
        DEFAULTS.put("COD_COSTO_VENTAS",         ConfiguracionContableService.COD_COSTO_VENTAS);
        DEFAULTS.put("COD_AJUSTE_ENTRADA_INV",   ConfiguracionContableService.COD_AJUSTE_ENTRADA_INV);
        DEFAULTS.put("COD_AJUSTE_SALIDA_INV",    ConfiguracionContableService.COD_AJUSTE_SALIDA_INV);
        DEFAULTS.put("COD_RETEFUENTE_PAGAR",     ConfiguracionContableService.COD_RETEFUENTE_PAGAR);
        DEFAULTS.put("COD_RETEIVA_PAGAR",        ConfiguracionContableService.COD_RETEIVA_PAGAR);
        DEFAULTS.put("COD_RETEICA_PAGAR",        ConfiguracionContableService.COD_RETEICA_PAGAR);
        DEFAULTS.put("COD_RESULTADO_EJERCICIO",  ConfiguracionContableService.COD_RESULTADO_EJERCICIO);
        DEFAULTS.put("COD_ANTICIPO_RETEFUENTE",  ConfiguracionContableService.COD_ANTICIPO_RETEFUENTE);
        DEFAULTS.put("COD_ANTICIPO_RETEIVA",     ConfiguracionContableService.COD_ANTICIPO_RETEIVA);
        DEFAULTS.put("COD_ANTICIPO_RETEICA",     ConfiguracionContableService.COD_ANTICIPO_RETEICA);
    }

    public ConfiguracionContableMapaController(ConfiguracionContableMapaRepository mapaRepo) {
        this.mapaRepo = mapaRepo;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> listar() {
        Map<String, String> overrides = new HashMap<>();
        mapaRepo.findAll().forEach(m -> overrides.put(m.getClave(), m.getCodigoCuenta()));

        List<Map<String, String>> result = new ArrayList<>();
        DEFAULTS.forEach((clave, defaultCod) -> {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("clave", clave);
            entry.put("descripcion", DESCRIPCIONES.getOrDefault(clave, clave));
            entry.put("codigoDefault", defaultCod);
            entry.put("codigoActual", overrides.getOrDefault(clave, defaultCod));
            entry.put("personalizado", overrides.containsKey(clave) ? "true" : "false");
            result.add(entry);
        });
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{clave}")
    public ResponseEntity<?> actualizar(@PathVariable String clave, @RequestBody Map<String, String> body) {
        String nuevoCodigo = body.get("codigoCuenta");
        if (nuevoCodigo == null || nuevoCodigo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "codigoCuenta es requerido"));
        }
        if (!DEFAULTS.containsKey(clave)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Clave no válida: " + clave));
        }
        ConfiguracionContableMapa mapa = mapaRepo.findByClave(clave)
                .orElse(new ConfiguracionContableMapa());
        mapa.setClave(clave);
        mapa.setCodigoCuenta(nuevoCodigo.trim());
        mapa.setDescripcion(DESCRIPCIONES.getOrDefault(clave, clave));
        mapaRepo.save(mapa);
        return ResponseEntity.ok(Map.of("mensaje", "Configuración actualizada correctamente"));
    }

    @DeleteMapping("/{clave}")
    public ResponseEntity<?> restaurarDefault(@PathVariable String clave) {
        mapaRepo.findByClave(clave).ifPresent(mapaRepo::delete);
        return ResponseEntity.ok(Map.of("mensaje", "Restaurado al código por defecto del PUC"));
    }
}
