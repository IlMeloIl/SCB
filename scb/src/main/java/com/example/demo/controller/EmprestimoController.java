/**
 * Controller REST responsável pelo gerenciamento de empréstimos de bicicletas
 * Gerencia o ciclo completo de empréstimo e devolução
 *
 * @RestController Indica que esta classe é um controller REST
 * @RequestMapping Define o path base "/api/emprestimos" para todos os endpoints
 */
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

    /**
     * Inicia um novo empréstimo de bicicleta
     * Processo:
     * 1. Valida se o ciclista pode realizar empréstimo
     * 2. Verifica disponibilidade da bicicleta
     * 3. Processa cobrança inicial
     * 4. Libera a tranca
     * 5. Registra o empréstimo
     *
     * @param requestDTO dados necessários para o empréstimo (identificação do ciclista e tranca)
     * @return ResponseEntity<EmprestimoDTO> dados do empréstimo realizado ou erro
     */
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

    /**
     * Busca informações de um empréstimo específico
     *
     * @param emprestimoId ID do empréstimo a ser consultado
     * @return ResponseEntity<EmprestimoDTO> dados do empréstimo ou erro se não encontrado
     */
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

    /**
     * Finaliza um empréstimo ativo
     * Processo:
     * 1. Valida o empréstimo ativo do ciclista
     * 2. Verifica disponibilidade da tranca para devolução
     * 3. Calcula e processa cobranças extras 
     * 4. Atualiza status da bicicleta e tranca
     * 5. Registra a devolução
     *
     * @param emprestimoId ID do empréstimo a ser finalizado
     * @param trancaId ID da tranca onde a bicicleta será devolvida
     * @param identificacaoCiclista documento do ciclista (CPF ou passaporte)
     * @return ResponseEntity<EmprestimoDTO> dados da devolução realizada ou erro
     */
    @PutMapping("/{emprestimoId}/devolucao")
    public ResponseEntity<EmprestimoDTO> finalizarEmprestimo(
            @PathVariable Long emprestimoId,
            @RequestParam Long trancaId,
            @RequestParam String identificacaoCiclista) {
        try {
        	// O emprestimoId da URL é ignorado, usando apenas identificacaoCiclista
            EmprestimoDTO emprestimo = emprestimoService.finalizarEmprestimo(null, trancaId, identificacaoCiclista);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            System.out.println("Erro ao finalizar empréstimo: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
}