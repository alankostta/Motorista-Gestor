package com.XzaoDoMal.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_motorista")
public class Motorista implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "Carro é obrigatório")
    @Size(min = 2, max = 50, message = "Carro deve ter entre 2 e 50 caracteres")
    @Column(name = "carro", nullable = false)
    private String carro;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "motorista")
    private List<ControleGeral> controle = new ArrayList<>();

    @OneToMany(mappedBy = "motorista")
    private List<Despesa> despesa = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCarro() {
        return carro;
    }

    public void setCarro(String carro) {
        this.carro = carro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ControleGeral> getControle() {
        return controle;
    }

    public void setControle(List<ControleGeral> controle) {
        this.controle = controle;
    }

    public List<Despesa> getDespesa() {
        return despesa;
    }

    public void setDespesa(List<Despesa> despesa) {
        this.despesa = despesa;
    }
}