package com.pazzioliweb.productosmodule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pazzioliweb.commonbacken.dtos.response.ApiResponse;
import com.pazzioliweb.productosmodule.dtos.ProductoMasterCreateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoMasterUpdateDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoResponseDTO;
import com.pazzioliweb.productosmodule.dtos.ProductoVarianteMasterUpdateDTO;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.mapper.ProductoMapper;
import com.pazzioliweb.productosmodule.service.ProductoMasterService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/productoMaster")
public class ProductoMasterController {

    private final ProductoMasterService productoMasterService;
    private final ProductoMapper productoMapper;

    public ProductoMasterController(ProductoMasterService productoMasterService,
                                    ProductoMapper productoMapper) {
        this.productoMasterService = productoMasterService;
        this.productoMapper = productoMapper;
    }

    @PostMapping("/crear")
    public ResponseEntity<ProductoResponseDTO> crearProductoMaster(
            @RequestBody ProductoMasterCreateDTO dto) {
    	
    	System.out.println("DTO PRODUCTO: " + dto.getProducto());
    	System.out.println("DTO PRODUCTO: " + dto.getProducto().getManejaVariantes());
    	System.out.println("DTO VARIANTES: " + dto.getVariantes().get(0));
    	//ProductoResponseDTO
        // Llamamos al service con los dos argumentos que requiere
        Productos producto = productoMasterService.crearProductoMaster(
                dto.getProducto(),      // ProductoCreateDTO
                dto.getVariantes()      // List<ProductoVarianteMasterDTO>
        );

        // Mapeo normal
        ProductoResponseDTO response = productoMapper.toResponseDto(producto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    	 //return ResponseEntity.status(HttpStatus.CREATED).body("algo");
    }
    
    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<ApiResponse<String>> eliminarProductoMaster(@PathVariable Integer productoId) {
        try {
            productoMasterService.eliminarProductoMaster(productoId);
            return ResponseEntity.ok(ApiResponse.success("Producto eliminado correctamente", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Error al eliminar el producto: " + e.getMessage()));
        }
    }
    
    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<?> actualizarProductoMaster(
            @PathVariable Integer productoId,
            @RequestBody ProductoMasterUpdateDTO dto) {
        try {

            Productos productoActualizado = productoMasterService.actualizarProductoMaster(
                    productoId,
                    dto.getProducto(),     // ProductoUpdateDTO
                    dto.getVariantes()     // List<ProductoVarianteMasterUpdateDTO>
            );

            ProductoResponseDTO response = productoMapper.toResponseDto(productoActualizado);

            return ResponseEntity.ok(ApiResponse.success("Producto actualizado correctamente", response));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage()));

        } catch (Exception e) {
        	System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Error al actualizar el producto: " + e.getMessage()));
        }
    }
}
