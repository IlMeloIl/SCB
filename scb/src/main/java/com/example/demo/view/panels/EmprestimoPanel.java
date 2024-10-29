package com.example.demo.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.springframework.http.ResponseEntity;

import java.awt.*;
import com.example.demo.view.WindowManager;
import com.example.demo.view.utils.ColorScheme;
import com.example.demo.controller.EmprestimoController;
import com.example.demo.controller.TotemController;
import com.example.demo.controller.CiclistaController;
import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.dto.EmprestimoRequestDTO;
import com.example.demo.model.*;
import java.util.List;

public class EmprestimoPanel extends JPanel {
	private WindowManager windowManager;
	private EmprestimoController emprestimoController;
	private TotemController totemController;
	private CiclistaController ciclistaController;

	private JTable bicicletasTable;
	private DefaultTableModel tableModel;
	private JComboBox<TotemComboItem> totemComboBox;
	private JLabel statusLabel;

	public EmprestimoPanel(WindowManager windowManager, EmprestimoController emprestimoController,
			TotemController totemController, CiclistaController ciclistaController) {
		this.windowManager = windowManager;
		this.emprestimoController = emprestimoController;
		this.totemController = totemController;
		this.ciclistaController = ciclistaController;
		setupUI();
	}

	private void setupUI() {
		setLayout(new BorderLayout(10, 10));
		setBackground(ColorScheme.BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// Painel superior com título e instruções
		JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
		headerPanel.setBackground(ColorScheme.BACKGROUND);

		// Título
		JLabel titleLabel = new JLabel("Novo Empréstimo");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setForeground(ColorScheme.PRIMARY);

		// Informações de custo
		JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		infoPanel.setBackground(ColorScheme.BACKGROUND);
		JLabel custoLabel = new JLabel("Custo: R$ 10,00 (primeiras 2 horas)");
		JLabel custoExtraLabel = new JLabel("Taxa adicional: R$ 5,00 por hora excedente");
		infoPanel.add(custoLabel);
		infoPanel.add(custoExtraLabel);

		headerPanel.add(titleLabel, BorderLayout.NORTH);
		headerPanel.add(infoPanel, BorderLayout.CENTER);

		// Painel de seleção do totem
		JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		selectionPanel.setBackground(ColorScheme.BACKGROUND);

		JLabel totemLabel = new JLabel("Selecione o Totem:");
		totemComboBox = new JComboBox<>();
		totemComboBox.setPreferredSize(new Dimension(300, 25));
		totemComboBox.addActionListener(e -> loadBicicletas());

		statusLabel = new JLabel("Selecione um totem para ver as bicicletas disponíveis");
		statusLabel.setForeground(ColorScheme.PRIMARY);

		selectionPanel.add(totemLabel);
		selectionPanel.add(totemComboBox);
		selectionPanel.add(statusLabel);

		headerPanel.add(selectionPanel, BorderLayout.SOUTH);

		// Tabela de bicicletas
		String[] columns = { "Nº Tranca", "Nº Bicicleta", "Marca", "Modelo", "Ano" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		bicicletasTable = new JTable(tableModel);
		bicicletasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bicicletasTable.setRowHeight(25);

		// Ajuste das cores do cabeçalho
		JTableHeader header = bicicletasTable.getTableHeader();
		header.setBackground(ColorScheme.PRIMARY);
		header.setForeground(Color.BLACK);
		header.setFont(new Font("Arial", Font.BOLD, 12));

		// Painel central com a tabela
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(ColorScheme.BACKGROUND);
		tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorScheme.PRIMARY),
				"Bicicletas Disponíveis"));
		tablePanel.add(new JScrollPane(bicicletasTable), BorderLayout.CENTER);

		// Painel de botões
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(ColorScheme.BACKGROUND);

		JButton confirmarButton = new JButton("Confirmar Empréstimo");
		confirmarButton.setBackground(ColorScheme.PRIMARY);
		confirmarButton.setForeground(Color.WHITE);
		confirmarButton.addActionListener(e -> realizarEmprestimo());

		JButton cancelarButton = new JButton("Voltar");
		cancelarButton.setBackground(ColorScheme.SECONDARY);
		cancelarButton.setForeground(Color.WHITE);
		cancelarButton.addActionListener(e -> windowManager.showDashboard());

		buttonPanel.add(confirmarButton);
		buttonPanel.add(cancelarButton);

		// Adicionar todos os painéis
		add(headerPanel, BorderLayout.NORTH);
		add(tablePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Carregar totens inicialmente
		loadTotens();
	}

	private void loadTotens() {
		totemComboBox.removeAllItems();
		try {
			List<Totem> totens = totemController.listarTodos();
			for (Totem totem : totens) {
				totemComboBox.addItem(new TotemComboItem(totem));
			}
		} catch (Exception e) {
			windowManager.showError("Erro ao carregar totens: " + e.getMessage());
		}
	}

