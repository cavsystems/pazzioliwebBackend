package com.pazzioliweb.tercerosmodule.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.pazzioliweb.commonbacken.entity.Departamento;
import com.pazzioliweb.commonbacken.entity.Municipio;
import com.pazzioliweb.commonbacken.entity.Retenciones;
import com.pazzioliweb.commonbacken.entity.Tipoidentificacion;
import com.pazzioliweb.empresasback.entity.Regimen;
import com.pazzioliweb.productosmodule.entity.Precios;
import com.pazzioliweb.tercerosmodule.dtos.ContactoTerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.dtos.SedeTerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.SedeTerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDTO;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDTOImpl;
import com.pazzioliweb.tercerosmodule.dtos.TerceroDtoresponse;
import com.pazzioliweb.tercerosmodule.dtos.TerceroResumenDTO;
import com.pazzioliweb.tercerosmodule.dtos.TerceroimplemenDTO;
import com.pazzioliweb.tercerosmodule.entity.ClasificacionTercero;
import com.pazzioliweb.tercerosmodule.entity.ContactoTercero;
import com.pazzioliweb.tercerosmodule.entity.SedeTercero;
import com.pazzioliweb.tercerosmodule.entity.Terceros;
import com.pazzioliweb.tercerosmodule.entity.TipoContacto;
import com.pazzioliweb.tercerosmodule.repositori.TercerosRepository;
import com.pazzioliweb.usuariosbacken.entity.Tipopersona;

import jakarta.persistence.EntityManager;

@Service
public class TercerosService {

    private final TercerosRepository terceroRepository;
    private final EntityManager entityManager;

