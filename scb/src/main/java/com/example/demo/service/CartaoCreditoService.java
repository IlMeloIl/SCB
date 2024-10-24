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

    private CartaoCredito obterCartaoPrincipal(Ciclista ciclista) {
        return cartaoCreditoRepository.findByCiclistaAndPrincipal(ciclista, true)
                .orElseThrow(() -> new RuntimeException("Cartão principal não encontrado"));
    }

    private boolean processarCobranca(CartaoCredito cartao, double valor) {
        // Simulação de processamento de cobrança
        System.out.println("Processando cobrança de R$" + valor + " no cartão " + cartao.getNumero());
        
        return true;
    }

    private void registrarCobranca(Ciclista ciclista, CartaoCredito cartao, double valor) {
        // Aqui você poderia registrar a cobrança em um histórico
        System.out.println("Cobrança de R$" + valor + " realizada para o ciclista " + ciclista.getNome() +
                " no cartão " + cartao.getNumero() + " em " + LocalDateTime.now());
    }

    public boolean validarCartao(CartaoCredito cartao) {
        // Implementar lógica de validação do cartão
        // Por exemplo, verificar se o número é válido, se a data de validade não expirou, etc.
        return true; // Retorno simplificado para este exemplo
    }

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

    @Transactional
    public void removerCartao(Long ciclistaId, Long cartaoId) {
        Ciclista ciclista = ciclistaRepository.findById(ciclistaId)
                .orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));

        ciclista.getCartoes().removeIf(c -> c.getId().equals(cartaoId));
        ciclistaRepository.save(ciclista);
    }

    @Transactional
    public void definirCartaoPrincipal(Long ciclistaId, Long cartaoId) {
        Ciclista ciclista = ciclistaRepository.findById(ciclistaId)
                .orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));

        ciclista.getCartoes().forEach(c -> c.setPrincipal(c.getId().equals(cartaoId)));
        ciclistaRepository.save(ciclista);
    }

    public List<CartaoCredito> listarCartoes(Long ciclistaId) {
        Ciclista ciclista = ciclistaRepository.findById(ciclistaId)
                .orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));
        return cartaoCreditoRepository.findByCiclista(ciclista);
    }
      
}