	private void loadBicicletas() {
		tableModel.setRowCount(0);
		TotemComboItem selectedTotem = (TotemComboItem) totemComboBox.getSelectedItem();

		if (selectedTotem != null) {
			try {
				Totem totem = totemController.buscarPorId(selectedTotem.getId()).getBody();
				if (totem != null) {
					for (Tranca tranca : totem.getTrancas()) {
						if (tranca.getStatus() == StatusTranca.OCUPADA && tranca.getBicicleta() != null
								&& tranca.getBicicleta().getStatus() == StatusBicicleta.DISPONIVEL) {

							Bicicleta bicicleta = tranca.getBicicleta();
							tableModel.addRow(new Object[] { tranca.getNumero(), bicicleta.getNumero(),
									bicicleta.getMarca(), bicicleta.getModelo(), bicicleta.getAno() });
						}
					}
					updateStatus();
				}
			} catch (Exception e) {
				windowManager.showError("Erro ao carregar bicicletas: " + e.getMessage());
			}
		}
	}

	private void updateStatus() {
		int bicicletasDisponiveis = tableModel.getRowCount();
		if (bicicletasDisponiveis > 0) {
			statusLabel.setText(bicicletasDisponiveis + " bicicleta(s) disponível(is)");
			statusLabel.setForeground(ColorScheme.SUCCESS);
		} else {
			statusLabel.setText("Nenhuma bicicleta disponível neste totem");
			statusLabel.setForeground(ColorScheme.ERROR);
		}
	}

	private void realizarEmprestimo() {
		// Verificar se tem cartão principal
		try {
			String userEmail = windowManager.getCurrentUserEmail();
			String userDocument = windowManager.getCurrentUserDocument();
			List<CartaoCredito> cartoes = ciclistaController.listarCartoes(userDocument).getBody();
			boolean temCartaoPrincipal = cartoes != null && cartoes.stream().anyMatch(CartaoCredito::isPrincipal);

			if (!temCartaoPrincipal) {
				int option = JOptionPane
						.showConfirmDialog(this,
								"É necessário ter um cartão de crédito principal para realizar empréstimos.\n"
										+ "Deseja adicionar um cartão agora?",
								"Cartão Necessário", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					windowManager.showPerfil();
				}
				return;
			}

			int selectedRow = bicicletasTable.getSelectedRow();
			if (selectedRow == -1) {
				windowManager.showError("Selecione uma bicicleta para realizar o empréstimo");
				return;
			}

			String numeroTranca = (String) tableModel.getValueAt(selectedRow, 0);
			String numeroBicicleta = (String) tableModel.getValueAt(selectedRow, 1);

			int confirm = JOptionPane.showConfirmDialog(this,
					"Confirmar empréstimo da bicicleta " + numeroBicicleta + "?\n"
							+ "Será cobrada uma taxa inicial de R$ 10,00",
					"Confirmação de Empréstimo", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				TotemComboItem selectedTotem = (TotemComboItem) totemComboBox.getSelectedItem();
				Totem totem = totemController.buscarPorId(selectedTotem.getId()).getBody();

				// Encontrar o ID da tranca
				Long trancaId = totem.getTrancas().stream().filter(t -> t.getNumero().equals(numeroTranca)).findFirst()
						.map(Tranca::getId).orElseThrow(() -> new RuntimeException("Tranca não encontrada"));

				EmprestimoRequestDTO requestDTO = new EmprestimoRequestDTO();
				requestDTO.setIdentificacaoCiclista(windowManager.getCurrentUserDocument());
				requestDTO.setTrancaId(trancaId);

				ResponseEntity<EmprestimoDTO> response = emprestimoController.realizarEmprestimo(requestDTO);

				if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
					EmprestimoDTO emprestimo = response.getBody();
					windowManager.updateEmprestimoStatus(true, "Bicicleta " + numeroBicicleta);
					windowManager.setCurrentEmprestimoId(emprestimo.getId()); // Salvar o ID
					windowManager.showSuccess("Empréstimo realizado com sucesso!");
					windowManager.showDashboard();
				}
			}
		} catch (Exception e) {
			windowManager.showError("Erro ao realizar empréstimo: " + e.getMessage());
		}
	}

	// Classe auxiliar para o ComboBox de totens
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
			return totem.getLocalizacao() + " - "
					+ totem.getTrancas().stream()
							.filter(t -> t.getStatus() == StatusTranca.OCUPADA && t.getBicicleta() != null
									&& t.getBicicleta().getStatus() == StatusBicicleta.DISPONIVEL)
							.count()
					+ " bicicletas disponíveis";
		}
	}
}