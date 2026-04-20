package com.pazzioliweb.productosmodule.service;

import java.util.ArrayList;
import java.util.List;

import com.pazzioliweb.productosmodule.dtos.*;
import com.pazzioliweb.productosmodule.repositori.ExistenciasRepository;
import com.pazzioliweb.productosmodule.repositori.PreciosProductoVarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.productosmodule.entity.ProductoVariante;
import com.pazzioliweb.productosmodule.entity.Productos;
import com.pazzioliweb.productosmodule.mapper.ProductoVarianteMapper;
import com.pazzioliweb.productosmodule.repositori.ProductoVarianteRepository;
import com.pazzioliweb.productosmodule.repositori.ProductosRepository;

import jakarta.persistence.EntityNotFoundException;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
public class ProductoVarianteServiceImpl implements ProductoVarianteService{
	
	private final ProductoVarianteRepository varianteRepository;
    private final ProductosRepository productosRepository;
    private final ProductoVarianteMapper mapper;
    private final ExistenciasRepository serviexistencia;
    @Autowired
    private PreciosProductoVarianteRepository repopreciova;
    public ProductoVarianteServiceImpl(
            ProductoVarianteRepository varianteRepository,
            ProductosRepository productosRepository,ProductoVarianteMapper mapper,
            ExistenciasRepository serviexistencia) {
        this.varianteRepository = varianteRepository;
        this.productosRepository = productosRepository;
        this.mapper = mapper;
        this.serviexistencia=serviexistencia;
    }

    @Override
    @Transactional
    public ProductoVarianteResponseDTO crear(ProductoVarianteCreateDTO dto) {

        Productos producto = productosRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        ProductoVariante variante = mapper.fromCreateDto(dto, producto);

        variante = varianteRepository.save(variante);

        return mapper.toResponseDto(variante);
    }

    @Override
    @Transactional
    public ProductoVarianteResponseDTO actualizar(Long id, ProductoVarianteUpdateDTO dto) {
    	
    	// 1. Verificar que la variante existe
        ProductoVariante existente = varianteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Variante no encontrada"));
        
     // 2. VALIDACIÓN: Código de barras repetido en otra variante
        if (dto.getCodigoBarras() != null) {
            boolean existeCodigoBarras = varianteRepository
                    .existsByCodigoBarrasAndProductoVarianteIdNot(dto.getCodigoBarras(), id);

            if (existeCodigoBarras) {
                throw new IllegalArgumentException(
                    "El código de barras ya está asignado a otra variante."
                );
            }
        }
        
        mapper.updateFromDto(dto, existente);

        varianteRepository.save(existente);

        return mapper.toResponseDto(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!varianteRepository.existsById(id)) {
            throw new EntityNotFoundException("La variante no existe");
        }
        varianteRepository.deleteById(id);
    }

    @Override
    public ProductoVariante buscarPorId(Long id) {
        return varianteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Variante no encontrada"));
    }

    @Override
    public Page<ProductoVarianteResponseDTO> listar(Pageable pageable) {

        Page<ProductoVariante> pagina = varianteRepository.traerProductosVariantes(pageable);

        return pagina.map(mapper::toResponseDto);
    }

    @Override
    public Page<ProductoVarianteResponseDTO> listarPorProducto(Integer productoId, Pageable pageable) {
    	Page<ProductoVariante> pagina = varianteRepository.findByProductoProductoId(productoId, pageable);
    	
        return pagina.map(mapper::toResponseDto); 
    }
    
    @Override
    public Page<ProductoInventarioDTO> listarInventarioBasico( String consultarentrada,int bodega,int activo,String estadoproduct,String productodes,Pageable pageable){
    	Page<ProductoInventarioDTO>  pagina;
    	if(consultarentrada.equals("NO")) {
    		 pagina=varianteRepository.listarInventario( activo,productodes,pageable);
    	}else {
    		pagina=varianteRepository.listarInventarioentradasalida(activo, bodega, productodes, pageable);
    	}
    	
    	return pagina;
    }

    @Override
    public List<ProductoInventarioDTO> listarInventarioBasicoPorDescripciones(List<String> descripciones, int estadova, String estadoproduct) {
        return varianteRepository.listarInventarioPorDescripciones(descripciones, estadova);
    }

