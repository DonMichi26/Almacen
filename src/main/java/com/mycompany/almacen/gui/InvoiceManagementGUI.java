package com.mycompany.almacen.gui;

import com.mycompany.almacen.dao.InvoiceDAO;
import com.mycompany.almacen.dao.InvoiceItemDAO;
import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.model.InvoiceItem;
import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.util.PdfGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceManagementGUI extends JPanel {

    private InvoiceDAO invoiceDAO;
    private InvoiceItemDAO invoiceItemDAO;
    private ProductDAO productDAO;

    // --- Common Components ---
    private DefaultTableModel invoiceTableModel;
    private JTable invoiceTable;

    // --- Product Sale Components ---
    private DefaultTableModel currentItemsTableModel;
    private JTable currentItemsTable;
    private JTextField saleCustomerNameField;
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;
    private JLabel totalAmountLabel;
    private List<InvoiceItem> currentInvoiceItems = new ArrayList<>();
    private double currentTotalAmount = 0.0;

    // --- Service Components ---
    private JTextField serviceCustomerNameField;
    private JTextArea serviceDescriptionArea;
    private JTextField servicePriceField;

    // --- UI Style Constants ---
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color TABLE_HEADER_COLOR = new Color(240, 240, 240);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public InvoiceManagementGUI() {
        invoiceDAO = new InvoiceDAO();
        invoiceItemDAO = new InvoiceItemDAO();
        productDAO = new ProductDAO();
        initComponents();
        loadProductsIntoComboBox();
        loadInvoices();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Create main content panel instead of tabs
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Create top panel with creation options
        JPanel topPanel = createCreationPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Create bottom panel for existing invoices
        JPanel bottomPanel = createExistingInvoicesPanel();
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createCreationPanel() {
        JPanel creationPanel = new JPanel(new BorderLayout(10, 10));
        creationPanel.setBorder(BorderFactory.createTitledBorder("Crear Nuevo Recibo"));
        creationPanel.setBackground(BACKGROUND_COLOR);

        JTabbedPane creationTabs = new JTabbedPane();
        creationTabs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        creationTabs.addTab("Venta de Productos", createProductSalePanel());
        creationTabs.addTab("Servicio de Mantenimiento", createServicePanel());

        creationPanel.add(creationTabs, BorderLayout.CENTER);
        return creationPanel;
    }

    private JPanel createProductSalePanel() {
        JPanel salePanel = new JPanel(new BorderLayout(10, 10));
        salePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        salePanel.setBackground(BACKGROUND_COLOR);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(createLabel("Cliente:"), gbc);
        saleCustomerNameField = createTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(saleCustomerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(createLabel("Producto:"), gbc);
        productComboBox = new JComboBox<>();
        productComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(productComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(createLabel("Cantidad:"), gbc);
        quantityField = createTextField();
        quantityField.setText("1");
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(quantityField, gbc);

        ModernButton addProductButton = new ModernButton("Añadir Producto");
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; formPanel.add(addProductButton, gbc);
        salePanel.add(formPanel, BorderLayout.NORTH);

        JPanel currentItemsPanel = new JPanel(new BorderLayout(10, 10));
        currentItemsPanel.setBackground(BACKGROUND_COLOR);
        currentItemsPanel.setBorder(BorderFactory.createTitledBorder("Productos en este Recibo"));
        String[] itemColumnNames = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        currentItemsTableModel = new DefaultTableModel(itemColumnNames, 0);
        currentItemsTable = createStyledTable(currentItemsTableModel);
        currentItemsPanel.add(new JScrollPane(currentItemsTable), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        southPanel.setBackground(BACKGROUND_COLOR);
        totalAmountLabel = createLabel("Total: S/ 0.00");
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        ModernButton createSaleButton = new ModernButton("Crear Recibo de Venta");
        southPanel.add(totalAmountLabel);
        southPanel.add(createSaleButton);
        currentItemsPanel.add(southPanel, BorderLayout.SOUTH);
        salePanel.add(currentItemsPanel, BorderLayout.CENTER);

        addProductButton.addActionListener(e -> addProductToCurrentInvoice());
        createSaleButton.addActionListener(e -> createSaleInvoice());

        return salePanel;
    }

    private JPanel createServicePanel() {
        JPanel servicePanel = new JPanel(new GridBagLayout());
        servicePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        servicePanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; servicePanel.add(createLabel("Cliente:"), gbc);
        serviceCustomerNameField = createTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; servicePanel.add(serviceCustomerNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST; servicePanel.add(createLabel("Descripción del Servicio:"), gbc);
        serviceDescriptionArea = new JTextArea(5, 20);
        serviceDescriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        serviceDescriptionArea.setLineWrap(true);
        serviceDescriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(serviceDescriptionArea);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; servicePanel.add(scrollPane, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; servicePanel.add(createLabel("Precio Total (S/):"), gbc);
        servicePriceField = createTextField();
        gbc.gridx = 1; gbc.gridy = 2; servicePanel.add(servicePriceField, gbc);

        ModernButton createServiceButton = new ModernButton("Crear Recibo de Servicio");
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; servicePanel.add(createServiceButton, gbc);

        createServiceButton.addActionListener(e -> createServiceInvoice());

        return servicePanel;
    }

    private JPanel createExistingInvoicesPanel() {
        JPanel existingInvoicesPanel = new JPanel(new BorderLayout(10, 10));
        existingInvoicesPanel.setBorder(BorderFactory.createTitledBorder("Recibos Existentes"));
        existingInvoicesPanel.setBackground(BACKGROUND_COLOR);

        String[] invoiceColumnNames = {"ID", "Fecha", "Cliente", "Total", "Tipo"};
        invoiceTableModel = new DefaultTableModel(invoiceColumnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        invoiceTable = createStyledTable(invoiceTableModel);

        existingInvoicesPanel.add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

        JPanel invoiceActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        invoiceActionsPanel.setBackground(BACKGROUND_COLOR);
        ModernButton generatePdfButton = new ModernButton("Generar PDF");
        invoiceActionsPanel.add(generatePdfButton);
        existingInvoicesPanel.add(invoiceActionsPanel, BorderLayout.SOUTH);

        generatePdfButton.addActionListener(e -> generateSelectedInvoicePdf());

        return existingInvoicesPanel;
    }

    private void createSaleInvoice() {
        String customerName = saleCustomerNameField.getText().trim();
        if (customerName.isEmpty() || currentInvoiceItems.isEmpty()) {
            showWarning("El nombre del cliente y al menos un producto son requeridos.");
            return;
        }
        try {
            for (InvoiceItem item : currentInvoiceItems) {
                productDAO.updateProductStock(item.getProductId(), -item.getQuantity());
            }
            Invoice newInvoice = new Invoice(new Date(), customerName, currentTotalAmount);
            int invoiceId = invoiceDAO.addInvoice(newInvoice);
            for (InvoiceItem item : currentInvoiceItems) {
                item.setInvoiceId(invoiceId);
                invoiceItemDAO.addInvoiceItem(item);
            }
            showMessage("Recibo de venta creado con ID: " + invoiceId);
            clearSaleFields();
            loadInvoices();
            loadProductsIntoComboBox();
        } catch (SQLException e) {
            showError("Error al guardar el recibo de venta: " + e.getMessage());
            e.printStackTrace();
            try {
                for (InvoiceItem item : currentInvoiceItems) {
                    productDAO.updateProductStock(item.getProductId(), item.getQuantity());
                }
            } catch (SQLException revertEx) {
                showError("¡ERROR CRÍTICO! No se pudo revertir el stock: " + revertEx.getMessage());
            }
        }
    }

    private void createServiceInvoice() {
        String customerName = serviceCustomerNameField.getText().trim();
        String description = serviceDescriptionArea.getText().trim();
        if (customerName.isEmpty() || description.isEmpty() || servicePriceField.getText().trim().isEmpty()) {
            showWarning("Cliente, descripción y precio son requeridos.");
            return;
        }
        try {
            double price = Double.parseDouble(servicePriceField.getText().trim());
            Invoice newInvoice = new Invoice(new Date(), customerName, price, description);
            int invoiceId = invoiceDAO.addInvoice(newInvoice);
            showMessage("Recibo de servicio creado con ID: " + invoiceId);
            clearServiceFields();
            loadInvoices();
        } catch (NumberFormatException e) {
            showError("Precio inválido. Ingrese un valor numérico.");
            e.printStackTrace();
        } catch (SQLException e) {
            showError("Error al guardar el recibo de servicio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateSelectedInvoicePdf() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Por favor, seleccione un recibo para generar el PDF.");
            return;
        }

        try {
            int invoiceId = (int) invoiceTableModel.getValueAt(selectedRow, 0);
            Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);

            if (invoice != null) {
                String fileName = "Recibo_" + invoice.getId() + "_" + invoice.getCustomerName().replaceAll("\\s+", "_") + ".pdf";
                if (invoice.getInvoiceType() == Invoice.InvoiceType.SALE) {
                    List<InvoiceItem> items = invoiceItemDAO.getInvoiceItemsByInvoiceId(invoiceId);
                    if (items == null || items.isEmpty()) {
                        showError("Este recibo de venta no tiene productos asociados.");
                        return;
                    }
                    PdfGenerator.generateInvoicePdf(invoice, items, fileName);
                } else { // SERVICE
                    PdfGenerator.generateServiceInvoicePdf(invoice, fileName);
                }
                showMessage("PDF generado exitosamente: " + fileName);
            } else {
                showError("No se pudo encontrar el recibo.");
            }
        } catch (SQLException | FileNotFoundException e) {
            showError("Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addProductToCurrentInvoice() {
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        if (selectedProduct == null) {
            showWarning("Por favor, seleccione un producto.");
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Cantidad inválida. Ingrese un número entero positivo.");
            return;
        }
        if (selectedProduct.getStock() < quantity) {
            showWarning("No hay suficiente stock. Disponible: " + selectedProduct.getStock());
            return;
        }
        boolean found = false;
        for (InvoiceItem item : currentInvoiceItems) {
            if (item.getProductId() == selectedProduct.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            currentInvoiceItems.add(new InvoiceItem(-1, selectedProduct.getId(), selectedProduct.getName(), quantity, selectedProduct.getPrice()));
        }
        updateCurrentItemsTable();
    }

    private void updateCurrentItemsTable() {
        currentItemsTableModel.setRowCount(0);
        currentTotalAmount = 0.0;
        for (InvoiceItem item : currentInvoiceItems) {
            double subtotal = item.getQuantity() * item.getUnitPrice();
            currentItemsTableModel.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                "S/ " + String.format("%.2f", item.getUnitPrice()),
                "S/ " + String.format("%.2f", subtotal)
            });
            currentTotalAmount += subtotal;
        }
        totalAmountLabel.setText(String.format("Total: S/ %.2f", currentTotalAmount));
    }

    private void loadInvoices() {
        invoiceTableModel.setRowCount(0);
        try {
            List<Invoice> invoices = invoiceDAO.getAllInvoices();
            for (Invoice invoice : invoices) {
                invoiceTableModel.addRow(new Object[]{
                    invoice.getId(),
                    DATE_FORMAT.format(invoice.getInvoiceDate()),
                    invoice.getCustomerName(),
                    "S/ " + String.format("%.2f", invoice.getTotalAmount()),
                    invoice.getInvoiceType()
                });
            }
        } catch (SQLException e) {
            showError("Error al cargar los recibos: " + e.getMessage());
        }
    }

    private void loadProductsIntoComboBox() {
        try {
            List<Product> products = productDAO.getAllProducts();
            productComboBox.removeAllItems();
            for (Product product : products) {
                productComboBox.addItem(product);
            }
            productComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Product) {
                        setText(((Product) value).getName());
                    }
                    return this;
                }
            });
        } catch (SQLException e) {
            showError("Error al cargar productos: " + e.getMessage());
        }
    }

    private void clearSaleFields() {
        saleCustomerNameField.setText("");
        quantityField.setText("1");
        if (productComboBox.getItemCount() > 0) productComboBox.setSelectedIndex(0);
        currentInvoiceItems.clear();
        updateCurrentItemsTable();
    }

    private void clearServiceFields() {
        serviceCustomerNameField.setText("");
        serviceDescriptionArea.setText("");
        servicePriceField.setText("");
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return table;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}

