package com.example.demo.view.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.springframework.http.ResponseEntity;

import java.awt.*;
import java.time.format.DateTimeFormatter;

import com.example.demo.view.WindowManager;
import com.example.demo.view.utils.ColorScheme;
import com.example.demo.controller.CiclistaController;
import com.example.demo.dto.CiclistaAtualizacaoDTO;
import com.example.demo.dto.EmprestimoDTO;
import com.example.demo.dto.CartaoCreditoDTO;
import com.example.demo.model.CartaoCredito;
import com.example.demo.model.Ciclista;

import java.util.List;

public class PerfilPanel extends JPanel {
	private WindowManager windowManager;
	private CiclistaController ciclistaController;

	// Campos de informação pessoal
	private JTextField nomeField;
	private JTextField emailField;
	private JTextField telefoneField;
	private JLabel documentoLabel;
	private boolean editMode = false;

	// Tabela de cartões
	private JTable cartoesTable;
	private DefaultTableModel cartoesTableModel;

	private JTable historicoTable;
	private DefaultTableModel historicoTableModel;

	// Painéis
	private JPanel infoPanel;
	private JPanel cartoesPanel;

	/**
     * Construtor do painel de perfil
     * 
     * @param windowManager gerenciador de janelas do sistema
     * @param ciclistaController controlador de operações do ciclista
     */
	public PerfilPanel(WindowManager windowManager, CiclistaController ciclistaController) {
		this.windowManager = windowManager;
		this.ciclistaController = ciclistaController;
		setupUI();
	}

