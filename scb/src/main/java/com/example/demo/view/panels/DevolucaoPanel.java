package com.example.demo.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import com.example.demo.view.WindowManager;
import com.example.demo.view.utils.ColorScheme;
import com.example.demo.controller.EmprestimoController;
import com.example.demo.controller.TotemController;
import com.example.demo.model.*;
import com.example.demo.dto.EmprestimoDTO;
import org.springframework.http.ResponseEntity;
import java.util.List;

public class DevolucaoPanel extends JPanel {
    private WindowManager windowManager;
    private EmprestimoController emprestimoController;
    private TotemController totemController;
    
    private JLabel tempoCorrido;
    private JLabel taxaExtra;
    private JLabel valorTotal;
    private JComboBox<TotemComboItem> totemComboBox;
    private JTable trancasTable;
    private DefaultTableModel trancasTableModel;
    private Timer timer;
    
    private LocalDateTime horaInicio;
    private Double taxaInicial;

    public DevolucaoPanel(WindowManager windowManager, EmprestimoController emprestimoController,
                         TotemController totemController) {
        this.windowManager = windowManager;
        this.emprestimoController = emprestimoController;
        this.totemController = totemController;
        setupUI();
        iniciarTimer();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ColorScheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Cabeçalho com título e informações
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(ColorScheme.BACKGROUND);

        JLabel titleLabel = new JLabel("Devolução de Bicicleta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(ColorScheme.PRIMARY);

        // Painel de informações
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBackground(ColorScheme.BACKGROUND);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY),
            "Informações do Empréstimo"
        ));

        tempoCorrido = new JLabel("Tempo de uso: 0h 0min");
        taxaExtra = new JLabel("Taxa extra: R$ 0,00");
        valorTotal = new JLabel("Valor total: R$ 0,00");

        infoPanel.add(tempoCorrido);
        infoPanel.add(new JLabel("Taxa inicial: R$ 10,00"));
        infoPanel.add(taxaExtra);
        infoPanel.add(valorTotal);

        // Seleção de totem
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(ColorScheme.BACKGROUND);

        totemComboBox = new JComboBox<>();
        totemComboBox.setPreferredSize(new Dimension(300, 25));
        totemComboBox.addActionListener(e -> carregarTrancas());

        selectionPanel.add(new JLabel("Selecione o totem para devolução:"));
        selectionPanel.add(totemComboBox);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        topPanel.add(selectionPanel, BorderLayout.SOUTH);

        // Tabela de trancas
        setupTranchTable();

        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton devolverButton = new JButton("Devolver Bicicleta");
        devolverButton.setBackground(ColorScheme.PRIMARY);
        devolverButton.setForeground(Color.WHITE);
        devolverButton.addActionListener(e -> realizarDevolucao());

        JButton cancelarButton = new JButton("Voltar");
        cancelarButton.setBackground(ColorScheme.SECONDARY);
        cancelarButton.setForeground(Color.WHITE);
        cancelarButton.addActionListener(e -> windowManager.showDashboard());

        buttonPanel.add(devolverButton);
        buttonPanel.add(cancelarButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(trancasTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupTranchTable() {
        String[] columns = {"Nº Tranca", "Status", "Localização na Estação"};
        trancasTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        trancasTable = new JTable(trancasTableModel);
        trancasTable.setRowHeight(25);

        JTableHeader header = trancasTable.getTableHeader();
        header.setBackground(ColorScheme.PRIMARY);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 12));
    }

    public void carregarDados() {
        try {
            Long emprestimoId = windowManager.getCurrentEmprestimoId();
            if (emprestimoId == null) {
                return; // Se não houver empréstimo ativo, apenas retorna
            }

            ResponseEntity<EmprestimoDTO> response = emprestimoController.buscarEmprestimo(emprestimoId);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                EmprestimoDTO emprestimo = response.getBody();
                horaInicio = emprestimo.getHoraInicio();
                taxaInicial = emprestimo.getTaxaInicial();
                
                atualizarInfoTempo();

                // Carregar totens
                List<Totem> totens = totemController.listarTodos();
                totemComboBox.removeAllItems();
                
                for (Totem totem : totens) {
                    totemComboBox.addItem(new TotemComboItem(totem));
                }
            } else {
                windowManager.showError("Não foi possível encontrar o empréstimo");
                windowManager.showDashboard();
            }
        } catch (Exception e) {
            windowManager.showError("Erro ao carregar dados: " + e.getMessage());
            windowManager.showDashboard();
        }
    }

    private void carregarTrancas() {
        trancasTableModel.setRowCount(0);
        TotemComboItem selectedTotem = (TotemComboItem) totemComboBox.getSelectedItem();
        
        if (selectedTotem != null) {
            try {
                ResponseEntity<Totem> response = totemController.buscarPorId(selectedTotem.getId());
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Totem totem = response.getBody();
                    for (Tranca tranca : totem.getTrancas()) {
                        if (tranca.getStatus() == StatusTranca.LIVRE) {
                            trancasTableModel.addRow(new Object[]{
                                tranca.getNumero(),
                                tranca.getStatus(),
                                "Posição " + tranca.getNumero()
                            });
                        }
                    }
                }
            } catch (Exception e) {
                windowManager.showError("Erro ao carregar trancas: " + e.getMessage());
            }
        }
    }

    private void realizarDevolucao() {
        int selectedRow = trancasTable.getSelectedRow();
        if (selectedRow == -1) {
            windowManager.showError("Selecione uma tranca para devolver a bicicleta");
            return;
        }

        try {
            String numeroTranca = (String) trancasTableModel.getValueAt(selectedRow, 0);
            TotemComboItem selectedTotem = (TotemComboItem) totemComboBox.getSelectedItem();
            
            if (selectedTotem == null) {
                windowManager.showError("Selecione um totem para devolução");
                return;
            }

            ResponseEntity<Totem> response = totemController.buscarPorId(selectedTotem.getId());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Totem totem = response.getBody();
                Tranca tranca = totem.getTrancas().stream()
                    .filter(t -> t.getNumero().equals(numeroTranca))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Tranca não encontrada"));

                // Realizar a devolução
                ResponseEntity<EmprestimoDTO> devolucaoResponse = emprestimoController
                    .finalizarEmprestimo(windowManager.getCurrentEmprestimoId(), tranca.getId());

                if (devolucaoResponse.getStatusCode().is2xxSuccessful()) {
                    windowManager.updateEmprestimoStatus(false, null);
                    windowManager.setCurrentEmprestimoId(null);
                    
                    windowManager.showSuccess("Bicicleta devolvida com sucesso!");
                    windowManager.showDashboard();
                }
            }
        } catch (Exception e) {
            windowManager.showError("Erro ao realizar devolução: " + e.getMessage());
        }
    }

    private void iniciarTimer() {
        timer = new Timer(60000, e -> atualizarInfoTempo()); // Atualiza a cada minuto
        timer.start();
    }

    private void atualizarInfoTempo() {
        if (horaInicio != null) {
            Duration duracao = Duration.between(horaInicio, LocalDateTime.now());
            long horas = duracao.toHours();
            long minutos = duracao.toMinutesPart();

            tempoCorrido.setText(String.format("Tempo de uso: %dh %dmin", horas, minutos));

            // Calcular taxa extra se passou de 2 horas
            double taxaExtraValor = 0.0;
            if (horas > 2) {
                taxaExtraValor = (horas - 2) * 5.0; // R$ 5,00 por hora extra
                taxaExtra.setText(String.format("Taxa extra: R$ %.2f", taxaExtraValor));
            }

            valorTotal.setText(String.format("Valor total: R$ %.2f", 10.0 + taxaExtraValor));
        }
    }

    // Classe auxiliar para itens do combo de totens
    private static class TotemComboItem {
        private final Totem totem;

        public TotemComboItem(Totem totem) {
            this.totem = totem;
        }

        public Long getId() {
            return totem.getId();
        }

        @Override
        public String toString() {
            return String.format("%s - %d trancas livres", 
                totem.getLocalizacao(),
                totem.getTrancas().stream()
                    .filter(t -> t.getStatus() == StatusTranca.LIVRE)
                    .count()
            );
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (timer != null) {
            timer.stop();
        }
    }
}