package com.pazzioliweb.tercerosmodule.repositori;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pazzioliweb.tercerosmodule.dtos.SaldoTerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDtoresponse;
import com.pazzioliweb.tercerosmodule.dtos.TerceroResumenDTO;
import com.pazzioliweb.tercerosmodule.entity.Terceros;

@Repository
public interface TercerosRepository extends JpaRepository<Terceros, Integer>{
	Optional<Terceros> findByTerceroId(int terceroid);
	Optional<Terceros> findByIdentificacion(String identificacion);

	@Query("""
		    SELECT 
		        t.terceroId AS terceroId,
		        t.identificacion AS identificacion,
		        t.dv AS dv,
		        t.nombre1 AS nombre1,
		        t.nombre2 AS nombre2,
		        t.apellido1 AS apellido1,
		        t.apellido2 AS apellido2,
		        t.razonSocial AS razonSocial,
		        t.direccion AS direccion,
		        t.plazo AS plazo,
		        t.cupo AS cupo,
		        ti AS tipoIdentificacion,
		        c AS clasificacionTercero,
		        p AS precio,
		        r AS regimen
		    FROM Terceros t
		    LEFT JOIN t.tipoIdentificacion ti
		    LEFT JOIN t.clasificacionTercero c
		    LEFT JOIN t.precio p
		    LEFT JOIN t.regimen r
		""")
	Page<TerceroDTO> traerTerceros(Pageable pageable);

	@Query("""
		    SELECT 
		        t.terceroId AS terceroId,
		        t.identificacion AS identificacion,
		        t.dv AS dv,
		        t.nombre1 AS nombre1,
		        t.nombre2 AS nombre2,
		        t.apellido1 AS apellido1,
		        t.apellido2 AS apellido2,
		        t.razonSocial AS razonSocial,
		        t.direccion AS direccion,
		        t.plazo AS plazo,
		        t.cupo AS cupo,
		        ti AS tipoIdentificacion,
		        c AS clasificacionTercero,
		        p AS precio,
		        r AS regimen
		    FROM Terceros t
		    LEFT JOIN t.tipoIdentificacion ti
		    LEFT JOIN t.clasificacionTercero c
		    LEFT JOIN t.precio p
		    LEFT JOIN t.regimen r
		    WHERE t.identificacion LIKE %:busqueda%
		       OR t.razonSocial LIKE %:busqueda%
		""")
	Page<TerceroDTO> traerTercerosXFiltro(@Param("busqueda") String busqueda, Pageable pageable);


	@Query("""
			SELECT t
			FROM Terceros t
			LEFT JOIN t.regimen r
			WHERE (LOWER(t.identificacion) LIKE LOWER(:busqueda)
			   OR LOWER(t.razonSocial) LIKE LOWER(:busqueda))
			AND t.clasificacionTercero.clasificacionTerceroId = :tipousuario
			""")
	Page<Terceros> traerTercerosXFiltropro(@Param("busqueda") String busqueda,@Param("tipousuario") int tipousuario, Pageable pageable);


	@Query("""
			SELECT t
			FROM Terceros t
			LEFT JOIN t.regimen r
			WHERE (LOWER(t.identificacion) LIKE LOWER(:busqueda)
			   OR LOWER(t.razonSocial) LIKE LOWER(:busqueda))

			""")
	Page<Terceros> traerTercerosXFiltropronormal(@Param("busqueda") String busqueda, Pageable pageable);

	// Excluye una clasificación específica (ej: excluir proveedores de la búsqueda de clientes en ventas)
	@Query("""
			SELECT t
			FROM Terceros t
			LEFT JOIN t.regimen r
			WHERE (LOWER(t.identificacion) LIKE LOWER(:busqueda)
			   OR LOWER(t.razonSocial) LIKE LOWER(:busqueda))
			AND t.clasificacionTercero.clasificacionTerceroId != :tipousuario
			""")
	Page<Terceros> traerTercerosXFiltroExcluir(@Param("busqueda") String busqueda, @Param("tipousuario") int tipousuario, Pageable pageable);


	@Query("""
			SELECT t
			FROM Terceros t
			LEFT JOIN t.regimen r
			WHERE t.terceroId=:codigo
			
			""")
	Optional<Terceros> traerTercerosXFiltroid(@Param("codigo") int id);

	@Query("""
        SELECT DISTINCT t
        FROM Terceros t
        LEFT JOIN FETCH t.tipoIdentificacion
        LEFT JOIN FETCH t.clasificacionTercero
        LEFT JOIN FETCH t.precio
        LEFT JOIN FETCH t.regimen
        LEFT JOIN FETCH t.retenciones r
        LEFT JOIN FETCH t.contactos
        LEFT JOIN FETCH t.sedes
        """)
	Page<Terceros> findAllWithAllRelations(Pageable pageable);

	@Query("""
        SELECT DISTINCT t
        FROM Terceros t
        LEFT JOIN FETCH t.tipoIdentificacion
        LEFT JOIN FETCH t.clasificacionTercero
        LEFT JOIN FETCH t.precio
        LEFT JOIN FETCH t.regimen
        LEFT JOIN FETCH t.retenciones
        LEFT JOIN FETCH t.contactos
        LEFT JOIN FETCH t.sedes
        WHERE t.identificacion LIKE %:busqueda%
           OR t.razonSocial LIKE %:busqueda%
        """)
	Page<Terceros> findByBusquedaWithAllRelations(@Param("busqueda") String busqueda, Pageable pageable);

