package com.example.demo.dto;

public class EmprestimoRequestDTO {
    private String identificacaoCiclista;
    private Long trancaId;

    // Construtores, getters e setters
    public EmprestimoRequestDTO() {}

    public EmprestimoRequestDTO(String identificacaoCiclista, Long trancaId) {
        this.identificacaoCiclista = identificacaoCiclista;
        this.trancaId = trancaId;
    }

    public String getIdentificacaoCiclista() {
        return identificacaoCiclista;
    }

    public void setIdentificacaoCiclista(String identificacaoCiclista) {
        this.identificacaoCiclista = identificacaoCiclista;
    }

    public Long getTrancaId() {
        return trancaId;
    }

    public void setTrancaId(Long trancaId) {
        this.trancaId = trancaId;
    }
}