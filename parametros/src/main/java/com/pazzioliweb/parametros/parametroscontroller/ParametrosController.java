package com.pazzioliweb.parametros.parametroscontroller;

import com.pazzioliweb.parametros.dtos.ParametroCreateDTO;
import com.pazzioliweb.parametros.entity.Parametros;
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
            @RequestParam String categoriacomprobante,
            @RequestParam String categoriaparametro) {
        List<Parametros> parametros = parametrosService.buscarPorCategorias(categoriacomprobante, categoriaparametro);
        return ResponseEntity.ok(parametros);
    }
}
