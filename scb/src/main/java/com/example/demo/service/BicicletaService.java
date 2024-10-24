package com.example.demo.service;

import com.example.demo.model.Bicicleta;
import com.example.demo.model.StatusBicicleta;
import com.example.demo.repository.BicicletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BicicletaService {

    @Autowired
    private BicicletaRepository bicicletaRepository;

    public List<Bicicleta> listarTodas() {
        return bicicletaRepository.findAll();
    }

    public Optional<Bicicleta> buscarPorId(Long id) {
        return bicicletaRepository.findById(id);
    }

    public Optional<Bicicleta> buscarPorNumero(String numero) {
        return bicicletaRepository.findByNumero(numero);
    }

    @Transactional
    public Bicicleta salvar(Bicicleta bicicleta) {
        return bicicletaRepository.save(bicicleta);
    }

    @Transactional
    public void deletar(Long id) {
        bicicletaRepository.deleteById(id);
    }

    public List<Bicicleta> buscarPorStatus(StatusBicicleta status) {
        return bicicletaRepository.findByStatus(status);
    }

    @Transactional
    public boolean atualizarStatus(Long id, StatusBicicleta novoStatus) {
        Optional<Bicicleta> bicicletaOpt = bicicletaRepository.findById(id);
        if (bicicletaOpt.isPresent()) {
            Bicicleta bicicleta = bicicletaOpt.get();
            bicicleta.setStatus(novoStatus);
            bicicletaRepository.save(bicicleta);
            return true;
        }
        return false;
    }
}