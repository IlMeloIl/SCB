package com.example.demo.view;

import javax.swing.*;
import java.awt.*;
import org.springframework.context.ApplicationContext;
import com.example.demo.controller.CiclistaController;
import com.example.demo.controller.TotemController;
import com.example.demo.controller.EmprestimoController;
import com.example.demo.view.panels.*;
import com.example.demo.view.frames.MainFrame;

public class WindowManager {
    // Constantes para CardLayout
    public static final String LOGIN_PANEL = "LOGIN";
    public static final String CADASTRO_PANEL = "CADASTRO";
    public static final String DASHBOARD_PANEL = "DASHBOARD";
    public static final String TOTEM_LIST_PANEL = "TOTEM_LIST";
    public static final String EMPRESTIMO_PANEL = "EMPRESTIMO";
    public static final String DEVOLUCAO_PANEL = "DEVOLUCAO";

    // Componentes principais
    private MainFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private ApplicationContext springContext;

    // Controllers
    private CiclistaController ciclistaController;
    private TotemController totemController;
    private EmprestimoController emprestimoController;

    // Dados da sessão atual
    private String currentUserEmail;
    private String currentUserName;
    private boolean hasActiveEmprestimo;
    private String currentBikeInfo;

    // Painéis
    private LoginPanel loginPanel;
    private CadastroPanel cadastroPanel;
    private DashboardPanel dashboardPanel;
//    private TotemListPanel totemListPanel;
//    private EmprestimoPanel emprestimoPanel;
//    private DevolucaoPanel devolucaoPanel;

    public WindowManager(ApplicationContext springContext) {
        this.springContext = springContext;
        initializeControllers();
        initializeWindow();
    }

    private void initializeControllers() {
        ciclistaController = springContext.getBean(CiclistaController.class);
        totemController = springContext.getBean(TotemController.class);
        emprestimoController = springContext.getBean(EmprestimoController.class);
    }

    private void initializeWindow() {
        mainFrame = new MainFrame();
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        mainFrame.setContentPane(contentPanel);

        initializePanels();
        addPanelsToContent();

        showLogin();
        mainFrame.setVisible(true);
    }

    private void initializePanels() {
        loginPanel = new LoginPanel(this, ciclistaController);
        cadastroPanel = new CadastroPanel(this, ciclistaController);
        dashboardPanel = new DashboardPanel(this, ciclistaController);
//        totemListPanel = new TotemListPanel(this, totemController);
//        emprestimoPanel = new EmprestimoPanel(this, emprestimoController);
//        devolucaoPanel = new DevolucaoPanel(this, emprestimoController);
    }

    private void addPanelsToContent() {
        contentPanel.add(loginPanel, LOGIN_PANEL);
        contentPanel.add(cadastroPanel, CADASTRO_PANEL);
        contentPanel.add(dashboardPanel, DASHBOARD_PANEL);
//        contentPanel.add(totemListPanel, TOTEM_LIST_PANEL);
//        contentPanel.add(emprestimoPanel, EMPRESTIMO_PANEL);
//        contentPanel.add(devolucaoPanel, DEVOLUCAO_PANEL);
    }

    // Métodos de navegação
    public void showLogin() {
        cardLayout.show(contentPanel, LOGIN_PANEL);
        mainFrame.setTitle("SCB - Login");
        resetSessionData();
    }

    public void showCadastro() {
        cardLayout.show(contentPanel, CADASTRO_PANEL);
        mainFrame.setTitle("SCB - Cadastro");
    }

    public void showDashboard() {
        cardLayout.show(contentPanel, DASHBOARD_PANEL);
        mainFrame.setTitle("SCB - Painel Principal");
        updateDashboardInfo();
    }

    public void showTotemList() {
        cardLayout.show(contentPanel, TOTEM_LIST_PANEL);
        mainFrame.setTitle("SCB - Lista de Totens");
    }

    public void showEmprestimo() {
        if (hasActiveEmprestimo) {
            showError("Você já possui uma bicicleta em uso.");
            return;
        }
        cardLayout.show(contentPanel, EMPRESTIMO_PANEL);
        mainFrame.setTitle("SCB - Novo Empréstimo");
    }

    public void showDevolucao() {
        if (!hasActiveEmprestimo) {
            showError("Você não possui nenhuma bicicleta em uso.");
            return;
        }
        cardLayout.show(contentPanel, DEVOLUCAO_PANEL);
        mainFrame.setTitle("SCB - Devolução");
    }

    // Gerenciamento de sessão
    public void handleLoginSuccess(String userEmail, String userName, boolean hasEmprestimo, String bikeInfo) {
        this.currentUserEmail = userEmail;
        this.currentUserName = userName;
        this.hasActiveEmprestimo = hasEmprestimo;
        this.currentBikeInfo = bikeInfo;
        showDashboard();
    }

    private void resetSessionData() {
        currentUserEmail = null;
        currentUserName = null;
        hasActiveEmprestimo = false;
        currentBikeInfo = null;
    }

    private void updateDashboardInfo() {
        if (currentUserEmail != null) {
            dashboardPanel.setUserName(currentUserName);
            dashboardPanel.updateEmprestimoStatus(hasActiveEmprestimo, currentBikeInfo);
        }
    }

    // Métodos de acesso aos dados da sessão
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public boolean hasActiveEmprestimo() {
        return hasActiveEmprestimo;
    }

    public String getCurrentBikeInfo() {
        return currentBikeInfo;
    }

    // Métodos de utilidade
    public void showError(String message) {
        JOptionPane.showMessageDialog(
            mainFrame,
            message,
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            mainFrame,
            message,
            "Sucesso",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public boolean showConfirmDialog(String message, String title) {
        int result = JOptionPane.showConfirmDialog(
            mainFrame,
            message,
            title,
            JOptionPane.YES_NO_OPTION
        );
        return result == JOptionPane.YES_OPTION;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }
}