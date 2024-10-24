package com.example.demo.controller;

import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.dto.EmprestimoRequestDTO;
import com.example.demo.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoDTO> realizarEmprestimo(@RequestBody EmprestimoRequestDTO requestDTO) {
        try {
            EmprestimoDTO emprestimo = emprestimoService.realizarEmprestimo(requestDTO);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{emprestimoId}/devolucao")
    public ResponseEntity<EmprestimoDTO> finalizarEmprestimo(
            @PathVariable Long emprestimoId,
            @RequestParam Long trancaId) {
        try {
            System.out.println("Tentando finalizar empréstimo: " + emprestimoId + " na tranca: " + trancaId);
            EmprestimoDTO emprestimo = emprestimoService.finalizarEmprestimo(emprestimoId, trancaId);
            System.out.println("Empréstimo finalizado com sucesso");
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            System.out.println("Erro ao finalizar empréstimo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}