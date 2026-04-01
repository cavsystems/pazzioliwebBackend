package com.pazzioliweb.productosmodule.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pazzioliweb.commonbacken.dtos.response.PaginationResponse;
import com.pazzioliweb.productosmodule.dtos.ProductoInventarioDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteConDetallesDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteUpdateDTO;
import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import com.pazzioliweb.productosmodule.service.ProductoVarianteService;

@RestController
@RequestMapping("/api/variantes")
public class ProductoVarianteController {
	private final ProductoVarianteRepository varianteRepository;
    private final ProductoVarianteService varianteService;

    public ProductoVarianteController(ProductoVarianteService varianteService,ProductoVarianteRepository varianteRepository) {
        this.varianteRepository = varianteRepository;
		this.varianteService = varianteService;
    }

    // -------------------------------------------------------
    // CRUD BÁSICO
    // -------------------------------------------------------

    @PostMapping("/crear-por-dto")
    public ResponseEntity<ProductoVarianteResponseDTO> crear(
            @RequestBody ProductoVarianteCreateDTO dto) {
        return ResponseEntity.ok(varianteService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoVarianteResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoVarianteUpdateDTO dto
    ) {
        return ResponseEntity.ok(varianteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        varianteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoVariante> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(varianteService.buscarPorId(id));
    }

    // -------------------------------------------------------
    // LISTADO GENERAL (CON ESTÁNDAR DE PAGINACIÓN)
    // -------------------------------------------------------

    @GetMapping("/listar")
    public ResponseEntity<PaginationResponse<ProductoVarianteResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productoVarianteId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoVarianteResponseDTO> resultado =
                varianteService.listar(pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    // -------------------------------------------------------
    // LISTADO POR PRODUCTO (MISMO ESTÁNDAR)
    // -------------------------------------------------------

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<PaginationResponse<ProductoVarianteResponseDTO>> listarPorProducto(
            @PathVariable Integer productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productoVarianteId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoVarianteResponseDTO> resultado =
                varianteService.listarPorProducto(productoId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
    
    @GetMapping("/listarInventarioBasico")
    public ResponseEntity<PaginationResponse<ProductoInventarioDTO>> listarInventarioBasico(
        	@RequestParam(defaultValue = "0") int page,
        	@RequestParam(defaultValue = "10") int size,
           	@RequestParam(defaultValue = "") String descripproduct,
        	@RequestParam(defaultValue = "ACTIVO") String estadoproducto,
        	@RequestParam(defaultValue ="1") String estadova,
          
        	@RequestParam(defaultValue = "varianteId") String sortField,
        	@RequestParam(defaultValue = "asc") String sortDirection
    ){
    	System.out.println("pagina actual es esta"+page+" "+estadoproducto+" "+estadova);
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoInventarioDTO> resultado =
                varianteService.listarInventarioBasico("NO",0,Integer.parseInt(estadova),"ACTIVO",descripproduct,pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
        
        
    }
    
    
    @GetMapping("/listarInventarioBasicoentra")
    public ResponseEntity<PaginationResponse<ProductoInventarioDTO>> listarInventarioBasicoentrada(
    		 @RequestParam(name = "page", defaultValue = "0") int page,
    		    @RequestParam(name = "size", defaultValue = "10") int size,
    		    @RequestParam(name = "descripproduct", defaultValue = "") String descripproduct,
    		    @RequestParam(name = "estadoproducto", defaultValue = "ACTIVO") String estadoproducto,
    		    @RequestParam(name = "estadova", defaultValue = "1") String estadova,
    		    @RequestParam(name = "bodega", defaultValue = "0") String bodega,
    		    @RequestParam(name = "consultarentradasalida", defaultValue = "NO") String consultarentradasalida,
    		    @RequestParam(name = "sortField", defaultValue = "varianteId") String sortField,
    		    @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection
    ){
    	System.out.println("pagina actual es esta"+page+" "+estadoproducto+" "+estadova);
    	
    	Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoInventarioDTO> resultado =
                varianteService.listarInventarioBasico(consultarentradasalida,Integer.parseInt(bodega),Integer.parseInt(estadova),estadoproducto,descripproduct,pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
    
    @GetMapping("/detalles-producto/{productoId}")
    public ResponseEntity<PaginationResponse<ProductoVarianteConDetallesDTO>> listarConDetallesPorProducto(
            @PathVariable Integer productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productoVarianteId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoVarianteConDetallesDTO> resultado =
                varianteService.listarConDetallesPorProducto(productoId, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }

    @GetMapping("listar/listarInventarioBasicoPorDescripciones")
    public ResponseEntity<List<ProductoInventarioDTO>> listarInventarioBasicoPorDescripciones(
            @RequestParam List<String> descripciones,
            @RequestParam(defaultValue = "1") int estadova,
            @RequestParam(defaultValue = "ACTIVO") String estadoproducto
    ){
        List<ProductoInventarioDTO> resultado = varianteService.listarInventarioBasicoPorDescripciones(descripciones, estadova, estadoproducto);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/detalles-codigos/{codigos}")
    public ResponseEntity<PaginationResponse<ProductoVarianteConDetallesDTO>> listarConDetallesPorCodigos(
            @PathVariable String codigos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productoVarianteId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {

        List<String> codigosList = Arrays.asList(codigos.split(","));
        System.out.println("codigos" + codigosList);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductoVarianteConDetallesDTO> resultado =
                varianteService.listarConDetallesPorCodigos(codigosList, pageable);

        return ResponseEntity.ok(PaginationResponse.of(resultado));
    }
    
    @GetMapping("/existecodigobarra")
    public ResponseEntity<Boolean> existecodigobarras(
            @RequestParam(defaultValue = "") String codigobarra) {
    	System.out.println("barra"+codigobarra);
    	Optional<ProductoVariante> opvariante=varianteRepository.findByCodigoBarras(codigobarra);
    	System.out.println("barra"+codigobarra+opvariante.isPresent()+opvariante.isEmpty());
    	if(opvariante.isPresent()) {
    		return ResponseEntity.ok(false);
    	}else {
    		return ResponseEntity.ok(true);
    	
    	
    }
    }

}

