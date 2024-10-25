package com.example.demo.simulator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HardwareSimulatorService {
    private final Map<Integer, TotemSimulator> totens = new HashMap<>();

    @PostConstruct
    public void init() {
        // Cria 3 totens para demonstração
        for (int i = 1; i <= 3; i++) {
            totens.put(i, new TotemSimulator(i));
        }
        System.out.println("Simulador de hardware iniciado com " + totens.size() + " totens");
        mostrarStatusTotens();
    }

    public void mostrarStatusTotens() {
        totens.values().forEach(TotemSimulator::mostrarStatus);
    }

    public TotemSimulator getTotem(int numero) {
        TotemSimulator totem = totens.get(numero);
        if (totem == null) {
            throw new IllegalArgumentException("Totem " + numero + " não encontrado");
        }
        return totem;
    }

    public Collection<TotemSimulator> getTotens() {
        return totens.values();
    }
}
