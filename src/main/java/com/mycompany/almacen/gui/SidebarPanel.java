package com.mycompany.almacen.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SidebarPanel extends JPanel {
    
    private ModernButton productsButton;
    private ModernButton invoicesButton;
    private ModernButton reportsButton;
    
    private ActionListener listener;
    
    public SidebarPanel() {
        initSidebar();
    }
    
    private void initSidebar() {
        setPreferredSize(new Dimension(200, 800));
        setBackground(new Color(248, 248, 248));
        setLayout(new BorderLayout());
        
        // Create buttons
        productsButton = new ModernButton("Productos");
        invoicesButton = new ModernButton("Facturas");
        reportsButton = new ModernButton("Reportes");
        
        // Set button properties
        Dimension buttonSize = new Dimension(180, 40);
        productsButton.setPreferredSize(buttonSize);
        invoicesButton.setPreferredSize(buttonSize);
        reportsButton.setPreferredSize(buttonSize);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(248, 248, 248));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        buttonPanel.add(productsButton);
        buttonPanel.add(invoicesButton);
        buttonPanel.add(reportsButton);
        
        add(buttonPanel, BorderLayout.NORTH);
        
        // Add action listeners
        productsButton.addActionListener(e -> fireActionEvent("PRODUCTS"));
        invoicesButton.addActionListener(e -> fireActionEvent("INVOICES"));
        reportsButton.addActionListener(e -> fireActionEvent("REPORTS"));
    }
    
    public void addActionListener(ActionListener listener) {
        this.listener = listener;
    }
    
    private void fireActionEvent(String command) {
        if (listener != null) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
            listener.actionPerformed(event);
        }
    }
}