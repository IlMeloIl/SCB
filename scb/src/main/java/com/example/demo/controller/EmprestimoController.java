package com.example.demo.controller;

import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.dto.EmprestimoRequestDTO;
import com.example.demo.model.Emprestimo;
import com.example.demo.repository.EmprestimoRepository;
import com.example.demo.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {
    @Autowired
    private EmprestimoService emprestimoService;
    
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @PostMapping
    public ResponseEntity<EmprestimoDTO> realizarEmprestimo(@RequestBody EmprestimoRequestDTO requestDTO) {
        try {
            System.out.println("Iniciando empréstimo para identificação: " + requestDTO.getIdentificacaoCiclista());
            EmprestimoDTO emprestimo = emprestimoService.realizarEmprestimo(requestDTO);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            System.out.println("Erro ao realizar empréstimo: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{emprestimoId}")
    public ResponseEntity<EmprestimoDTO> buscarEmprestimo(@PathVariable Long emprestimoId) {
        try {
            Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                    .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
            return ResponseEntity.ok(emprestimoService.convertToDTO(emprestimo));
        } catch (Exception e) {
            System.out.println("Erro ao buscar empréstimo: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{emprestimoId}/devolucao")
    public ResponseEntity<EmprestimoDTO> finalizarEmprestimo(
            @PathVariable Long emprestimoId,
            @RequestParam Long trancaId,
            @RequestParam String identificacaoCiclista) {
        try {
            // Ignoramos o emprestimoId da URL e usamos apenas identificacaoCiclista
            EmprestimoDTO emprestimo = emprestimoService.finalizarEmprestimo(null, trancaId, identificacaoCiclista);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            System.out.println("Erro ao finalizar empréstimo: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}