package com.example.demo.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import com.example.demo.view.WindowManager;
import com.example.demo.view.utils.ColorScheme;
import com.example.demo.controller.TotemController;
import com.example.demo.model.*;
import java.util.List;

public class TotemListPanel extends JPanel {
    private WindowManager windowManager;
    private TotemController totemController;
    private JTable totemTable;
    private DefaultTableModel tableModel;
    private JTable trancasTable;
    private DefaultTableModel trancasTableModel;
    private JLabel detailsLabel;
    private JTextField searchField;

    public TotemListPanel(WindowManager windowManager, TotemController totemController) {
        this.windowManager = windowManager;
        this.totemController = totemController;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ColorScheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Painel superior com título, busca e refresh
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(ColorScheme.BACKGROUND);

        // Título
        JLabel titleLabel = new JLabel("Totens Disponíveis");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(ColorScheme.PRIMARY);

        // Painel de busca e refresh
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(ColorScheme.BACKGROUND);

        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Buscar por localização...");
        
        JButton refreshButton = new JButton("Atualizar");
        refreshButton.setBackground(ColorScheme.PRIMARY);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> refreshTotemList());
        
        JButton backButton = new JButton("Voltar");
        backButton.setBackground(ColorScheme.SECONDARY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> windowManager.showDashboard());

        actionPanel.add(searchField);
        actionPanel.add(refreshButton);
        actionPanel.add(backButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        // Configuração da tabela de totens
        String[] columns = {"ID", "Localização", "Descrição", "Trancas Totais", "Trancas Livres"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        totemTable = new JTable(tableModel);
        totemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        totemTable.setRowHeight(25);
//        totemTable.getTableHeader().setBackground(ColorScheme.PRIMARY);
//        totemTable.getTableHeader().setForeground(Color.WHITE);
        
        JTableHeader header = totemTable.getTableHeader();
        header.setBackground(ColorScheme.PRIMARY);
        header.setForeground(Color.BLACK); // Alterado para preto
        header.setFont(new Font("Arial", Font.BOLD, 12));

        // Adiciona ordenação à tabela
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        totemTable.setRowSorter(sorter);

        // Campo de busca funcionando
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                }
            }
        });

        // Configuração da tabela de trancas
        String[] trancaColumns = {"Nº Tranca", "Status", "Bicicleta", "Marca", "Modelo", "Ano"};
        trancasTableModel = new DefaultTableModel(trancaColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        trancasTable = new JTable(trancasTableModel);
        trancasTable.setRowHeight(25);
        JTableHeader trancaHeader = trancasTable.getTableHeader();
        trancaHeader.setBackground(ColorScheme.SECONDARY);
        trancaHeader.setForeground(Color.BLACK);
        trancaHeader.setFont(new Font("Arial", Font.BOLD, 12));

        // Label para detalhes
        detailsLabel = new JLabel("Selecione um totem para ver os detalhes");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsLabel.setForeground(ColorScheme.PRIMARY);

        // Painel de detalhes
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBackground(ColorScheme.BACKGROUND);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Detalhes do Totem"));
        detailsPanel.add(detailsLabel, BorderLayout.NORTH);
        detailsPanel.add(new JScrollPane(trancasTable), BorderLayout.CENTER);

        // Listener para seleção na tabela de totens
        totemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showTotemDetails();
            }
        });

        // Layout principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(totemTable),
            detailsPanel);
        splitPane.setDividerLocation(250);

        add(headerPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    public void refreshTotemList() {
        tableModel.setRowCount(0);
        try {
            List<Totem> totens = totemController.listarTodos();
            for (Totem totem : totens) {
                int totalTrancas = totem.getTrancas().size();
                long trancasLivres = totem.getTrancas().stream()
                    .filter(t -> t.getStatus() == StatusTranca.LIVRE)
                    .count();

                tableModel.addRow(new Object[]{
                    totem.getId(),
                    totem.getLocalizacao(),
                    totem.getDescricao(),
                    totalTrancas,
                    trancasLivres
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar totens: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTotemDetails() {
        int selectedRow = totemTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedRow = totemTable.convertRowIndexToModel(selectedRow);
            Long totemId = (Long) tableModel.getValueAt(selectedRow, 0);
            try {
                Totem totem = totemController.buscarPorId(totemId).getBody();
                if (totem != null) {
                    detailsLabel.setText("Detalhes do Totem: " + totem.getLocalizacao());
                    updateTrancasTable(totem);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao carregar detalhes do totem: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTrancasTable(Totem totem) {
        trancasTableModel.setRowCount(0);
        for (Tranca tranca : totem.getTrancas()) {
            Bicicleta bicicleta = tranca.getBicicleta();
            
            trancasTableModel.addRow(new Object[]{
                tranca.getNumero(),
                tranca.getStatus(),
                bicicleta != null ? bicicleta.getNumero() : "Livre",
                bicicleta != null ? bicicleta.getMarca() : "-",
                bicicleta != null ? bicicleta.getModelo() : "-",
                bicicleta != null ? bicicleta.getAno() : "-"
            });
        }
    }
}