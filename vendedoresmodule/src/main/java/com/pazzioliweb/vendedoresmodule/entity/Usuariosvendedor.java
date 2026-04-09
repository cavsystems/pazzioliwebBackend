package com.pazzioliweb.vendedoresmodule.entity;

import com.pazzioliweb.usuariosbacken.entity.Usuario;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios_vendedor")
@Data
public class Usuariosvendedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Ojo: aquí respeto tu tabla, aunque parezca un error
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "vendedor_id")
    private Vendedores vendedor;
}
