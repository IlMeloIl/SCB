package com.example.demo.simulator;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class TotemSimulator {
	
    private int numero;
    private List<TrancaSimulator> trancas;
    private String mensagemDisplay;

    public TotemSimulator(int numero) {
        this.numero = numero;
        this.trancas = new ArrayList<>();
        
        // Cria 5 trancas para demonstração
        for (int i = 1; i <= 5; i++) {
            trancas.add(new TrancaSimulator(i));
        }
        this.mensagemDisplay = "Bem-vindo ao Totem " + numero;
    }

    public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public List<TrancaSimulator> getTrancas() {
		return trancas;
	}

	public void setTrancas(List<TrancaSimulator> trancas) {
		this.trancas = trancas;
	}

	public String getMensagemDisplay() {
		return mensagemDisplay;
	}

	public void setMensagemDisplay(String mensagemDisplay) {
		this.mensagemDisplay = mensagemDisplay;
	}

	public void exibirMensagem(String mensagem) {
        this.mensagemDisplay = mensagem;
        System.out.println("📱 Totem " + numero + ": " + mensagem);
    }

    public void mostrarStatus() {
        System.out.println("\n=== Status do Totem " + numero + " ===");
        System.out.println("Display: " + mensagemDisplay);
        System.out.println("Status das Trancas:");
        trancas.forEach(t -> System.out.println(
            "Tranca " + t.getNumero() + " " + t.getStatus() + 
            " - " + t.getStatusDisplay()
        ));
        System.out.println("================================\n");
    }

    public TrancaSimulator getTrancaPorNumero(int numero) {
        return trancas.stream()
            .filter(t -> t.getNumero() == numero)
            .findFirst()
            .orElse(null);
    }
}

