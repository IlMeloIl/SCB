package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_ciclista")
public abstract class Ciclista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;
    
    @NotBlank(message = "A data de nascimento não pode estar em branco")
    private String nascimento;
    
    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email não pode estar em branco")
    private String email;
    
    @NotBlank(message = "O telefone não pode estar em branco")
    private String telefone;

    private LocalDateTime dataCadastro;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "ciclista_id")
    private List<CartaoCredito> cartoes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StatusCiclista status = StatusCiclista.ATIVO;

 // Getters e Setters
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

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<CartaoCredito> getCartoes() {
        return cartoes;
    }

    public void setCartoes(List<CartaoCredito> cartoes) {
        this.cartoes = cartoes;
    }

    public StatusCiclista getStatus() {
        return status;
    }

    public void setStatus(StatusCiclista status) {
        this.status = status;
    }

    // Métodos adicionais
    public void adicionarCartao(CartaoCredito cartao) {
        this.cartoes.add(cartao);
    }

    public void removerCartao(CartaoCredito cartao) {
        this.cartoes.remove(cartao);
    }
    
}