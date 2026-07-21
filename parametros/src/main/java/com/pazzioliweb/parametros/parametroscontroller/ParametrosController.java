package com.pazzioliweb.parametros.parametroscontroller;

import com.pazzioliweb.parametros.dtos.ComprobanteContableSimpleDTO;
import com.pazzioliweb.parametros.dtos.ParametroComprobanteResponseDTO;
import com.pazzioliweb.parametros.dtos.ParametroComprobanteUpdateDTO;
import com.pazzioliweb.parametros.dtos.ParametroCreateDTO;
import com.pazzioliweb.parametros.dtos.ParametroGlobalResponseDTO;
import com.pazzioliweb.parametros.dtos.ParametroGlobalUpdateDTO;
import com.pazzioliweb.parametros.entity.Parametros;
import com.pazzioliweb.parametros.entity.Parametroscomprobantes;
import com.pazzioliweb.parametros.entity.Parametrosglobales;
import com.pazzioliweb.parametros.service.ParametrosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametros")
public class ParametrosController {

    private final ParametrosService parametrosService;

    public ParametrosController(ParametrosService parametrosService) {
        this.parametrosService = parametrosService;
    }

    @PostMapping
    public ResponseEntity<Parametros> crearParametro(@RequestBody ParametroCreateDTO dto) {
        Parametros parametro = parametrosService.crearParametro(dto);
        return ResponseEntity.ok(parametro);
    }

    @GetMapping
    public ResponseEntity<List<Parametros>> buscarPorCategorias(
            @RequestParam(required = false) String categoriacomprobante,
            @RequestParam(required = false) String categoriaparametro) {
        List<Parametros> parametros = parametrosService.buscarPorCategorias(categoriacomprobante, categoriaparametro);
        return ResponseEntity.ok(parametros);
    }

    @GetMapping("/por-comprobante")
    public ResponseEntity<List<ParametroComprobanteResponseDTO>> obtenerParametrosPorComprobante(
            @RequestParam(required = false) String categoriacomprobante,
            @RequestParam(required = false) Long comprobante,
            @RequestParam(required = false) String categoriaparametro) {
        List<ParametroComprobanteResponseDTO> resultado = parametrosService.obtenerParametrosPorComprobante(categoriacomprobante, comprobante, categoriaparametro);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/comprobantes-por-categoria")
    public ResponseEntity<List<ComprobanteContableSimpleDTO>> obtenerComprobantesPorCategoria(
            @RequestParam String categoriaComprobante) {
        List<ComprobanteContableSimpleDTO> resultado = parametrosService.obtenerComprobantesPorCategoria(categoriaComprobante);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/globales")
    public ResponseEntity<List<ParametroGlobalResponseDTO>> obtenerParametrosGlobales(
            @RequestParam(required = false) String categoriaparametro,
            @RequestParam(required = false) String categoriacomprobante) {
        List<ParametroGlobalResponseDTO> resultado = parametrosService.obtenerParametrosGlobalesConJoin(categoriaparametro, categoriacomprobante);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/globales/{id}")
    public ResponseEntity<Parametrosglobales> actualizarParametroGlobal(
            @PathVariable Integer id,
            @RequestBody ParametroGlobalUpdateDTO dto) {
        Parametrosglobales actualizado = parametrosService.actualizarParametroGlobal(id, dto.getValor());
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/comprobantes/{id}")
    public ResponseEntity<Object> actualizarParametroComprobante(
            @PathVariable Integer id,
            @RequestBody ParametroComprobanteUpdateDTO dto) {
        Object actualizado = parametrosService.actualizarParametroComprobante(id, dto.getValor(), dto.getPrefijo());
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/globales/{id}")
    public ResponseEntity<Void> eliminarParametroGlobal(@PathVariable Integer id) {
        parametrosService.eliminarParametroGlobal(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comprobantes/{id}")
    public ResponseEntity<Void> eliminarParametroComprobante(
            @PathVariable Integer id,
            @RequestParam(required = false) String prefijo) {
        parametrosService.eliminarParametroComprobante(id, prefijo);
        return ResponseEntity.noContent().build();
    }
}
