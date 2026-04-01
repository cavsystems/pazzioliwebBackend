package com.pazzioliweb.tercerosmodule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contactos_tercero")
public class ContactoTercero {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contacto_id")
    private Integer contactoId;

    @ManyToOne
    @JoinColumn(name = "tipo_contacto_id", nullable = false)
    private TipoContacto tipoContacto;

    @Column(name = "valor_contacto", length = 150, nullable = false)
    private String valorContacto;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tercero_id", nullable = false)
    private Terceros tercero;

	public Integer getContactoId() {
		return contactoId;
	}

	public void setContactoId(Integer contactoId) {
		this.contactoId = contactoId;
	}

	public TipoContacto getTipoContacto() {
		return tipoContacto;
	}

	public void setTipoContacto(TipoContacto tipoContacto) {
		this.tipoContacto = tipoContacto;
	}

	public String getValorContacto() {
		return valorContacto;
	}

	public void setValorContacto(String valorContacto) {
		this.valorContacto = valorContacto;
	}

	public Boolean getEsPrincipal() {
		return esPrincipal;
	}

	public void setEsPrincipal(Boolean esPrincipal) {
		this.esPrincipal = esPrincipal;
	}

	public Terceros getTercero() {
		return tercero;
	}

	public void setTercero(Terceros tercero) {
		this.tercero = tercero;
	}
}
