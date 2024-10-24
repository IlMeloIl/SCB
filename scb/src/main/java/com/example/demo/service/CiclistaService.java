package com.example.demo.service;

import com.example.demo.model.Ciclista;
import com.example.demo.model.Emprestimo;
import com.example.demo.model.Estrangeiro;
import com.example.demo.dto.CartaoCreditoDTO;
import com.example.demo.dto.CiclistaAtualizacaoDTO;
import com.example.demo.model.Brasileiro;
import com.example.demo.model.CartaoCredito;
import com.example.demo.repository.CiclistaRepository;
import com.example.demo.repository.EmprestimoRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CiclistaService {

    @Autowired
    private CiclistaRepository ciclistaRepository;
    
    @Autowired
    private CartaoCreditoService cartaoCreditoService;
    
    @Autowired
    @Lazy
    private EmprestimoService emprestimoService;
    
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    public Ciclista cadastrarCiclista(@Valid Ciclista ciclista) {
        validarCiclista(ciclista);
        ciclista.setDataCadastro(LocalDateTime.now());
        return ciclistaRepository.save(ciclista);
    }

    private void validarCiclista(Ciclista ciclista) {
        if (ciclista instanceof Brasileiro) {
            Brasileiro brasileiro = (Brasileiro) ciclista;
            if (ciclistaRepository.findByCpf(brasileiro.getCpf()).isPresent()) {
                throw new RuntimeException("CPF já cadastrado");
            }
        } else if (ciclista instanceof Estrangeiro) {
            Estrangeiro estrangeiro = (Estrangeiro) ciclista;
            if (ciclistaRepository.findByPassaporte(estrangeiro.getPassaporte()).isPresent()) {
                throw new RuntimeException("Passaporte já cadastrado");
            }
        }
        if (ciclistaRepository.findByEmail(ciclista.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
    }

    public Ciclista buscarCiclista(String identificacao) {
        System.out.println("Buscando ciclista com identificação: " + identificacao);
        
        Optional<Ciclista> ciclista = ciclistaRepository.findByCpf(identificacao);
        if (ciclista.isPresent()) {
            System.out.println("Ciclista encontrado por CPF: " + ciclista.get().getId());
            return ciclista.get();
        }
        
        ciclista = ciclistaRepository.findByPassaporte(identificacao);
        if (ciclista.isPresent()) {
            System.out.println("Ciclista encontrado por passaporte: " + ciclista.get().getId());
            return ciclista.get();
        }
        
        System.out.println("Ciclista não encontrado para identificação: " + identificacao);
        throw new RuntimeException("Ciclista não encontrado");
    }

    @Transactional
    public Ciclista atualizarCiclista(String identificacao, CiclistaAtualizacaoDTO atualizacaoDTO) {
        return ciclistaRepository.findByCpf(identificacao)
            .map(ciclistaExistente -> {
                if (atualizacaoDTO.getNome() != null) {
                    if (atualizacaoDTO.getNome().length() > 100) {
                        throw new IllegalArgumentException("Nome não pode exceder 100 caracteres");
                    }
                    ciclistaExistente.setNome(atualizacaoDTO.getNome());
                }
                if (atualizacaoDTO.getEmail() != null) {
                    if (!atualizacaoDTO.getEmail().equals(ciclistaExistente.getEmail())) {
                        if (ciclistaRepository.findByEmail(atualizacaoDTO.getEmail()).isPresent()) {
                            throw new IllegalArgumentException("E-mail já está em uso");
                        }
                        if (!isValidEmail(atualizacaoDTO.getEmail())) {
                            throw new IllegalArgumentException("Formato de e-mail inválido");
                        }
                    }
                    ciclistaExistente.setEmail(atualizacaoDTO.getEmail());
                }
                if (atualizacaoDTO.getTelefone() != null) {
                    ciclistaExistente.setTelefone(atualizacaoDTO.getTelefone());
                }
                return ciclistaRepository.save(ciclistaExistente);
            })
            .orElseThrow(() -> new RuntimeException("Ciclista não encontrado"));
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    //@Transactional
    //public CartaoCredito adicionarCartaoCredito(String identificacao, CartaoCredito cartao) {
    //    Ciclista ciclista = buscarCiclista(identificacao);
    //    return cartaoCreditoService.adicionarCartao(ciclista.getId(), cartao);
    //}
    
    @Transactional
    public CartaoCreditoDTO adicionarCartaoCredito(String identificacao, CartaoCreditoDTO cartaoDTO) {
        Ciclista ciclista = buscarCiclista(identificacao);
        CartaoCredito cartao = new CartaoCredito();
        cartao.setNumero(cartaoDTO.getNumero());
        cartao.setNomeTitular(cartaoDTO.getNomeTitular());
        cartao.setValidade(cartaoDTO.getValidade());
        cartao.setCvv(cartaoDTO.getCvv());
        cartao.setPrincipal(cartaoDTO.isPrincipal());
        cartao.setCiclista(ciclista);
        
        if (cartao.isPrincipal()) {
            ciclista.getCartoes().forEach(c -> c.setPrincipal(false));
        }
        
        ciclista.getCartoes().add(cartao);
        ciclistaRepository.save(ciclista);
        
        return converterParaDTO(cartao);
    }

    private CartaoCreditoDTO converterParaDTO(CartaoCredito cartao) {
        CartaoCreditoDTO dto = new CartaoCreditoDTO();
        dto.setId(cartao.getId());
        dto.setNumero(cartao.getNumero());
        dto.setNomeTitular(cartao.getNomeTitular());
        dto.setValidade(cartao.getValidade());
        dto.setCvv(cartao.getCvv());
        dto.setPrincipal(cartao.isPrincipal());
        return dto;
    }

    @Transactional
    public void removerCartaoCredito(String identificacao, Long cartaoId) {
        Ciclista ciclista = buscarCiclista(identificacao);
        cartaoCreditoService.removerCartao(ciclista.getId(), cartaoId);
    }

    @Transactional
    public void definirCartaoPrincipal(String identificacao, Long cartaoId) {
        Ciclista ciclista = buscarCiclista(identificacao);
        cartaoCreditoService.definirCartaoPrincipal(ciclista.getId(), cartaoId);
    }

    public List<CartaoCredito> listarCartoes(String identificacao) {
        Ciclista ciclista = buscarCiclista(identificacao);
        return cartaoCreditoService.listarCartoes(ciclista.getId());
    }
    
    public List<Emprestimo> buscarHistoricoEmprestimos(String identificacao) {
        Ciclista ciclista = buscarCiclista(identificacao);
        System.out.println("Buscando empréstimos para o ciclista: " + ciclista.getId());
        List<Emprestimo> emprestimos = emprestimoRepository.findByCiclistaId(ciclista.getId());
        System.out.println("Número de empréstimos encontrados: " + emprestimos.size());
        return emprestimos;
    }
    
}