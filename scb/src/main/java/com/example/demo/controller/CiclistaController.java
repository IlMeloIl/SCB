package com.example.demo.controller;

import com.example.demo.model.Ciclista;
import com.example.demo.model.Emprestimo;
import com.example.demo.dto.CartaoCreditoDTO;
import com.example.demo.dto.CiclistaAtualizacaoDTO;
import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.model.Brasileiro;
import com.example.demo.model.Estrangeiro;
import com.example.demo.model.CartaoCredito;
import com.example.demo.service.CiclistaService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ciclistas")
public class CiclistaController {

    @Autowired
    private CiclistaService ciclistaService;

    @PostMapping("/brasileiro")
    public ResponseEntity<Ciclista> cadastrarBrasileiro(@Valid @RequestBody Brasileiro brasileiro) {
        try {
            Ciclista novoCiclista = ciclistaService.cadastrarCiclista(brasileiro);
            return ResponseEntity.ok(novoCiclista);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/estrangeiro")
    public ResponseEntity<Ciclista> cadastrarEstrangeiro(@Valid @RequestBody Estrangeiro estrangeiro) {
        try {
            Ciclista novoCiclista = ciclistaService.cadastrarCiclista(estrangeiro);
            return ResponseEntity.ok(novoCiclista);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{identificacao}")
    public ResponseEntity<Ciclista> buscarCiclista(@PathVariable String identificacao) {
        try {
            Ciclista ciclista = ciclistaService.buscarCiclista(identificacao);
            return ResponseEntity.ok(ciclista);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{identificacao}")
    public ResponseEntity<Ciclista> atualizarCiclista(@PathVariable String identificacao, @RequestBody CiclistaAtualizacaoDTO atualizacaoDTO) {
        try {
            Ciclista ciclistaAtualizado = ciclistaService.atualizarCiclista(identificacao, atualizacaoDTO);
            return ResponseEntity.ok(ciclistaAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{identificacao}/cartoes")
    public ResponseEntity<CartaoCreditoDTO> adicionarCartaoCredito(
            @PathVariable String identificacao, 
            @Valid @RequestBody CartaoCreditoDTO cartaoDTO) {
        try {
            CartaoCreditoDTO novoCartao = ciclistaService.adicionarCartaoCredito(identificacao, cartaoDTO);
            return ResponseEntity.ok(novoCartao);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{identificacao}/cartoes/{cartaoId}")
    public ResponseEntity<Void> removerCartaoCredito(@PathVariable String identificacao, @PathVariable Long cartaoId) {
        try {
            ciclistaService.removerCartaoCredito(identificacao, cartaoId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{identificacao}/cartoes/{cartaoId}/principal")
    public ResponseEntity<Void> definirCartaoPrincipal(@PathVariable String identificacao, @PathVariable Long cartaoId) {
        try {
            ciclistaService.definirCartaoPrincipal(identificacao, cartaoId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    
    @GetMapping("/{identificacao}/cartoes")
    public ResponseEntity<List<CartaoCredito>> listarCartoes(@PathVariable String identificacao) {
        try {
            List<CartaoCredito> cartoes = ciclistaService.listarCartoes(identificacao);
            return ResponseEntity.ok(cartoes);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    
    @GetMapping("/{identificacao}/emprestimos")
    public ResponseEntity<List<EmprestimoDTO>> buscarHistoricoEmprestimos(@PathVariable String identificacao) {
        try {
            System.out.println("Requisição de histórico de empréstimos para identificação: " + identificacao);
            List<Emprestimo> emprestimos = ciclistaService.buscarHistoricoEmprestimos(identificacao);
            List<EmprestimoDTO> emprestimosDTO = emprestimos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            System.out.println("Número de empréstimos convertidos para DTO: " + emprestimosDTO.size());
            return ResponseEntity.ok(emprestimosDTO);
        } catch (RuntimeException e) {
            System.out.println("Erro ao buscar histórico de empréstimos: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    private EmprestimoDTO convertToDTO(Emprestimo emprestimo) {
        EmprestimoDTO dto = new EmprestimoDTO();
        dto.setId(emprestimo.getId());
        dto.setCiclistaId(emprestimo.getCiclista().getId());
        dto.setBicicletaId(emprestimo.getBicicleta().getId());
        dto.setTrancaInicioId(emprestimo.getTrancaInicio().getId());
        dto.setTotemInicioId(emprestimo.getTotemInicio().getId());
        dto.setHoraInicio(emprestimo.getHoraInicio());
        dto.setTaxaInicial(emprestimo.getTaxaInicial());
        dto.setStatus(emprestimo.getStatus().toString());

        // Campos opcionais que podem ser null
        if (emprestimo.getTrancaFim() != null) {
            dto.setTrancaFimId(emprestimo.getTrancaFim().getId());
        }
        if (emprestimo.getTotemFim() != null) {
            dto.setTotemFimId(emprestimo.getTotemFim().getId());
        }
        dto.setHoraFim(emprestimo.getHoraFim());
        dto.setTaxaExtra(emprestimo.getTaxaExtra());

        return dto;
    }
    
}