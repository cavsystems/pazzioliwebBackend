package com.pazzioliweb.ventasmodule.repository;

import com.pazzioliweb.ventasmodule.entity.Pedido;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoSpecification {

    private PedidoSpecification() {}

    /**
     * Construye una Specification con filtros opcionales combinables.
     *
     * @param terceroId   ID del cliente (tercero). Opcional.
     * @param vendedorId  ID del vendedor. Opcional.
     * @param cajeroId    ID del cajero. Opcional.
     * @param fechaInicio Fecha de inicio del rango de emisión. Opcional.
     * @param fechaFin    Fecha de fin del rango de emisión. Opcional.
     * @return Specification que aplica solo los filtros que no son null.
     */
    public static Specification<Pedido> conFiltros(
            Long terceroId,
            Integer vendedorId,
            Integer cajeroId,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (terceroId != null  && terceroId != 0) {
                predicates.add(cb.equal(root.get("cliente").get("terceroId"), terceroId));
            }

            if (vendedorId != null && vendedorId != 0) {
                predicates.add(cb.equal(root.get("vendedor").get("vendedor_id"), vendedorId));
            }

            if (cajeroId != null && cajeroId != 0) {
                predicates.add(cb.equal(root.get("cajero").get("cajeroId"), cajeroId));
            }

            if (fechaInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaEmision"), fechaInicio));
            }

            if (fechaFin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaEmision"), fechaFin));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
