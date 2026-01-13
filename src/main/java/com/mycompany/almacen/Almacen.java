package com.mycompany.almacen;

import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.almacen.database.DatabaseManager;
import com.mycompany.almacen.gui.InvoiceManagementGUI;
import com.mycompany.almacen.gui.ModernButton;
import com.mycompany.almacen.gui.ProductManagementGUI;
import com.mycompany.almacen.gui.SidebarPanel;
import com.mycompany.almacen.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Almacen extends JFrame {

    // UI Style Constants
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    private ProductManagementGUI productPanel;
    private InvoiceManagementGUI invoicePanel;
    private JPanel contentPanel;
    private ModernButton themeToggleButton;

    public Almacen() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Gestión de Almacén");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create top menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Add theme toggle button to menu bar
        themeToggleButton = new ModernButton("🌙 Modo Oscuro");
        themeToggleButton.addActionListener(e -> toggleTheme());
        menuBar.add(Box.createHorizontalGlue()); // Push button to the right
        menuBar.add(themeToggleButton);

        setJMenuBar(menuBar);

        // Create sidebar
        SidebarPanel sidebar = new SidebarPanel();

        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Initialize panels
        productPanel = new ProductManagementGUI();
        invoicePanel = new InvoiceManagementGUI();

        // Add initial content
        contentPanel.add(productPanel, BorderLayout.CENTER);

        // Add components to main frame
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Add action listener for sidebar navigation
        sidebar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();

                // Clear current content
                contentPanel.removeAll();

                switch(command) {
                    case "PRODUCTS":
                        contentPanel.add(productPanel, BorderLayout.CENTER);
                        break;
                    case "INVOICES":
                        contentPanel.add(invoicePanel, BorderLayout.CENTER);
                        break;
                    case "REPORTS":
                        // TODO: Add reports panel
                        JLabel reportsLabel = new JLabel("Panel de Reportes (Próximamente)", SwingConstants.CENTER);
                        reportsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                        reportsLabel.setForeground(TEXT_COLOR);
                        contentPanel.add(reportsLabel, BorderLayout.CENTER);
                        break;
                }

                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
    }

    private void toggleTheme() {
        ThemeManager.toggleTheme();

        // Update button text based on current theme
        String currentLaf = UIManager.getLookAndFeel().getClass().getName();
        if (currentLaf.equals("com.formdev.flatlaf.FlatDarkLaf")) {
            themeToggleButton.setText("☀️ Modo Claro");
        } else {
            themeToggleButton.setText("🌙 Modo Oscuro");
        }
    }

    public static void main(String[] args) {
        try {
            // Set FlatLaf Light Look and Feel by default
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
            e.printStackTrace();
        }

        DatabaseManager.createTables();
        DatabaseManager.seedDatabaseWithSampleData();

        SwingUtilities.invokeLater(() -> {
            new Almacen().setVisible(true);
        });
    }
}