    @Override
    public Page<ProductoVarianteConDetallesDTO> listarConDetallesPorProducto(Integer productoId, Pageable pageable){
        Page<ProductoVariante> variantes =
                varianteRepository.findByProductoProductoId(productoId, pageable);

        return variantes.map(variant -> {
             String sortDirection="ASC";
             String sortField="precioId";
            Sort sort = sortDirection.equalsIgnoreCase("asc")
                    ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();

            Pageable pageablepre= PageRequest.of(0, 20, sort);
            ProductoVarianteConDetallesDTO dto = new ProductoVarianteConDetallesDTO();
            dto.setProductoVarianteId(variant.getProductoVarianteId());
            dto.setSku(variant.getSku());
            dto.setReferenciaVariantes(variant.getReferenciaVariantes());
            dto.setCodigoBarras(variant.getCodigoBarras());
            dto.setActivo(variant.getActivo());
            dto.setImagen(variant.getImagen());
            List<Integer> idpre = new ArrayList<>(List.of(variant.getProductoVarianteId().intValue()));
            List<PreciosProductoVarianteDTO> precios=repopreciova.preciosProductoVarianteMultiple(idpre,pageablepre).getContent();
            List<ExistenciasBodegaDTO> e=serviexistencia.listadoExistenciasNombreBodegaVariante(variant.getProductoVarianteId());
            List<ProductoVarianteConDetallesDTO.existenciaDTO> existenciasDtos = new ArrayList<>();
             dto.setPrecios(precios);
            for(ExistenciasBodegaDTO exis: e) {
                ProductoVarianteConDetallesDTO.existenciaDTO ex= new ProductoVarianteConDetallesDTO.existenciaDTO();
                ex.setExistenciaId(exis.getBodegaId().longValue());
                ex.setBodega(exis.getBodega());
                ex.setCantidad(exis.getExistencia());
                existenciasDtos.add(ex);
            }
            // MAPEO de entidad → DTO
            List<ProductoVarianteConDetallesDTO.DetalleDTO> detalleDTOs =
                    variant.getDetalles().stream().map(det -> {

                         System.out.println("impresion out impresora get tipo"+det.getCaracteristica().getTipo());
                        ProductoVarianteConDetallesDTO.DetalleDTO d =
                                new ProductoVarianteConDetallesDTO.DetalleDTO();
                        d.setDetalleId(det.getProductoVariantesDetalleId());
                        d.setCaracteristicaId(det.getCaracteristica().getCaracteristicaId());
                        d.setCaracteristicaNombre(det.getCaracteristica().getNombre());
                        d.setTipo(det.getCaracteristica().getTipo()!=null ?  det.getCaracteristica().getTipo().getNombre():"descripcion");
                        return d;
                    }).toList();

            dto.setDetalles(detalleDTOs);
            dto.setExistencia(existenciasDtos);
            return dto;
        });
    }

    @Override
    public Page<ProductoVarianteConDetallesDTO> listarConDetallesPorCodigos(List<String> codigos, Pageable pageable) {
        Page<ProductoVariante> variantes = varianteRepository.findByCodigoBarrasIn(codigos, pageable);

        return variantes.map(variant -> {
            ProductoVarianteConDetallesDTO dto = new ProductoVarianteConDetallesDTO();
            dto.setProductoVarianteId(variant.getProductoVarianteId());
            dto.setSku(variant.getSku());
            dto.setReferenciaVariantes(variant.getReferenciaVariantes());
            dto.setCodigoBarras(variant.getCodigoBarras());
            dto.setActivo(variant.getActivo());
            dto.setImagen(variant.getImagen());
            List<ExistenciasBodegaDTO> e=serviexistencia.listadoExistenciasNombreBodegaVariante(variant.getProductoVarianteId());
            List<ProductoVarianteConDetallesDTO.existenciaDTO> existenciasDtos = new ArrayList<>();
            for(ExistenciasBodegaDTO exis: e) {
                ProductoVarianteConDetallesDTO.existenciaDTO ex= new ProductoVarianteConDetallesDTO.existenciaDTO();
                ex.setExistenciaId(exis.getBodegaId().longValue());
                ex.setBodega(exis.getBodega());
                ex.setCantidad(exis.getExistencia());
                existenciasDtos.add(ex);
            }

            // MAPEO de entidad → DTO
            List<ProductoVarianteConDetallesDTO.DetalleDTO> detalleDTOs =
                    variant.getDetalles().stream().map(det -> {
                        ProductoVarianteConDetallesDTO.DetalleDTO d =
                                new ProductoVarianteConDetallesDTO.DetalleDTO();
                        d.setDetalleId(det.getProductoVariantesDetalleId());
                        d.setCaracteristicaId(det.getCaracteristica().getCaracteristicaId());
                        d.setCaracteristicaNombre(det.getCaracteristica().getNombre());
                        d.setTipo(det.getCaracteristica().getTipo()!=null ?  det.getCaracteristica().getTipo().getNombre():"descripcion");
                        return d;
                    }).toList();

            dto.setDetalles(detalleDTOs);
            dto.setExistencia(existenciasDtos);
            return dto;
        });
    }
}
