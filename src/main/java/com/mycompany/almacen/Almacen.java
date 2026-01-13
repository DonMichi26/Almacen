package com.mycompany.almacen;

import com.mycompany.almacen.database.DatabaseManager;
import com.mycompany.almacen.gui.InvoiceManagementGUI;
import com.mycompany.almacen.gui.ProductManagementGUI;

import javax.swing.*;
import java.awt.*;

public class Almacen extends JFrame {

    // UI Style Constants
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    public Almacen() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Gestión de Almacén");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setBackground(BACKGROUND_COLOR);

        // Product Management Tab
        ProductManagementGUI productPanel = new ProductManagementGUI();
        tabbedPane.addTab("  Gestión de Productos  ", productPanel);

        // Invoice Management Tab
        InvoiceManagementGUI invoicePanel = new InvoiceManagementGUI();
        tabbedPane.addTab("  Facturas y Recibos  ", invoicePanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus L&F not found. Using default.");
        }

        DatabaseManager.createTables();
        DatabaseManager.seedDatabaseWithSampleData();

        SwingUtilities.invokeLater(() -> {
            new Almacen().setVisible(true);
        });
    }
}
