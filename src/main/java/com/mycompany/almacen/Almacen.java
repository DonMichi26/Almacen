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

/**
 * Clase principal de la aplicación Almacen.
 * Esta clase extiende JFrame y sirve como el contenedor principal (ventana) de
 * la aplicación.
 * Gestiona la navegación entre los diferentes módulos (Productos, Facturas,
 * Reportes)
 * y la configuración global como el tema (Claro/Oscuro).
 */
public class Almacen extends JFrame {

    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);

    private ProductManagementGUI productPanel;
    private InvoiceManagementGUI invoicePanel;
    private JPanel contentPanel;
    private ModernButton themeToggleButton;

    public Almacen() {
        initComponents();
    }

    /**
     * Inicializa y configura todos los componentes de la interfaz gráfica.
     * Configura:
     * 1. Propiedades de la ventana principal (título, tamaño, cierre).
     * 2. Barra de menú superior con el botón de cambio de tema.
     * 3. Barra lateral (SidebarPanel) para la navegación.
     * 4. Panel de contenido central donde se muestran las vistas.
     */
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

        // Agrega un escuchador de eventos para manejar la navegación desde la barra
        // lateral.
        // Este bloque de código determina qué vista mostrar basándose en el botón
        // presionado.
        sidebar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand(); // Obtiene el comando del botón presionado

                // Elimina el contenido actual del panel central para mostrar el nuevo
                contentPanel.removeAll();

                // Estructura de control para cambiar entre vistas
                switch (command) {
                    case "PRODUCTS":
                        // Muestra el panel de gestión de productos
                        contentPanel.add(productPanel, BorderLayout.CENTER);
                        break;
                    case "INVOICES":
                        // Muestra el panel de gestión de facturas
                        contentPanel.add(invoicePanel, BorderLayout.CENTER);
                        break;
                    case "REPORTS":
                        // Muestra un panel provisional para reportes
                        JLabel reportsLabel = new JLabel("Panel de Reportes (Próximamente)", SwingConstants.CENTER);
                        reportsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                        reportsLabel.setForeground(TEXT_COLOR);
                        contentPanel.add(reportsLabel, BorderLayout.CENTER);
                        break;
                }

                // Actualiza la interfaz gráfica para reflejar los cambios
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });
    }

    /**
     * Alterna entre el tema Claro y Oscuro de la aplicación.
     * Utiliza ThemeManager para realizar el cambio y actualiza el texto del botón
     * en la barra de menú.
     */
    private void toggleTheme() {
        ThemeManager.toggleTheme();

        // Actualiza el texto del botón basado en el tema actual
        String currentLaf = UIManager.getLookAndFeel().getClass().getName();
        if (currentLaf.equals("com.formdev.flatlaf.FlatDarkLaf")) {
            themeToggleButton.setText("☀️ Modo Claro");
        } else {
            themeToggleButton.setText("🌙 Modo Oscuro");
        }
    }

    /**
     * Método principal (Main) - Punto de entrada de la aplicación.
     * 1. Configura el Look and Feel (apariencia) inicial usando FlatLaf.
     * 2. Inicializa la base de datos y carga datos de prueba.
     * 3. Inicia la interfaz gráfica en el hilo de despacho de eventos de Swing.
     */
    public static void main(String[] args) {
        try {
            // Establece FlatLaf Light como apariencia por defecto
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
