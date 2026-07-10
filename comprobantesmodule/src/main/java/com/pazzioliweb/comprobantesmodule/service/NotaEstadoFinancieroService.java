package com.pazzioliweb.comprobantesmodule.service;

import com.pazzioliweb.comprobantesmodule.dtos.NotaEstadoFinancieroDTO;
import com.pazzioliweb.comprobantesmodule.entity.NotaEstadoFinanciero;
import com.pazzioliweb.comprobantesmodule.repositori.NotaEstadoFinancieroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotaEstadoFinancieroService {

    private final NotaEstadoFinancieroRepository repo;

    public NotaEstadoFinancieroService(NotaEstadoFinancieroRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<NotaEstadoFinancieroDTO> listar(Integer anio) {
        int a = anio != null ? anio : LocalDate.now().getYear();
        return repo.findByAnioOrderByNumeroAsc(a).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public NotaEstadoFinancieroDTO crear(NotaEstadoFinancieroDTO dto) {
        validar(dto);
        NotaEstadoFinanciero n = new NotaEstadoFinanciero();
        n.setAnio(dto.getAnio() != null ? dto.getAnio() : LocalDate.now().getYear());
        n.setNumero(dto.getNumero() != null && dto.getNumero() > 0 ? dto.getNumero() : 1);
        n.setTitulo(dto.getTitulo().trim());
        n.setContenido(dto.getContenido());
        n.setEstado("ACTIVO");
        return toDto(repo.save(n));
    }

    @Transactional
    public NotaEstadoFinancieroDTO actualizar(Integer id, NotaEstadoFinancieroDTO dto) {
        NotaEstadoFinanciero n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada: " + id));
        validar(dto);
        if (dto.getAnio() != null) n.setAnio(dto.getAnio());
        if (dto.getNumero() != null && dto.getNumero() > 0) n.setNumero(dto.getNumero());
        n.setTitulo(dto.getTitulo().trim());
        n.setContenido(dto.getContenido());
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) n.setEstado(dto.getEstado());
        return toDto(repo.save(n));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!repo.existsById(id)) throw new RuntimeException("Nota no encontrada: " + id);
        repo.deleteById(id);
    }

    private void validar(NotaEstadoFinancieroDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank())
            throw new RuntimeException("El título de la nota es obligatorio");
    }

    private NotaEstadoFinancieroDTO toDto(NotaEstadoFinanciero n) {
        NotaEstadoFinancieroDTO dto = new NotaEstadoFinancieroDTO();
        dto.setId(n.getId());
        dto.setAnio(n.getAnio());
        dto.setNumero(n.getNumero());
        dto.setTitulo(n.getTitulo());
        dto.setContenido(n.getContenido());
        dto.setEstado(n.getEstado());
        return dto;
    }
}
