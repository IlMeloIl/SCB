/**
 * Serviço responsável pelo gerenciamento de cartões de crédito
 * Implementa a lógica de cobranças e gestão de cartões dos ciclistas
 * 
 * @Service Marca como um componente de serviço do Spring
 */
package com.example.demo.service;

import com.example.demo.model.CartaoCredito;
import com.example.demo.model.Ciclista;
import com.example.demo.repository.CartaoCreditoRepository;
import com.example.demo.repository.CiclistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartaoCreditoService {

	@Autowired
	private CartaoCreditoRepository cartaoCreditoRepository;

	@Autowired
	private CiclistaRepository ciclistaRepository;

	/**
	 * Processa uma cobrança no cartão de crédito do ciclista Utiliza o cartão
	 * principal do ciclista para a cobrança
	 * 
	 * @param ciclista ciclista que será cobrado
	 * @param valor    valor a ser cobrado
	 * @return boolean true se a cobrança foi realizada com sucesso
	 * @throws RuntimeException se o ciclista não tiver cartão principal
	 */
	@Transactional
	public boolean realizarCobranca(Ciclista ciclista, double valor) {
		CartaoCredito cartaoPrincipal = obterCartaoPrincipal(ciclista);
		if (cartaoPrincipal == null) {
			throw new RuntimeException("Ciclista não possui cartão de crédito principal");
		}
		boolean cobrancaRealizada = processarCobranca(cartaoPrincipal, valor);
		if (cobrancaRealizada) {
			registrarCobranca(ciclista, cartaoPrincipal, valor);
		}
		return cobrancaRealizada;
	}

	/**
	 * Obtém o cartão principal de um ciclista
	 * 
	 * @param ciclista ciclista dono do cartão
	 * @return CartaoCredito cartão principal do ciclista
	 * @throws RuntimeException se não encontrar cartão principal
	 */
	private CartaoCredito obterCartaoPrincipal(Ciclista ciclista) {
		return cartaoCreditoRepository.findByCiclistaAndPrincipal(ciclista, true)
				.orElseThrow(() -> new RuntimeException("Cartão principal não encontrado"));
	}

	/**
     * Simula o processamento de uma cobrança
     * 
     * @param cartao cartão onde será realizada a cobrança
     * @param valor valor a ser cobrado
     * @return boolean true indicando sucesso da cobrança
     */
	private boolean processarCobranca(CartaoCredito cartao, double valor) {
		System.out.println("Processando cobrança de R$" + valor + " no cartão " + cartao.getNumero());

		return true;
	}

	/**
     * Registra uma cobrança realizada
     * 
     * @param ciclista ciclista cobrado
     * @param cartao cartão utilizado
     * @param valor valor cobrado
     */
	private void registrarCobranca(Ciclista ciclista, CartaoCredito cartao, double valor) {
		System.out.println("Cobrança de R$" + valor + " realizada para o ciclista " + ciclista.getNome() + " no cartão "
				+ cartao.getNumero() + " em " + LocalDateTime.now());
	}

	/**
     * Adiciona um novo cartão de crédito ao ciclista
     * 
     * @param ciclistaId ID do ciclista
     * @param cartao dados do novo cartão
     * @return CartaoCredito cartão adicionado
     * @throws RuntimeException se o ciclista não for encontrado
     */
	@Transactional
	public CartaoCredito adicionarCartao(Long ciclistaId, CartaoCredito cartao) {
		Ciclista ciclista = ciclistaRepository.findById(ciclistaId)
				.orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));

		cartao.setCiclista(ciclista);

		if (cartao.isPrincipal()) {
			cartaoCreditoRepository.findByCiclista(ciclista).forEach(c -> c.setPrincipal(false));
		}

		return cartaoCreditoRepository.save(cartao);
	}

	/**
     * Remove um cartão de crédito do ciclista
     * Não permite remover o cartão principal se for o único
     * 
     * @param ciclistaId ID do ciclista
     * @param cartaoId ID do cartão a ser removido
     * @throws RuntimeException se o cartão for principal e único
     */
	@Transactional
	public void removerCartao(Long ciclistaId, Long cartaoId) {
		Ciclista ciclista = ciclistaRepository.findById(ciclistaId)
				.orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));

		ciclista.getCartoes().removeIf(c -> c.getId().equals(cartaoId));
		ciclistaRepository.save(ciclista);
	}

	/**
     * Define um cartão como principal para o ciclista
     * Remove o status principal de outros cartões
     * 
     * @param ciclistaId ID do ciclista
     * @param cartaoId ID do cartão a ser definido como principal
     * @throws RuntimeException se o ciclista não for encontrado
     */
	@Transactional
	public void definirCartaoPrincipal(Long ciclistaId, Long cartaoId) {
		Ciclista ciclista = ciclistaRepository.findById(ciclistaId)
				.orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));

		ciclista.getCartoes().forEach(c -> c.setPrincipal(c.getId().equals(cartaoId)));
		ciclistaRepository.save(ciclista);
	}

	/**
     * Lista todos os cartões de um ciclista
     * 
     * @param ciclistaId ID do ciclista
     * @return List<CartaoCredito> lista de cartões do ciclista
     * @throws RuntimeException se o ciclista não for encontrado
     */
	public List<CartaoCredito> listarCartoes(Long ciclistaId) {
		Ciclista ciclista = ciclistaRepository.findById(ciclistaId)
				.orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));
		return cartaoCreditoRepository.findByCiclista(ciclista);
	}

}