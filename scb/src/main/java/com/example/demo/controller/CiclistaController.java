/**
 * Controller REST responsável pelo gerenciamento de ciclistas no sistema
 * Endpoints para cadastro, autenticação, atualização de dados
 * e gerenciamento de cartões de crédito dos ciclistas
 *
 * @RestController Indica que esta classe é um controller REST
 * @RequestMapping Define o path base "/api/ciclistas" para todos os endpoints
 */
package com.example.demo.controller;

import com.example.demo.model.Ciclista;
import com.example.demo.model.Emprestimo;
import com.example.demo.dto.CartaoCreditoDTO;
import com.example.demo.dto.CiclistaAtualizacaoDTO;
import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.LoginResponseDTO;
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

	/**
     * Cadastra um novo ciclista brasileiro no sistema
     * Valida os dados do ciclista, incluindo CPF único
     *
     * @param brasileiro dados do novo ciclista brasileiro a ser cadastrado
     * @return ResponseEntity<Ciclista> ciclista cadastrado ou erro de validação
     * @throws ResponseStatusException se houver erro de validação ou CPF duplicado
     */
	@PostMapping("/brasileiro")
	public ResponseEntity<Ciclista> cadastrarBrasileiro(@Valid @RequestBody Brasileiro brasileiro) {
		try {
			Ciclista novoCiclista = ciclistaService.cadastrarCiclista(brasileiro);
			return ResponseEntity.ok(novoCiclista);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
     * Cadastra um novo ciclista estrangeiro no sistema
     * Valida os dados do ciclista, incluindo passaporte único
     *
     * @param estrangeiro dados do novo ciclista estrangeiro
     * @return ResponseEntity<Ciclista> ciclista cadastrado ou erro de validação
     * @throws ResponseStatusException se houver erro de validação ou passaporte duplicado
     */
	@PostMapping("/estrangeiro")
	public ResponseEntity<Ciclista> cadastrarEstrangeiro(@Valid @RequestBody Estrangeiro estrangeiro) {
		try {
			Ciclista novoCiclista = ciclistaService.cadastrarCiclista(estrangeiro);
			return ResponseEntity.ok(novoCiclista);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
     * Realiza o login do ciclista no sistema
     * Valida credenciais e retorna informações da sessão
     *
     * @param loginDTO credenciais do usuário (email e senha)
     * @return ResponseEntity<LoginResponseDTO> dados da sessão ou erro de autenticação
     * @throws ResponseStatusException se as credenciais forem inválidas
     */
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
		try {
			LoginResponseDTO response = ciclistaService.realizarLogin(loginDTO.getEmail(), loginDTO.getSenha());
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
		}
	}

	/**
     * Busca um ciclista por sua identificação (CPF ou passaporte)
     *
     * @param identificacao CPF ou passaporte do ciclista
     * @return ResponseEntity<Ciclista> ciclista encontrado ou notFound
     */
	@GetMapping("/{identificacao}")
	public ResponseEntity<Ciclista> buscarCiclista(@PathVariable String identificacao) {
		try {
			Ciclista ciclista = ciclistaService.buscarCiclista(identificacao);
			return ResponseEntity.ok(ciclista);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Atualiza os dados de um ciclista existente
     *
     * @param identificacao CPF ou passaporte do ciclista
     * @param atualizacaoDTO novos dados do ciclista
     * @return ResponseEntity<Ciclista> ciclista atualizado ou notFound
     */
	@PutMapping("/{identificacao}")
	public ResponseEntity<Ciclista> atualizarCiclista(@PathVariable String identificacao,
			@RequestBody CiclistaAtualizacaoDTO atualizacaoDTO) {
		try {
			Ciclista ciclistaAtualizado = ciclistaService.atualizarCiclista(identificacao, atualizacaoDTO);
			return ResponseEntity.ok(ciclistaAtualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Adiciona um novo cartão de crédito ao ciclista
     *
     * @param identificacao CPF ou passaporte do ciclista
     * @param cartaoDTO dados do novo cartão
     * @return ResponseEntity<CartaoCreditoDTO> cartão adicionado ou erro
     */
	@PostMapping("/{identificacao}/cartoes")
	public ResponseEntity<CartaoCreditoDTO> adicionarCartaoCredito(@PathVariable String identificacao,
			@Valid @RequestBody CartaoCreditoDTO cartaoDTO) {
		try {
			CartaoCreditoDTO novoCartao = ciclistaService.adicionarCartaoCredito(identificacao, cartaoDTO);
			return ResponseEntity.ok(novoCartao);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
     * Remove um cartão de crédito do ciclista
     * Não permite remover o cartão principal se houver apenas um cartão
     *
     * @param identificacao CPF ou passaporte do ciclista
     * @param cartaoId ID do cartão a ser removido
     * @return ResponseEntity<Void> sucesso ou erro
     */
	@DeleteMapping("/{identificacao}/cartoes/{cartaoId}")
	public ResponseEntity<Void> removerCartaoCredito(@PathVariable String identificacao, @PathVariable Long cartaoId) {
		try {
			ciclistaService.removerCartaoCredito(identificacao, cartaoId);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
     * Define um cartão como principal para cobranças
     *
     * @param identificacao CPF ou passaporte do ciclista
     * @param cartaoId ID do cartão a ser definido como principal
     * @return ResponseEntity<Void> sucesso ou erro
     */
	@PutMapping("/{identificacao}/cartoes/{cartaoId}/principal")
	public ResponseEntity<Void> definirCartaoPrincipal(@PathVariable String identificacao,
			@PathVariable Long cartaoId) {
		try {
			ciclistaService.definirCartaoPrincipal(identificacao, cartaoId);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
     * Lista todos os cartões de crédito do ciclista
     *
     * @param identificacao CPF ou passaporte do ciclista
     * @return ResponseEntity<List<CartaoCredito>> lista de cartões ou erro
     */
	@GetMapping("/{identificacao}/cartoes")
	public ResponseEntity<List<CartaoCredito>> listarCartoes(@PathVariable String identificacao) {
		try {
			List<CartaoCredito> cartoes = ciclistaService.listarCartoes(identificacao);
			return ResponseEntity.ok(cartoes);
		} catch (RuntimeException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
     * Retorna o histórico de empréstimos do ciclista
     * Inclui empréstimos ativos e finalizados
     *
     * @param identificacao CPF ou passaporte do ciclista
     * @return ResponseEntity<List<EmprestimoDTO>> histórico de empréstimos ou notFound
     */
	@GetMapping("/{identificacao}/emprestimos")
	public ResponseEntity<List<EmprestimoDTO>> buscarHistoricoEmprestimos(@PathVariable String identificacao) {
		try {
			System.out.println("Requisição de histórico de empréstimos para identificação: " + identificacao);
			List<Emprestimo> emprestimos = ciclistaService.buscarHistoricoEmprestimos(identificacao);
			List<EmprestimoDTO> emprestimosDTO = emprestimos.stream().map(this::convertToDTO)
					.collect(Collectors.toList());
			System.out.println("Número de empréstimos convertidos para DTO: " + emprestimosDTO.size());
			return ResponseEntity.ok(emprestimosDTO);
		} catch (RuntimeException e) {
			System.out.println("Erro ao buscar histórico de empréstimos: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Converte um objeto Emprestimo para EmprestimoDTO
     * Método utilitário para conversão de dados
     *
     * @param emprestimo objeto a ser convertido
     * @return EmprestimoDTO objeto convertido
     */
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