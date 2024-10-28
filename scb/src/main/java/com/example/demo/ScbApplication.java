package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.demo.view.WindowManager;

import javax.swing.*;

@SpringBootApplication
public class ScbApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        
        SpringApplication application = new SpringApplication(ScbApplication.class);
        application.setHeadless(false);
        final ApplicationContext springContext = application.run(args);
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Passa o contexto para o WindowManager
                new WindowManager(springContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}