	/**
     * Configura a interface gráfica principal do painel
     * Inicializa todos os componentes e define o layout
     */
	private void setupUI() {
		setLayout(new BorderLayout(10, 10));
		setBackground(ColorScheme.BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// Cabeçalho
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(ColorScheme.BACKGROUND);

		JLabel titleLabel = new JLabel("Meu Perfil");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setForeground(ColorScheme.PRIMARY);

		JButton voltarButton = new JButton("Voltar");
		voltarButton.setBackground(ColorScheme.SECONDARY);
		voltarButton.setForeground(Color.WHITE);
		voltarButton.addActionListener(e -> windowManager.showDashboard());

		headerPanel.add(titleLabel, BorderLayout.WEST);
		headerPanel.add(voltarButton, BorderLayout.EAST);

		// Painel principal com divisão vertical
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setDividerLocation(200);
		mainSplitPane.setResizeWeight(0.3);

		// Painel superior com divisão horizontal
		JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		// Painéis de informações e cartões com mesmo tamanho
		JPanel infoPanel = setupInfoPanel();
		JPanel cartoesPanel = setupCartoesPanel();

		// Definir tamanhos preferidos iguais
		Dimension preferredSize = new Dimension(400, 200);
		infoPanel.setPreferredSize(preferredSize);
		cartoesPanel.setPreferredSize(preferredSize);

		topSplitPane.setLeftComponent(infoPanel);
		topSplitPane.setRightComponent(cartoesPanel);

		// Configurar a divisão exatamente no meio
		topSplitPane.setResizeWeight(0.5);

		// Configurar o tamanho inicial da divisão
		SwingUtilities.invokeLater(() -> {
			int width = topSplitPane.getWidth();
			topSplitPane.setDividerLocation(width / 2);
		});

		// Adiciona o histórico
		JPanel historicoPanel = setupHistoricoPanel();

		// Configura o split principal
		mainSplitPane.setTopComponent(topSplitPane);
		mainSplitPane.setBottomComponent(historicoPanel);

		add(headerPanel, BorderLayout.NORTH);
		add(mainSplitPane, BorderLayout.CENTER);
	}

	/**
     * Configura o painel de informações pessoais
     * 
     * @return JPanel painel configurado com campos de informação pessoal
     */
	private JPanel setupInfoPanel() {
		infoPanel = new JPanel(new BorderLayout(10, 10));
		infoPanel.setBackground(ColorScheme.BACKGROUND);
		infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorScheme.PRIMARY),
				"Informações Pessoais"));

		// Campos de informação
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(ColorScheme.BACKGROUND);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		// Nome
		gbc.gridx = 0;
		gbc.gridy = 0;
		formPanel.add(new JLabel("Nome:"), gbc);
		nomeField = new JTextField(30);
		nomeField.setEditable(false);
		gbc.gridx = 1;
		formPanel.add(nomeField, gbc);

		// Email
		gbc.gridx = 0;
		gbc.gridy = 1;
		formPanel.add(new JLabel("Email:"), gbc);
		emailField = new JTextField(30);
		emailField.setEditable(false);
		gbc.gridx = 1;
		formPanel.add(emailField, gbc);

		// Telefone
		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(new JLabel("Telefone:"), gbc);
		telefoneField = new JTextField(30);
		telefoneField.setEditable(false);
		gbc.gridx = 1;
		formPanel.add(telefoneField, gbc);

		// Documento
		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(new JLabel("Documento:"), gbc);
		documentoLabel = new JLabel();
		documentoLabel.setForeground(ColorScheme.TEXT);
		gbc.gridx = 1;
		formPanel.add(documentoLabel, gbc);

		// Botões de ação
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBackground(ColorScheme.BACKGROUND);

		JButton editarButton = new JButton("Editar");
		editarButton.setBackground(ColorScheme.PRIMARY);
		editarButton.setForeground(Color.WHITE);
		editarButton.addActionListener(e -> toggleEditMode(true));

		JButton salvarButton = new JButton("Salvar");
		salvarButton.setBackground(ColorScheme.SUCCESS);
		salvarButton.setForeground(Color.WHITE);
		salvarButton.setVisible(false);
		salvarButton.addActionListener(e -> salvarAlteracoes());

		JButton cancelarButton = new JButton("Cancelar");
		cancelarButton.setBackground(ColorScheme.ERROR);
		cancelarButton.setForeground(Color.WHITE);
		cancelarButton.setVisible(false);
		cancelarButton.addActionListener(e -> toggleEditMode(false));

		buttonPanel.add(editarButton);
		buttonPanel.add(salvarButton);
		buttonPanel.add(cancelarButton);

		infoPanel.add(formPanel, BorderLayout.CENTER);
		infoPanel.add(buttonPanel, BorderLayout.SOUTH);

		return infoPanel;
	}

	/**
     * Configura o painel de cartões de crédito
     * 
     * @return JPanel painel configurado com lista de cartões e ações
     */
	private JPanel setupCartoesPanel() {
		cartoesPanel = new JPanel(new BorderLayout(10, 10));
		cartoesPanel.setBackground(ColorScheme.BACKGROUND);
		cartoesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorScheme.PRIMARY),
				"Cartões de Crédito"));

		// Tabela de cartões
		String[] columns = { "Número", "Nome do Titular", "Validade", "Principal" };
		cartoesTableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		cartoesTable = new JTable(cartoesTableModel);
		cartoesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cartoesTable.setRowHeight(25);

		// Ajuste das cores do cabeçalho
		JTableHeader header = cartoesTable.getTableHeader();
		header.setBackground(ColorScheme.PRIMARY);
		header.setForeground(Color.BLACK);
		header.setFont(new Font("Arial", Font.BOLD, 12));

		// Botões de ação para cartões
		JPanel cartoesBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		cartoesBtnPanel.setBackground(ColorScheme.BACKGROUND);

		JButton adicionarCartaoBtn = new JButton("Adicionar Cartão");
		adicionarCartaoBtn.setBackground(ColorScheme.PRIMARY);
		adicionarCartaoBtn.setForeground(Color.WHITE);
		adicionarCartaoBtn.addActionListener(e -> mostrarDialogNovoCartao());

		JButton definirPrincipalBtn = new JButton("Definir como Principal");
		definirPrincipalBtn.setBackground(ColorScheme.SECONDARY);
		definirPrincipalBtn.setForeground(Color.WHITE);
		definirPrincipalBtn.addActionListener(e -> definirCartaoPrincipal());

		JButton removerCartaoBtn = new JButton("Remover Cartão");
		removerCartaoBtn.setBackground(ColorScheme.ERROR);
		removerCartaoBtn.setForeground(Color.WHITE);
		removerCartaoBtn.addActionListener(e -> removerCartao());

		cartoesBtnPanel.add(adicionarCartaoBtn);
		cartoesBtnPanel.add(definirPrincipalBtn);
		cartoesBtnPanel.add(removerCartaoBtn);

		cartoesPanel.add(new JScrollPane(cartoesTable), BorderLayout.CENTER);
		cartoesPanel.add(cartoesBtnPanel, BorderLayout.SOUTH);

		return cartoesPanel;
	}

	/**
     * Configura o painel de histórico de empréstimos
     * 
     * @return JPanel painel configurado com histórico de transações
     */
	private JPanel setupHistoricoPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(ColorScheme.BACKGROUND);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ColorScheme.PRIMARY),
				"Histórico de Empréstimos"));

		// Configurar tabela de histórico
		String[] columns = { "Data/Hora Início", "Local Retirada", "Data/Hora Fim", "Local Devolução", "Valor Inicial",
				"Valor Extra", "Status" };
		historicoTableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		historicoTable = new JTable(historicoTableModel);
		historicoTable.setRowHeight(25);

		// Configurar cabeçalho da tabela
		JTableHeader header = historicoTable.getTableHeader();
		header.setBackground(ColorScheme.PRIMARY);
		header.setForeground(Color.BLACK);
		header.setFont(new Font("Arial", Font.BOLD, 12));

		// Adicionar tabela ao painel com scroll
		panel.add(new JScrollPane(historicoTable), BorderLayout.CENTER);

		// Botão de atualizar
		JButton refreshButton = new JButton("Atualizar Histórico");
		refreshButton.setBackground(ColorScheme.PRIMARY);
		refreshButton.setForeground(Color.WHITE);
		refreshButton.addActionListener(e -> carregarHistorico());

		panel.add(refreshButton, BorderLayout.SOUTH);

		return panel;
	}

	/**
     * Carrega o histórico de empréstimos do usuário
     * Atualiza a tabela com os dados mais recentes do servidor
     */
	private void carregarHistorico() {
		try {
			String documento = windowManager.getCurrentUserDocument();
			if (documento == null)
				return;

			// Limpar tabela atual
			historicoTableModel.setRowCount(0);

			// Buscar histórico
			List<EmprestimoDTO> historico = ciclistaController.buscarHistoricoEmprestimos(documento).getBody();

			if (historico != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

				for (EmprestimoDTO emprestimo : historico) {
					historicoTableModel.addRow(new Object[] { emprestimo.getHoraInicio().format(formatter),
							"Totem " + emprestimo.getTotemInicioId(),
							emprestimo.getHoraFim() != null ? emprestimo.getHoraFim().format(formatter) : "-",
							emprestimo.getTotemFimId() != null ? "Totem " + emprestimo.getTotemFimId() : "-",
							String.format("R$ %.2f", emprestimo.getTaxaInicial()),
							emprestimo.getTaxaExtra() != null ? String.format("R$ %.2f", emprestimo.getTaxaExtra())
									: "-",
							emprestimo.getStatus() });
				}
			}
		} catch (Exception e) {
			windowManager.showError("Erro ao carregar histórico: " + e.getMessage());
		}
	}

	/**
     * Carrega os dados do usuário do servidor
     * Atualiza todos os campos e tabelas com as informações mais recentes
     */
	public void carregarDados() {
		try {
			String documento = windowManager.getCurrentUserDocument();
			if (documento == null || documento.isEmpty()) {
				windowManager.showError("Usuário não autenticado");
				windowManager.showLogin();
				return;
			}

			ResponseEntity<Ciclista> response = ciclistaController.buscarCiclista(documento);
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				Ciclista ciclista = response.getBody();

				nomeField.setText(ciclista.getNome());
				emailField.setText(ciclista.getEmail());
				telefoneField.setText(ciclista.getTelefone() != null ? ciclista.getTelefone() : "");
				documentoLabel.setText(documento);

				// Carregar cartões
				List<CartaoCredito> cartoes = ciclistaController.listarCartoes(documento).getBody();
				atualizarTabelaCartoes(cartoes);
				carregarHistorico();
			} else {
				// Se não encontrar o ciclista, usar os dados do WindowManager
				nomeField.setText(windowManager.getCurrentUserName());
				emailField.setText(windowManager.getCurrentUserEmail());
				documentoLabel.setText(documento);
			}
		} catch (Exception e) {
			windowManager.showError("Erro ao carregar dados: " + e.getMessage());
			windowManager.showDashboard();
		}
	}

	/**
     * Alterna o modo de edição dos campos de informação pessoal
     * 
     * @param edit true para habilitar edição, false para desabilitar
     */
	private void toggleEditMode(boolean edit) {
		editMode = edit;
		nomeField.setEditable(edit);
		emailField.setEditable(edit);
		telefoneField.setEditable(edit);

		// Alternar visibilidade dos botões
		Component[] components = ((JPanel) infoPanel.getComponent(1)).getComponents();
		for (Component c : components) {
			if (c instanceof JButton) {
				JButton button = (JButton) c;
				if (button.getText().equals("Editar")) {
					button.setVisible(!edit);
				} else {
					button.setVisible(edit);
				}
			}
		}

		if (!edit) {
			carregarDados(); // Recarregar dados originais ao cancelar
		}
	}
	
	/**
     * Salva as alterações realizadas no perfil
     * Envia os dados atualizados para o servidor
     */
	private void salvarAlteracoes() {
		try {
			String novoNome = nomeField.getText().trim();
			String novoEmail = emailField.getText().trim();
			String novoTelefone = telefoneField.getText().trim();

			// Validar campos obrigatórios
			if (novoNome.isEmpty() || novoEmail.isEmpty()) {
				windowManager.showError("Nome e email são obrigatórios");
				return;
			}

			CiclistaAtualizacaoDTO atualizacaoDTO = new CiclistaAtualizacaoDTO();
			atualizacaoDTO.setNome(novoNome);
			atualizacaoDTO.setEmail(novoEmail);
			// Só inclui o telefone se foi preenchido
			if (!novoTelefone.isEmpty()) {
				atualizacaoDTO.setTelefone(novoTelefone);
			}

			ResponseEntity<?> response = ciclistaController.atualizarCiclista(windowManager.getCurrentUserDocument(),
					atualizacaoDTO);

			if (response.getStatusCode().is2xxSuccessful()) {
				// Atualizar dados na sessão do WindowManager
				windowManager.updateUserInfo(novoEmail, novoNome);

				windowManager.showSuccess("Dados atualizados com sucesso!");
				toggleEditMode(false);
				carregarDados();
			}
		} catch (Exception e) {
			windowManager.showError("Erro ao atualizar dados: " + e.getMessage());
		}
	}

	/**
     * Atualiza a tabela de cartões de crédito
     * 
     * @param cartoes lista de cartões a serem exibidos
     */
	private void atualizarTabelaCartoes(List<CartaoCredito> cartoes) {
		cartoesTableModel.setRowCount(0);
		if (cartoes != null) {
			for (CartaoCredito cartao : cartoes) {
				cartoesTableModel
						.addRow(new Object[] { "•••• " + cartao.getNumero().substring(cartao.getNumero().length() - 4),
								cartao.getNomeTitular(), cartao.getValidade(), cartao.isPrincipal() ? "Sim" : "Não" });
			}
		}
	}

	/**
     * Exibe diálogo para adicionar novo cartão de crédito
     * Valida e processa os dados do novo cartão
     */
	private void mostrarDialogNovoCartao() {
		JDialog dialog = new JDialog(windowManager.getMainFrame(), "Novo Cartão", true);
		dialog.setLayout(new BorderLayout(10, 10));
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(this);

		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JTextField numeroField = new JTextField(16);
		JTextField nomeField = new JTextField(30);
		JTextField validadeField = new JTextField(7);
		JTextField cvvField = new JTextField(4);
		JCheckBox principalCheck = new JCheckBox("Definir como cartão principal");

		gbc.gridx = 0;
		gbc.gridy = 0;
		formPanel.add(new JLabel("Número do Cartão:"), gbc);
		gbc.gridx = 1;
		formPanel.add(numeroField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		formPanel.add(new JLabel("Nome do Titular:"), gbc);
		gbc.gridx = 1;
		formPanel.add(nomeField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(new JLabel("Validade (MM/AA):"), gbc);
		gbc.gridx = 1;
		formPanel.add(validadeField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(new JLabel("CVV:"), gbc);
		gbc.gridx = 1;
		formPanel.add(cvvField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		formPanel.add(principalCheck, gbc);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton salvarBtn = new JButton("Salvar");
		JButton cancelarBtn = new JButton("Cancelar");

		salvarBtn.addActionListener(e -> {
			try {
				CartaoCreditoDTO cartaoDTO = new CartaoCreditoDTO();
				cartaoDTO.setNumero(numeroField.getText());
				cartaoDTO.setNomeTitular(nomeField.getText());
				cartaoDTO.setValidade(validadeField.getText()); 
				cartaoDTO.setCvv(cvvField.getText());
				cartaoDTO.setPrincipal(principalCheck.isSelected());

				if (!validarValidade(validadeField.getText())) {
	                windowManager.showError("Data de validade inválida ou cartão expirado");
	                return;
	            }
				
				ciclistaController.adicionarCartaoCredito(windowManager.getCurrentUserDocument(), cartaoDTO);

				dialog.dispose();
				carregarDados();
				windowManager.showSuccess("Cartão adicionado com sucesso!");
			} catch (Exception ex) {
				windowManager.showError("Erro ao adicionar cartão: " + ex.getMessage());
			}
		});

		cancelarBtn.addActionListener(e -> dialog.dispose());

		buttonPanel.add(salvarBtn);
		buttonPanel.add(cancelarBtn);

		dialog.add(formPanel, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	/**
     * Define o cartão selecionado como principal
     * Atualiza o status no servidor e na interface
     */
	private void definirCartaoPrincipal() {
		int selectedRow = cartoesTable.getSelectedRow();
		if (selectedRow == -1) {
			windowManager.showError("Selecione um cartão para definir como principal");
			return;
		}

		try {
			// Obter todos os cartões para encontrar o ID do selecionado
			List<CartaoCredito> cartoes = ciclistaController.listarCartoes(windowManager.getCurrentUserDocument())
					.getBody();

			if (cartoes != null && selectedRow < cartoes.size()) {
				CartaoCredito cartaoSelecionado = cartoes.get(selectedRow);
				ciclistaController.definirCartaoPrincipal(windowManager.getCurrentUserDocument(),
						cartaoSelecionado.getId());

				carregarDados(); // Recarrega os dados para atualizar a tabela
				windowManager.showSuccess("Cartão definido como principal com sucesso!");
			}
		} catch (Exception e) {
			windowManager.showError("Erro ao definir cartão principal: " + e.getMessage());
		}
	}

	/**
     * Remove o cartão de crédito selecionado
     * Confirma a ação com o usuário antes de processar
     */
	private void removerCartao() {
		int selectedRow = cartoesTable.getSelectedRow();
		if (selectedRow == -1) {
			windowManager.showError("Selecione um cartão para remover");
			return;
		}

		// Verificar se é o cartão principal
		boolean isPrincipal = cartoesTableModel.getValueAt(selectedRow, 3).toString().equals("Sim");
		if (isPrincipal) {
			windowManager.showError(
					"Não é possível remover o cartão principal. " + "Defina outro cartão como principal primeiro.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este cartão?",
				"Confirmar Remoção", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			try {
				List<CartaoCredito> cartoes = ciclistaController.listarCartoes(windowManager.getCurrentUserDocument())
						.getBody();

				if (cartoes != null && selectedRow < cartoes.size()) {
					CartaoCredito cartaoSelecionado = cartoes.get(selectedRow);
					ciclistaController.removerCartaoCredito(windowManager.getCurrentUserDocument(),
							cartaoSelecionado.getId());

					carregarDados(); // Recarrega os dados para atualizar a tabela
					windowManager.showSuccess("Cartão removido com sucesso!");
				}
			} catch (Exception e) {
				windowManager.showError("Erro ao remover cartão: " + e.getMessage());
			}
		}
	}

	/**
     * Valida o número do cartão de crédito 
     * 
     * @param numero número do cartão a ser validado
     * @return boolean true se o número é válido
     */
	private boolean validarNumeroCartao(String numero) {
		return numero != null && numero.replaceAll("\\s", "").matches("\\d{16}")
				&& validarLuhn(numero.replaceAll("\\s", ""));
	}

	// Implementação do algoritmo de Luhn para validar número do cartão
	private boolean validarLuhn(String numero) {
		int sum = 0;
		boolean alternate = false;

		for (int i = numero.length() - 1; i >= 0; i--) {
			int n = Integer.parseInt(numero.substring(i, i + 1));
			if (alternate) {
				n *= 2;
				if (n > 9) {
					n = (n % 10) + 1;
				}
			}
			sum += n;
			alternate = !alternate;
		}

		return (sum % 10 == 0);
	}

	/**
     * Valida a data de validade do cartão
     * Verifica formato e validade temporal
     * 
     * @param validade data no formato MM/AA
     * @return boolean true se a data é válida
     */
	private boolean validarValidade(String validade) {
		if (!validade.matches("\\d{2}/\\d{2}")) {
			return false;
		}

		try {
			int mes = Integer.parseInt(validade.substring(0, 2));
			int ano = Integer.parseInt(validade.substring(3));

			if (mes < 1 || mes > 12) {
				return false;
			}

			// Converter para ano completo (20XX)
			int anoAtual = java.time.LocalDate.now().getYear() % 100;
			int anoValidade = ano;

			// Validar se não está expirado
			if (anoValidade < anoAtual
					|| (anoValidade == anoAtual && mes < java.time.LocalDate.now().getMonthValue())) {
				return false;
			}

			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
     * Valida o código de segurança do cartão (CVV)
     * 
     * @param cvv código de segurança
     * @return boolean true se o CVV é válido
     */
	private boolean validarCVV(String cvv) {
		return cvv != null && cvv.matches("\\d{3,4}");
	}

	/**
     * Formata o número do cartão para exibição
     * Adiciona espaços a cada 4 dígitos
     * 
     * @param numero número do cartão a ser formatado
     * @return String número formatado
     */
	private String formatarNumeroCartao(String numero) {
		if (numero == null)
			return "";
		numero = numero.replaceAll("\\s", "");
		StringBuilder formatted = new StringBuilder();
		for (int i = 0; i < numero.length(); i++) {
			if (i > 0 && i % 4 == 0) {
				formatted.append(" ");
			}
			formatted.append(numero.charAt(i));
		}
		return formatted.toString();
	}
}