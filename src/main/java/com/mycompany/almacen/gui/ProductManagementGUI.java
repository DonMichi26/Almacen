package com.mycompany.almacen.gui;

import com.mycompany.almacen.dao.BrandDAO;
import com.mycompany.almacen.dao.CategoryDAO;
import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.model.Brand;
import com.mycompany.almacen.model.Category;
import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.service.ProductService;
import com.mycompany.almacen.util.CsvManager;
import com.mycompany.almacen.util.FileUtils;
import com.mycompany.almacen.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProductManagementGUI extends JPanel {

    private ProductService productService;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;
    private DefaultTableModel tableModel;
    private List<Product> loadedProducts;

    private JTextField nameField, descriptionField, priceField, stockField, modelField;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<Brand> brandComboBox;
    private ModernButton addButton, updateButton, deleteButton, clearButton, searchButton, importButton, exportButton;
    private JTable productTable;

    // Define a minimalist color palette
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250); // Light Gray
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);    // Steel Blue
    private static final Color TEXT_COLOR = new Color(50, 50, 50);          // Dark Gray
    private static final Color TABLE_HEADER_COLOR = new Color(240, 240, 240); // Lighter Gray

    public ProductManagementGUI() {
        productService = new ProductService();
        categoryDAO = new CategoryDAO();
        brandDAO = new BrandDAO();
        initComponents();
        loadProducts();
        loadCategories();
        loadBrands();
    }

    private void initComponents() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Input Panel with GridBagLayout for better alignment ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        inputPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Category
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(createLabel("Equipo:"), gbc);
        categoryComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(categoryComboBox, gbc);

        // Brand
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(createLabel("Marca:"), gbc);
        brandComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(brandComboBox, gbc);

        // Model
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(createLabel("Modelo:"), gbc);
        modelField = createTextField();
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(modelField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(createLabel("Nombre:"), gbc);
        nameField = createTextField();
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; inputPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(createLabel("Descripción:"), gbc);
        descriptionField = createTextField();
        gbc.gridx = 1; gbc.gridy = 4; inputPanel.add(descriptionField, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(createLabel("Precio:"), gbc);
        priceField = createTextField();
        gbc.gridx = 1; gbc.gridy = 5; inputPanel.add(priceField, gbc);

        // Stock
        gbc.gridx = 0; gbc.gridy = 6; inputPanel.add(createLabel("Stock:"), gbc);
        stockField = createTextField();
        gbc.gridx = 1; gbc.gridy = 6; inputPanel.add(stockField, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        addButton = new ModernButton("Agregar");
        updateButton = new ModernButton("Actualizar");
        deleteButton = new ModernButton("Eliminar");
        clearButton = new ModernButton("Limpiar");
        importButton = new ModernButton("Importar CSV");
        exportButton = new ModernButton("Exportar CSV");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        // --- Table Panel with improved aesthetics ---
        String[] columnNames = {"Equipo", "Marca", "Modelo", "Nombre", "Descripción", "Precio", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);

        // Table styling
        productTable.setFillsViewportHeight(true);
        productTable.setRowHeight(25);
        productTable.setGridColor(new Color(230, 230, 230));
        productTable.setSelectionBackground(PRIMARY_COLOR);
        productTable.setSelectionForeground(Color.WHITE);

        JTableHeader header = productTable.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // --- Main Layout ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 5));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 5, 5, 5));

        searchPanel.add(createLabel("Buscar (Equipo, Marca, Modelo, Nombre):"), BorderLayout.WEST);

        JTextField searchFieldForPanel = createTextField();
        searchFieldForPanel.setName("searchField"); // Asignar nombre para identificarlo fácilmente
        searchPanel.add(searchFieldForPanel, BorderLayout.CENTER);

        searchButton = new ModernButton("Buscar");
        searchPanel.add(searchButton, BorderLayout.EAST);

        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        final JTextField finalSearchField = searchFieldForPanel;
        searchButton.addActionListener(e -> {
            if (finalSearchField != null) {
                loadProducts(finalSearchField.getText());
            }
        });
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearFields());
        importButton.addActionListener(e -> importProductsFromCsv());
        exportButton.addActionListener(e -> exportProductsToCsv());

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productTable.getSelectedRow() != -1) {
                displayProductDetails();
            }
        });
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            categoryComboBox.removeAllItems();
            for (Category category : categories) {
                categoryComboBox.addItem(category);
            }
            // Set renderer to show category name
            categoryComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Category) {
                        setText(((Category) value).getName());
                    }
                    return this;
                }
            });
        } catch (SQLException e) {
            showError("Error al cargar categorías: " + e.getMessage());
        }
    }

    private void loadBrands() {
        try {
            List<Brand> brands = brandDAO.getAllBrands();
            brandComboBox.removeAllItems();
            for (Brand brand : brands) {
                brandComboBox.addItem(brand);
            }
            // Add option to add new brand
            brandComboBox.addItem(new Brand(-1, "Agregar nuevo..."));

            // Set renderer to show brand name
            brandComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Brand) {
                        setText(((Brand) value).getName());
                    }
                    return this;
                }
            });
        } catch (SQLException e) {
            showError("Error al cargar marcas: " + e.getMessage());
        }
    }

    // --- Helper methods for creating styled components ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    // Método para buscar productos en múltiples campos
    private List<Product> searchProducts(String searchTerm) throws Exception {
        List<Product> allProducts = productService.getAllProducts();
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : allProducts) {
            // Obtener los nombres de categoría y marca para la comparación
            String categoryName = getCategoryNameById(product.getCategoryId()).toLowerCase();
            String brandName = getBrandNameById(product.getBrandId()).toLowerCase();
            String modelName = (product.getModel() != null ? product.getModel() : "").toLowerCase();
            String productName = product.getName().toLowerCase();

            // Convertir el término de búsqueda a minúsculas
            String lowerSearchTerm = searchTerm.toLowerCase();

            // Verificar si el término de búsqueda coincide con alguno de los campos
            if (categoryName.contains(lowerSearchTerm) ||
                brandName.contains(lowerSearchTerm) ||
                modelName.contains(lowerSearchTerm) ||
                productName.contains(lowerSearchTerm)) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    // --- Data loading and actions (mostly unchanged, added error handling) ---
    private void loadProducts() {
        loadProducts(null);
    }

    private void loadProducts(String searchTerm) {
        tableModel.setRowCount(0);
        try {
            List<Product> products;
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                products = searchProducts(searchTerm.trim());
            } else {
                products = productService.getAllProducts();
            }
            loadedProducts = products;
            for (Product product : products) {
                String categoryName = getCategoryNameById(product.getCategoryId());
                String brandName = getBrandNameById(product.getBrandId());
                tableModel.addRow(new Object[]{
                    categoryName, // Equipo
                    brandName, // Marca
                    product.getModel() != null ? product.getModel() : "", // Modelo
                    product.getName(), // Nombre
                    product.getDescription(), // Descripción
                    "S/ " + String.format("%.2f", product.getPrice()), // Precio
                    product.getStock() // Stock
                });
            }
        } catch (Exception e) {
            showError("Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addProduct() {
        try {
            String name = nameField.getText();
            if (name.trim().isEmpty()) {
                showWarning("El nombre del producto no puede estar vacío.");
                return;
            }
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText().replace("S/ ", ""));
            int stock = Integer.parseInt(stockField.getText());
            String model = modelField.getText().trim();
            Brand selectedBrand = (Brand) brandComboBox.getSelectedItem();
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();

            int brandId;
            // Check if user wants to add a new brand
            if (selectedBrand != null && selectedBrand.getId() == -1) {
                // Prompt user for new brand name
                String newBrandName = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva marca:");
                if (newBrandName != null && !newBrandName.trim().isEmpty()) {
                    // Create new brand
                    Brand newBrand = new Brand(0, newBrandName.trim());
                    new BrandDAO().addBrand(newBrand);
                    // Get the new brand with its assigned ID
                    Brand createdBrand = new BrandDAO().getBrandByName(newBrandName.trim());
                    brandId = createdBrand != null ? createdBrand.getId() : 1; // Default to General if creation failed
                } else {
                    // User cancelled or entered empty name, default to General
                    brandId = 1;
                }
            } else {
                brandId = selectedBrand != null ? selectedBrand.getId() : 1; // Default to General brand
            }

            int categoryId = selectedCategory != null ? selectedCategory.getId() : 1; // Default to General

            Product product = new Product(0, name, description, price, stock, categoryId, brandId, model);
            productService.addProduct(product);
            showMessage("Producto agregado exitosamente.");
            clearFields();
            loadProducts();
            loadCategories(); // Reload categories in case a new one was added during import
            loadBrands(); // Reload brands in case a new one was added
        } catch (NumberFormatException ex) {
            showError("Por favor, ingrese valores numéricos válidos para Precio y Stock.");
        } catch (Exception ex) {
            showError("Error al agregar producto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Por favor, seleccione un producto de la tabla para actualizar.");
            return;
        }
        try {
            Product selectedProduct = loadedProducts.get(selectedRow);
            String name = nameField.getText();
            if (name.trim().isEmpty()) {
                showWarning("El nombre del producto no puede estar vacío.");
                return;
            }
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText().replace("S/ ", ""));
            int stock = Integer.parseInt(stockField.getText());
            String model = modelField.getText().trim();
            Brand selectedBrand = (Brand) brandComboBox.getSelectedItem();
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();

            int brandId;
            // Check if user wants to add a new brand
            if (selectedBrand != null && selectedBrand.getId() == -1) {
                // Prompt user for new brand name
                String newBrandName = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva marca:");
                if (newBrandName != null && !newBrandName.trim().isEmpty()) {
                    // Create new brand
                    Brand newBrand = new Brand(0, newBrandName.trim());
                    new BrandDAO().addBrand(newBrand);
                    // Get the new brand with its assigned ID
                    Brand createdBrand = new BrandDAO().getBrandByName(newBrandName.trim());
                    brandId = createdBrand != null ? createdBrand.getId() : 1; // Default to General if creation failed
                } else {
                    // User cancelled or entered empty name, default to General
                    brandId = 1;
                }
            } else {
                brandId = selectedBrand != null ? selectedBrand.getId() : 1; // Default to General brand
            }

            int categoryId = selectedCategory != null ? selectedCategory.getId() : 1; // Default to General

            Product product = new Product(selectedProduct.getId(), name, description, price, stock, categoryId, brandId, model);
            productService.updateProduct(product);
            showMessage("Producto actualizado exitosamente.");
            clearFields();
            loadProducts();
        } catch (NumberFormatException ex) {
            showError("Por favor, ingrese valores numéricos válidos para Precio y Stock.");
        } catch (Exception ex) {
            showError("Error al actualizar producto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarning("Por favor, seleccione un producto de la tabla para eliminar.");
            return;
        }
        try {
            Product selectedProduct = loadedProducts.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el producto: " + selectedProduct.getName() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                productService.deleteProduct(selectedProduct.getId());
                showMessage("Producto eliminado exitosamente.");
                clearFields();
                loadProducts();
            }
        } catch (Exception ex) {
            showError("Error al eliminar producto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        descriptionField.setText("");
        priceField.setText("");
        stockField.setText("");
        modelField.setText("");
        if (categoryComboBox.getItemCount() > 0) {
            categoryComboBox.setSelectedIndex(0);
        }
        if (brandComboBox.getItemCount() > 0) {
            brandComboBox.setSelectedIndex(0);
        }
        productTable.clearSelection();
    }

    private void displayProductDetails() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            Product selectedProduct = loadedProducts.get(selectedRow);
            nameField.setText(selectedProduct.getName());
            descriptionField.setText(selectedProduct.getDescription());
            priceField.setText(String.format("%.2f", selectedProduct.getPrice()));
            stockField.setText(String.valueOf(selectedProduct.getStock()));
            modelField.setText(selectedProduct.getModel() != null ? selectedProduct.getModel() : "");

            // Select the appropriate category in the combo box
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                Category category = (Category) categoryComboBox.getItemAt(i);
                if (category.getId() == selectedProduct.getCategoryId()) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }

            // Select the appropriate brand in the combo box
            for (int i = 0; i < brandComboBox.getItemCount(); i++) {
                Brand brand = (Brand) brandComboBox.getItemAt(i);
                if (brand.getId() == selectedProduct.getBrandId()) {
                    brandComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void importProductsFromCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo CSV para importar productos");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV", "csv"));

        // Establecer directorio predeterminado como la carpeta de documentos del usuario
        fileChooser.setCurrentDirectory(new java.io.File(FileUtils.getUserDocumentsFolder()));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                List<Product> importedProducts = CsvManager.importProductsFromCsv(filePath);

                for (Product product : importedProducts) {
                    productService.addProduct(product);
                }

                showMessage("Importación completada. " + importedProducts.size() + " productos importados.");
                loadProducts();
                loadCategories(); // Reload categories in case new ones were added
            } catch (IOException e) {
                showError("Error de E/S al importar productos: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showError("Error inesperado al importar productos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void exportProductsToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo CSV de productos");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos CSV", "csv"));

        // Set default filename
        java.util.Date now = new java.util.Date();
        String defaultFileName = "productos_exportados_" +
            new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(now) + ".csv";

        // Establecer directorio predeterminado como la carpeta de documentos del usuario
        fileChooser.setCurrentDirectory(new java.io.File(FileUtils.getUserDocumentsFolder()));

        fileChooser.setSelectedFile(new java.io.File(defaultFileName));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                // Ensure the file has .csv extension
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }

                List<Product> allProducts = productService.getAllProducts();
                CsvManager.exportProductsToCsv(allProducts, filePath);

                showMessage("Exportación completada. " + allProducts.size() + " productos exportados a: " + filePath);
            } catch (IOException e) {
                showError("Error de E/S al exportar productos: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showError("Error inesperado al exportar productos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getCategoryNameById(int categoryId) {
        try {
            CategoryDAO categoryDAO = new CategoryDAO();
            Category category = categoryDAO.getCategoryById(categoryId);
            return category != null ? category.getName() : "General";
        } catch (Exception e) {
            e.printStackTrace();
            return "General"; // Default fallback
        }
    }

    private String getBrandNameById(int brandId) {
        try {
            BrandDAO brandDAO = new BrandDAO();
            Brand brand = brandDAO.getBrandById(brandId);
            return brand != null ? brand.getName() : "General";
        } catch (Exception e) {
            e.printStackTrace();
            return "General"; // Default fallback
        }
    }

    // --- Helper methods for showing messages ---
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