	@Query("""
    		SELECT new com.pazzioliweb.tercerosmodule.dtos.TerceroResumenDTO(
    	        t.terceroId,
    	        t.identificacion,
    	        t.razonSocial,
    	        ti.tipoIdentificacion,
    	        c.nombre,
    	        r.descripcion
    	        
    	    )
    	    FROM Terceros t
    	    LEFT JOIN t.tipoIdentificacion ti
    	    LEFT JOIN t.clasificacionTercero c
    	    LEFT JOIN t.regimen r
    		WHERE LOWER(t.identificacion) LIKE LOWER(:busqueda)
    		OR LOWER(t.razonSocial) LIKE LOWER(:busqueda)
    		""")
	Page<TerceroResumenDTO> buscarPorIdentificacionORazonSocial(@Param("busqueda") String busqueda, Pageable pageable);


	@Query("""
    		SELECT new com.pazzioliweb.tercerosmodule.dtos.TerceroResumenDTO(
    	        t.terceroId,
    	        t.identificacion,
    	        t.razonSocial,
    	        ti.tipoIdentificacion,
    	        c.nombre,
    	        r.descripcion
    	    )
    	    FROM Terceros t
    	    LEFT JOIN t.tipoIdentificacion ti
    	    LEFT JOIN t.clasificacionTercero c
    	    LEFT JOIN t.regimen r
    		WHERE (LOWER(t.identificacion) LIKE LOWER(:busqueda)
    		OR LOWER(t.razonSocial) LIKE LOWER(:busqueda)) and c.clasificacionTerceroId=:tipousuario
    		""")
	Page<TerceroResumenDTO> buscarPorIdentificacionORazonSocialTipo(@Param("busqueda") String busqueda,@Param("tipousuario") int tipousuario, Pageable pageable);

	@Query("""
    	    SELECT DISTINCT t
    	    FROM Terceros t
    	    LEFT JOIN FETCH t.tipoIdentificacion ti
    	    LEFT JOIN FETCH t.clasificacionTercero c
    	    LEFT JOIN FETCH t.precio p
    	    LEFT JOIN FETCH t.regimen r
    	    LEFT JOIN FETCH t.retenciones rt
    	    LEFT JOIN FETCH t.contactos con
    	    LEFT JOIN FETCH t.sedes s
    	    LEFT JOIN FETCH s.departamento d
    		LEFT JOIN FETCH s.municipio m
    		LEFT JOIN FETCH t.tipoPersona tp
    	    """)
	List<Terceros> traerTercerosConDetalles();

	@Query("""
    	    SELECT new com.pazzioliweb.tercerosmodule.dtos.TerceroResumenDTO(
    	        t.terceroId,
    	        t.identificacion,
    	        t.razonSocial,
    	        ti.tipoIdentificacion,
    	        c.nombre,
    	        r.descripcion
    	    )
    	    FROM Terceros t
    	    LEFT JOIN t.tipoIdentificacion ti
    	    LEFT JOIN t.clasificacionTercero c
    	    LEFT JOIN t.regimen r
    	""")
	Page<TerceroResumenDTO> listarTercerosBasicos(Pageable pageable);

	@Query("""
    	    SELECT t
    	    FROM Terceros t
    	    LEFT JOIN FETCH t.tipoIdentificacion ti
    	    LEFT JOIN FETCH t.clasificacionTercero c
    	    LEFT JOIN FETCH t.precio p
    	    LEFT JOIN FETCH t.regimen r
    	    LEFT JOIN FETCH t.retenciones rt
    	    LEFT JOIN FETCH t.contactos con
    	    LEFT JOIN FETCH t.sedes s
    	    LEFT JOIN FETCH s.departamento d
    	    LEFT JOIN FETCH s.municipio m
    	    WHERE t.terceroId = :id
    	    """)
	Optional<Terceros> buscarPorIdConDetalles(@Param("id") Integer id);

	@Query("SELECT COUNT(t) FROM Terceros t")
	long countTotal();

	@Query("SELECT COUNT(t) FROM Terceros t WHERE LOWER(t.clasificacionTercero.nombre) = LOWER(:nombre)")
	long countByClasificacionNombre(@Param("nombre") String nombre);

	@Modifying
	@Query("UPDATE Terceros t SET t.ultimoMovimiento = :momento WHERE t.terceroId = :id")
	void actualizarUltimoMovimiento(@Param("id") Integer id, @Param("momento") java.time.LocalDateTime momento);

	@Query("SELECT COUNT(t) FROM Terceros t WHERE t.ultimoMovimiento IS NULL OR t.ultimoMovimiento < :fechaLimite")
	long countSinActividad(@Param("fechaLimite") java.time.LocalDateTime fechaLimite);

	@Query(value = """
			SELECT t.razon_social AS razonSocial, t.tercero_id AS terceroId, SUM(c.saldo) AS saldo, 'Pagar' AS tipo
			FROM cuentas_por_pagar c
			JOIN terceros t ON c.proveedor_id = t.tercero_id
			WHERE c.estado = 'PENDIENTE'
			GROUP BY t.tercero_id, t.razon_social
			UNION ALL
			SELECT t.razon_social AS razonSocial, t.tercero_id AS terceroId, SUM(c.saldo) AS saldo, 'Cobrar' AS tipo
			FROM cuentas_por_cobrar c
			JOIN ventas v ON c.venta_id = v.id
			JOIN terceros t ON t.tercero_id = v.cliente_id
			WHERE c.estado = 'PENDIENTE'
			GROUP BY t.tercero_id, t.razon_social
			""", nativeQuery = true)
	List<SaldoTerceroDTO> consultarSaldosPendientesPorTercero();
}