    @Autowired
    public TercerosService(TercerosRepository terceroRepository, EntityManager entityManager) {
        this.terceroRepository = terceroRepository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public Page<TerceroDTOImpl> listar(int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Terceros> tercerosPage = terceroRepository.findAll(pageable);

        return tercerosPage.map(this::convertirADTO);
    }

    public Page<TerceroResumenDTO> listarTerceroBasicos(int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TerceroResumenDTO> tercerosPage = terceroRepository.listarTercerosBasicos(pageable);

        return tercerosPage;
    }

    public Page<TerceroResumenDTO> buscar(String termino,int tipoprovedor, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();



        Pageable pageable = PageRequest.of(page, size, sort);
        if(tipoprovedor>0) {
            Page<TerceroResumenDTO> tercerosPage = terceroRepository.buscarPorIdentificacionORazonSocialTipo("%" + termino.trim().toLowerCase() + "%", tipoprovedor, pageable);

            return tercerosPage;
        }
        Page<TerceroResumenDTO> tercerosPage = terceroRepository.buscarPorIdentificacionORazonSocial("%" + termino.trim().toLowerCase() + "%", pageable);
        return tercerosPage;


    }


    @Transactional(readOnly = true)
    public Page<TerceroDTOImpl> buscarconconcta(String termino,int tipoprovedor, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();



        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Terceros> tercerosPage = terceroRepository.traerTercerosXFiltropro("%" + termino.trim().toLowerCase() + "%", tipoprovedor, pageable);
        tercerosPage.getContent().forEach(t -> {
            t.getContactos().size();

        });

        return tercerosPage.map(t -> {
            TerceroDTOImpl  dto = convertirADTO(t);
            return dto;
        });




    }




    @Transactional(readOnly = true)
    public Page<TerceroDTOImpl> buscarconconctanormal(String termino,int tipoprovedor, int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();



        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Terceros> tercerosPage = terceroRepository.traerTercerosXFiltropronormal("%" + termino.trim().toLowerCase() + "%", pageable);
        tercerosPage.getContent().forEach(t -> {
            t.getContactos().size();

        });

        return tercerosPage.map(t -> {
            TerceroDTOImpl  dto = convertirADTO(t);
            return dto;
        });




    }


    @Transactional(readOnly = true)
    public TerceroDTOImpl buscarconconctatoid(int idcliente) {





        Optional<Terceros> tercerosPage = terceroRepository. traerTercerosXFiltroid(idcliente);

        TerceroDTOImpl  dto = convertirADTO( tercerosPage.get());
        return dto;





    }



    @Transactional(readOnly = true)
    public Optional<TerceroDTOImpl> buscarPorId(Integer id) {
        return terceroRepository.buscarPorIdConDetalles(id)
                .map(TerceroDTOImpl::fromEntity);
    }

    @Transactional
    public TerceroDTOImpl guardar(TerceroDTOImpl dto) {
        Terceros tercero = new Terceros();

        // Campos básicos
        tercero.setIdentificacion(dto.getIdentificacion());
        tercero.setDv(dto.getDv());
        tercero.setNombre1(dto.getNombre1());
        tercero.setNombre2(dto.getNombre2());
        tercero.setApellido1(dto.getApellido1());
        tercero.setApellido2(dto.getApellido2());
        tercero.setRazonSocial(dto.getRazonSocial());
        tercero.setDireccion(dto.getDireccion());
        tercero.setPlazo(dto.getPlazo());
        tercero.setCupo(dto.getCupo());

        // Relaciones usando solo IDs
        if (dto.getTipoIdentificacion() != null && dto.getTipoIdentificacion().getCodigo() != null) {
            tercero.setTipoIdentificacion(entityManager.getReference(Tipoidentificacion.class, dto.getTipoIdentificacion().getCodigo()));
        }

        if (dto.getClasificacionTercero() != null && dto.getClasificacionTercero().getClasificacionTerceroId() != null) {
            tercero.setClasificacionTercero(entityManager.getReference(ClasificacionTercero.class, dto.getClasificacionTercero().getClasificacionTerceroId()));
        }

        if (dto.getRegimen() != null && dto.getRegimen().getCodigo() != null) {
            tercero.setRegimen(entityManager.getReference(Regimen.class, dto.getRegimen().getCodigo()));
        }

        if (dto.getPrecio() != null && dto.getPrecio().getPrecio_id() != null) {
            tercero.setPrecio(entityManager.getReference(Precios.class, dto.getPrecio().getPrecio_id()));
        }

        if (dto.getRetenciones() != null && !dto.getRetenciones().isEmpty()) {
            Set<Retenciones> retSet = dto.getRetenciones().stream()
                    .map(rdto -> entityManager.getReference(Retenciones.class, rdto.getRetencionId()))
                    .collect(Collectors.toSet());
            tercero.setRetenciones(retSet);
        }

        // Contactos
        if (dto.getContactos() != null) {
            List<ContactoTercero> contactos = dto.getContactos().stream().map(c -> {
                ContactoTercero contacto = new ContactoTercero();
                contacto.setValorContacto(c.getValorContacto());
                contacto.setEsPrincipal(c.getEsPrincipal());
                if (c.getTipoContacto() != null && c.getTipoContacto().getTipoContactoId() != null) {
                    contacto.setTipoContacto(entityManager.getReference(TipoContacto.class, c.getTipoContacto().getTipoContactoId()));
                }
                contacto.setTercero(tercero);
                return contacto;
            }).toList();
            tercero.setContactos(new HashSet<>(contactos));
        }

        // Sedes
        if (dto.getSedes() != null) {
            List<SedeTercero> sedes = dto.getSedes().stream().map(s -> {
                SedeTercero sede = new SedeTercero();
                sede.setNombreSede(s.getNombreSede());
                sede.setDireccion(s.getDireccion());
                sede.setTelefono(s.getTelefono());
                sede.setPrincipal(s.getPrincipal());
                sede.setActivo(s.getActivo());
                if (s.getDepartamento() != null && s.getDepartamento().getDepartamentoId() != null) {
                    sede.setDepartamento(entityManager.getReference(Departamento.class, s.getDepartamento().getDepartamentoId()));
                }
                if (s.getMunicipio() != null && s.getMunicipio().getMunicipioId() != null) {
                    sede.setMunicipio(entityManager.getReference(Municipio.class, s.getMunicipio().getMunicipioId()));
                }
                sede.setTercero(tercero);
                return sede;
            }).toList();
            tercero.setSedes(new HashSet<>(sedes));
        }

        if (dto.getTipoPersona() != null && dto.getTipoPersona().getCodigo() != null) {
            tercero.setTipoPersona(entityManager.getReference(Tipopersona.class, dto.getTipoPersona().getCodigo()));
        }

        if (dto.getDepartamento() != null && dto.getDepartamento().getDepartamentoId() != null) {
            tercero.setDepartamento(entityManager.getReference(Departamento.class, dto.getDepartamento().getDepartamentoId()));
        }

        if (dto.getCiudad() != null && dto.getCiudad().getMunicipioId() != null) {
            tercero.setCiudad(entityManager.getReference(Municipio.class, dto.getCiudad().getMunicipioId()));
        }

        if (dto.getCodigoPostal() != null) {
            tercero.setCodigoPostal(dto.getCodigoPostal());
        }

        // Guardar en BD
        Terceros guardado = terceroRepository.save(tercero);

        // ⚡ Conversión dentro de la transacción
        return TerceroDTOImpl.fromEntity(guardado);
    }

    @Transactional
    public TerceroDTOImpl actualizar(Integer id, TerceroDTOImpl dto) {
        Terceros tercero = terceroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tercero no encontrado con ID: " + id));

        // Solo actualiza los campos enviados (no pisas los existentes con null)
        if (dto.getIdentificacion() != null) tercero.setIdentificacion(dto.getIdentificacion());
        if (dto.getDv() != null) tercero.setDv(dto.getDv());
        if (dto.getNombre1() != null) tercero.setNombre1(dto.getNombre1());
        if (dto.getNombre2() != null) tercero.setNombre2(dto.getNombre2());
        if (dto.getApellido1() != null) tercero.setApellido1(dto.getApellido1());
        if (dto.getApellido2() != null) tercero.setApellido2(dto.getApellido2());
        if (dto.getRazonSocial() != null) tercero.setRazonSocial(dto.getRazonSocial());
        if (dto.getDireccion() != null) tercero.setDireccion(dto.getDireccion());
        if (dto.getPlazo() != null) tercero.setPlazo(dto.getPlazo());
        if (dto.getCupo() != null) tercero.setCupo(dto.getCupo());

        // Relaciones (solo si vienen en el DTO)
        if (dto.getTipoIdentificacion() != null && dto.getTipoIdentificacion().getCodigo() != null) {
            tercero.setTipoIdentificacion(entityManager.getReference(Tipoidentificacion.class, dto.getTipoIdentificacion().getCodigo()));
        }

        if (dto.getClasificacionTercero() != null && dto.getClasificacionTercero().getClasificacionTerceroId() != null) {
            tercero.setClasificacionTercero(entityManager.getReference(ClasificacionTercero.class, dto.getClasificacionTercero().getClasificacionTerceroId()));
        }

        if (dto.getRegimen() != null && dto.getRegimen().getCodigo() != null) {
            tercero.setRegimen(entityManager.getReference(Regimen.class, dto.getRegimen().getCodigo()));
        }

        if (dto.getPrecio() != null && dto.getPrecio().getPrecio_id() != null) {
            tercero.setPrecio(entityManager.getReference(Precios.class, dto.getPrecio().getPrecio_id()));
        }

        // Retenciones (solo si viene el arreglo)
        if (dto.getRetenciones() != null) {
            Set<Retenciones> retSet = dto.getRetenciones().stream()
                    .map(rdto -> entityManager.getReference(Retenciones.class, rdto.getRetencionId()))
                    .collect(Collectors.toSet());
            tercero.setRetenciones(retSet);
        }
        if (dto.getTipoPersona() != null && dto.getTipoPersona().getCodigo() != null) {
            tercero.setTipoPersona(entityManager.getReference(Tipopersona.class, dto.getTipoPersona().getCodigo()));
        }

        if (dto.getDepartamento() != null && dto.getDepartamento().getDepartamentoId() != null) {
            tercero.setDepartamento(entityManager.getReference(Departamento.class, dto.getDepartamento().getDepartamentoId()));
        }

        if (dto.getCiudad() != null && dto.getCiudad().getMunicipioId() != null) {
            tercero.setCiudad(entityManager.getReference(Municipio.class, dto.getCiudad().getMunicipioId()));
        }

        if (dto.getCodigoPostal() != null) {
            tercero.setCodigoPostal(dto.getCodigoPostal());
        }

        // SEDES
        if (dto.getSedes() != null) {

            // Crear mapa para sedes existentes
            Map<Integer, SedeTercero> existentes = tercero.getSedes().stream()
                    .collect(Collectors.toMap(SedeTercero::getSedeId, s -> s));

            for (SedeTerceroDTO sdto : dto.getSedes()) {

                // ACTUALIZAR una sede existente
                if (sdto.getSedeId() != null && existentes.containsKey(sdto.getSedeId())) {

                    SedeTercero existente = existentes.get(sdto.getSedeId());

                    if (sdto.getNombreSede() != null) existente.setNombreSede(sdto.getNombreSede());
                    if (sdto.getDireccion() != null) existente.setDireccion(sdto.getDireccion());
                    if (sdto.getTelefono() != null) existente.setTelefono(sdto.getTelefono());
                    if (sdto.getPrincipal() != null) existente.setPrincipal(sdto.getPrincipal());
                    if (sdto.getActivo() != null) existente.setActivo(sdto.getActivo());

                    if (sdto.getDepartamento() != null) {
                        existente.setDepartamento(
                                entityManager.getReference(Departamento.class, sdto.getDepartamento().getDepartamentoId())
                        );
                    }

                    if (sdto.getMunicipio() != null) {
                        existente.setMunicipio(
                                entityManager.getReference(Municipio.class, sdto.getMunicipio().getMunicipioId())
                        );
                    }
                }
                else {
                    // CREAR una nueva
                    SedeTercero nueva = new SedeTercero();

                    nueva.setNombreSede(sdto.getNombreSede());
                    nueva.setDireccion(sdto.getDireccion());
                    nueva.setTelefono(sdto.getTelefono());
                    nueva.setPrincipal(sdto.getPrincipal());
                    nueva.setActivo(sdto.getActivo());

                    if (sdto.getDepartamento() != null) {
                        nueva.setDepartamento(
                                entityManager.getReference(Departamento.class, sdto.getDepartamento().getDepartamentoId())
                        );
                    }
                    if (sdto.getMunicipio() != null) {
                        nueva.setMunicipio(
                                entityManager.getReference(Municipio.class, sdto.getMunicipio().getMunicipioId())
                        );
                    }

                    nueva.setTercero(tercero);
                    tercero.getSedes().add(nueva); // ← IMPORTANTE: ahora se añade en vez de reemplazar todo
                }
            }
        }
        // ------------------------------------------------

        // Guardar cambios
        Terceros actualizado = terceroRepository.save(tercero);

        return TerceroDTOImpl.fromEntity(actualizado);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!terceroRepository.existsById(id)) {
            throw new RuntimeException("No existe un tercero con el ID: " + id);
        }
        try {
            terceroRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Este tercero no puede ser eliminado, tiene movimientos asociados");
        }
    }

    private TerceroDTOImpl convertirADTO(Terceros t) {
        TerceroDTOImpl dto = TerceroDTOImpl.fromEntity(t);

        // Mapear Contactos
        if (t.getContactos() != null) {
            dto.setContactos(
                    t.getContactos()
                            .stream()
                            .map(ContactoTerceroDTOImpl::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        // Mapear Sedes
        if (t.getSedes() != null) {
            dto.setSedes(
                    t.getSedes()
                            .stream()
                            .map(SedeTerceroDTOImpl::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }





    @Transactional(readOnly = true)
    public List<TerceroDTOImpl> listarTercerosConDetalles() {
        return terceroRepository.traerTercerosConDetalles()
                .stream()
                .map(TerceroDTOImpl::fromEntity)
                .toList();
    }

}