package com.example.demo.service;

import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.dto.EmprestimoRequestDTO;
import com.example.demo.model.*;
import com.example.demo.repository.EmprestimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.time.Duration;

@Service
public class EmprestimoService {
    @Autowired
    private EmprestimoRepository emprestimoRepository;
    
    @Autowired
    private CiclistaService ciclistaService;
    
    @Autowired
    private TrancaService trancaService;
    
    @Autowired
    private BicicletaService bicicletaService;
    
    @Autowired
    @Lazy
    private CartaoCreditoService cartaoCreditoService;
    
    @Autowired
    private NotificacaoService notificacaoService;

    private static final double TAXA_INICIAL = 10.0;
    private static final double TAXA_EXTRA_POR_HORA = 5.0;
    private static final long DURACAO_PADRAO_HORAS = 2;

    public List<Emprestimo> buscarHistoricoEmprestimos(Long ciclistaId) {
        return emprestimoRepository.findByCiclistaId(ciclistaId);
    }

    private boolean isCiclistaDoEmprestimo(Ciclista ciclista, Emprestimo emprestimo) {
        Ciclista ciclistaEmprestimo = emprestimo.getCiclista();
        
        if (ciclistaEmprestimo instanceof Brasileiro && ciclista instanceof Brasileiro) {
            return ((Brasileiro) ciclistaEmprestimo).getCpf().equals(((Brasileiro) ciclista).getCpf());
        }
        
        if (ciclistaEmprestimo instanceof Estrangeiro && ciclista instanceof Estrangeiro) {
            return ((Estrangeiro) ciclistaEmprestimo).getPassaporte()
                   .equals(((Estrangeiro) ciclista).getPassaporte());
        }
        
        return false;
    }

    @Transactional
    public EmprestimoDTO realizarEmprestimo(EmprestimoRequestDTO requestDTO) {
        Ciclista ciclista = ciclistaService.buscarCiclista(requestDTO.getIdentificacaoCiclista());
        
        if (emprestimoRepository.findByCiclistaIdAndStatus(
                ciclista.getId(), StatusEmprestimo.EM_ANDAMENTO).isPresent()) {
            throw new RuntimeException("Ciclista já possui um empréstimo em andamento");
        }

        Tranca tranca = trancaService.buscarPorId(requestDTO.getTrancaId())
            .orElseThrow(() -> new RuntimeException("Tranca não encontrada"));
        
        Totem totem = tranca.getTotem();

        if (tranca.getStatus() != StatusTranca.OCUPADA) {
            throw new RuntimeException("Não há bicicleta disponível nesta tranca");
        }

        Bicicleta bicicleta = tranca.getBicicleta();
        if (bicicleta == null) {
            throw new RuntimeException("Bicicleta não encontrada na tranca");
        }

        boolean pagamentoRealizado = cartaoCreditoService.realizarCobranca(ciclista, TAXA_INICIAL);
        if (!pagamentoRealizado) {
            throw new RuntimeException("Não foi possível realizar o pagamento");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setCiclista(ciclista);
        emprestimo.setBicicleta(bicicleta);
        emprestimo.setTrancaInicio(tranca);
        emprestimo.setTotemInicio(totem);
        emprestimo.setHoraInicio(LocalDateTime.now());
        emprestimo.setTaxaInicial(TAXA_INICIAL);
        emprestimo.setStatus(StatusEmprestimo.EM_ANDAMENTO);

        emprestimo = emprestimoRepository.save(emprestimo);

        bicicleta.setStatus(StatusBicicleta.EM_USO);
        bicicletaService.atualizarStatus(bicicleta.getId(), StatusBicicleta.EM_USO);

        tranca.setBicicleta(null);
        trancaService.atualizarStatus(tranca.getId(), StatusTranca.LIVRE);

        notificacaoService.notificarEmprestimoRealizado(emprestimo);

        System.out.println("Empréstimo realizado com sucesso para ciclista: " + ciclista.getNome());
        return convertToDTO(emprestimo);
    }

    @Transactional
    public EmprestimoDTO finalizarEmprestimo(Long emprestimoId, Long trancaId, String identificacaoCiclista) {
        System.out.println("Iniciando finalização do empréstimo para ciclista: " + identificacaoCiclista);

        // Validar ciclista
        Ciclista ciclista = ciclistaService.buscarCiclista(identificacaoCiclista);
        System.out.println("Buscando empréstimo ativo para ciclista ID: " + ciclista.getId());

        // Buscar empréstimo ativo do ciclista
        Emprestimo emprestimo = emprestimoRepository
            .findByCiclistaIdAndStatus(ciclista.getId(), StatusEmprestimo.EM_ANDAMENTO)
            .orElseThrow(() -> new RuntimeException("Nenhum empréstimo em andamento encontrado para este ciclista"));

        System.out.println("Empréstimo ativo encontrado - ID: " + emprestimo.getId());

        // Buscar e validar tranca
        Tranca trancaFim = trancaService.buscarPorId(trancaId)
                .orElseThrow(() -> new RuntimeException("Tranca não encontrada"));

        if (trancaFim.getStatus() != StatusTranca.LIVRE) {
            throw new RuntimeException("Esta tranca não está disponível para devolução");
        }

        // Validar bicicleta
        Bicicleta bicicleta = emprestimo.getBicicleta();
        if (bicicleta == null) {
            throw new RuntimeException("Bicicleta não encontrada no empréstimo");
        }

        // Atualizar dados
        LocalDateTime horaFim = LocalDateTime.now();
        emprestimo.setHoraFim(horaFim);
        emprestimo.setTrancaFim(trancaFim);
        emprestimo.setTotemFim(trancaFim.getTotem());

        // Processar cobrança extra
        long horasDeUso = Duration.between(emprestimo.getHoraInicio(), horaFim).toHours();
        if (horasDeUso > DURACAO_PADRAO_HORAS) {
            long horasExtras = horasDeUso - DURACAO_PADRAO_HORAS;
            double taxaExtra = horasExtras * TAXA_EXTRA_POR_HORA;
            emprestimo.setTaxaExtra(taxaExtra);
            cartaoCreditoService.realizarCobranca(ciclista, taxaExtra);
        }

        // Atualizar status
        emprestimo.setStatus(StatusEmprestimo.CONCLUIDO);
        bicicleta.setStatus(StatusBicicleta.DISPONIVEL);
        
        // Salvar alterações
        bicicletaService.atualizarStatus(bicicleta.getId(), StatusBicicleta.DISPONIVEL);
        trancaService.associarBicicleta(trancaId, bicicleta.getId());
        emprestimo = emprestimoRepository.save(emprestimo);

        notificacaoService.notificarDevolucaoRealizada(emprestimo);

        System.out.println("Empréstimo " + emprestimo.getId() + 
                         " finalizado com sucesso por ciclista: " + ciclista.getNome() +
                         " (ID: " + ciclista.getId() + ")");
        return convertToDTO(emprestimo);
    }

    public EmprestimoDTO convertToDTO(Emprestimo emprestimo) {
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