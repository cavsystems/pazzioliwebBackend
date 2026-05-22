package com.pazzioliweb.facturacionmodule.repositori;

import com.pazzioliweb.facturacionmodule.entity.DocumentoElectronico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DocumentoElectronicoRepository extends JpaRepository<DocumentoElectronico, Long> {

    Optional<DocumentoElectronico> findByCufe(String cufe);

    @Query("SELECT MAX(d.id) FROM DocumentoElectronico d WHERE d.tipo = :tipo")
    Long findMaxIdByTipo(@Param("tipo") String tipo);

    /** Lista con filtros por fechas, tipo y estado DIAN. */
    @Query("SELECT d FROM DocumentoElectronico d " +
           "WHERE (:tipo IS NULL OR d.tipo = :tipo) " +
           "  AND (:estadoDian IS NULL OR d.estadoDian = :estadoDian) " +
           "  AND (:desde IS NULL OR d.fechaEmision >= :desde) " +
           "  AND (:hasta IS NULL OR d.fechaEmision <= :hasta) " +
           "  AND d.estado = 'ACTIVO' " +
           "ORDER BY d.fechaEmision DESC, d.id DESC")
    List<DocumentoElectronico> findFiltrado(@Param("tipo") String tipo,
                                             @Param("estadoDian") String estadoDian,
                                             @Param("desde") LocalDate desde,
                                             @Param("hasta") LocalDate hasta);
}
