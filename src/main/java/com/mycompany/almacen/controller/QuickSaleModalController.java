package com.mycompany.almacen.controller;

import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.model.InvoiceItem;
import com.mycompany.almacen.service.ProductService;
import com.mycompany.almacen.service.InvoiceService;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.util.PdfGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.*;
import java.util.stream.Collectors;
import java.io.File;

/**
 * Controller para el Modal de Venta Rápida.
 * Permite seleccionar productos, agregar al carrito y procesar la venta.
 */
public class QuickSaleModalController {

    @FXML private BorderPane quickSaleRoot;
    
    // Búsqueda de productos
    @FXML private TextField productSearchField;
    
    // Tabla de productos
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, Double> colProductPrice;
    @FXML private TableColumn<Product, Integer> colProductStock;
    @FXML private TableColumn<Product, Void> colProductAction;
    
    // Carrito
    @FXML private ScrollPane cartScrollPane;
    @FXML private VBox cartItems;
    
    // Cliente
    @FXML private TextField customerNameField;
    @FXML private TextField customerEmailField;
    
    // Totales
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    
    // Botones
    @FXML private Button closeButton;
    @FXML private Button cancelButton;
    @FXML private Button processButton;

    private ProductService productService;
    private InvoiceService invoiceService;
    private ObservableList<Product> products;
    private Map<Integer, CartItem> cart;
    private Runnable onCloseCallback;

    private static final double TAX_RATE = 0.16;

    public QuickSaleModalController() {
        this.products = FXCollections.observableArrayList();
        this.cart = new HashMap<>();
    }

    public void initialize(ProductService productService, InvoiceService invoiceService, Runnable onCloseCallback) {
        this.productService = productService;
        this.invoiceService = invoiceService;
        this.onCloseCallback = onCloseCallback;
        
        setupTable();
        setupEventHandlers();
        loadProducts();
    }

    private void setupTable() {
        // Columna Nombre
        colProductName.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        
        // Columna Precio
        colProductPrice.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPrice()));
        colProductPrice.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });
        
        // Columna Stock
        colProductStock.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStock()));
        colProductStock.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item <= 0) {
                    setText("Agotado");
                    setStyle("-fx-text-fill: -danger; -fx-font-weight: bold;");
                } else if (item <= 10) {
                    setText(item + " (Bajo)");
                    setStyle("-fx-text-fill: -warning; -fx-font-weight: bold;");
                } else {
                    setText(item.toString());
                    setStyle("");
                }
            }
        });
        
        // Columna Acción (Botón Agregar)
        colProductAction.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Product, Void> call(TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button addButton = new Button("+ Agregar");
                    
                    {
                        addButton.getStyleClass().addAll("button", "success");
                        addButton.setPrefWidth(100);
                        addButton.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            addToCart(product);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Product product = getTableView().getItems().get(getIndex());
                            addButton.setDisable(product.getStock() <= 0);
                            setGraphic(addButton);
                        }
                    }
                };
            }
        });
    }

    private void setupEventHandlers() {
        closeButton.setOnAction(e -> close());
        cancelButton.setOnAction(e -> close());
        processButton.setOnAction(e -> processSale());
        
        productSearchField.textProperty().addListener((obs, old, newValue) -> {
            filterProducts(newValue != null ? newValue.trim() : "");
        });
    }

    private void loadProducts() {
        try {
            products.setAll(productService.getAllProducts());
            productTable.setItems(products);
        } catch (AlmacenException e) {
            showError("Error al cargar productos: " + e.getMessage());
        }
    }

    private void filterProducts(String searchTerm) {
        if (searchTerm.isEmpty()) {
            productTable.setItems(products);
            return;
        }
        
        ObservableList<Product> filtered = FXCollections.observableArrayList(
            products.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            (p.getModel() != null && p.getModel().toLowerCase().contains(searchTerm.toLowerCase())))
                .collect(Collectors.toList())
        );
        productTable.setItems(filtered);
    }

    // ==================== CARRITO ====================

    private static class CartItem {
        Product product;
        int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        double getTotal() {
            return product.getPrice() * quantity;
        }
    }

    private void addToCart(Product product) {
        if (product.getStock() <= 0) {
            showError("Producto agotado");
            return;
        }

        CartItem existingItem = cart.get(product.getId());
        if (existingItem != null) {
            if (existingItem.quantity >= product.getStock()) {
                showError("No hay más stock disponible");
                return;
            }
            existingItem.quantity++;
        } else {
            cart.put(product.getId(), new CartItem(product, 1));
        }

        renderCart();
    }

    private void renderCart() {
        cartItems.getChildren().clear();
        
        for (CartItem item : cart.values()) {
            VBox card = createCartItemCard(item);
            cartItems.getChildren().add(card);
        }

        updateTotals();
    }

    private VBox createCartItemCard(CartItem item) {
        VBox card = new VBox(8);
        card.getStyleClass().add("cart-item");
        card.setPadding(new javafx.geometry.Insets(12));

        // Header: Nombre y Precio Unitario
        HBox header = new HBox();
        Label nameLabel = new Label(item.product.getName());
        nameLabel.getStyleClass().add("cart-item-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        
        Label unitPriceLabel = new Label(String.format("$%.2f c/u", item.product.getPrice()));
        unitPriceLabel.getStyleClass().add("cart-item-price");
        
        header.getChildren().addAll(nameLabel, unitPriceLabel);

        // Controls: Cantidad y Total
        HBox controls = new HBox(8);
        controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button minusBtn = new Button("−");
        minusBtn.getStyleClass().add("icon-button");
        minusBtn.setOnAction(e -> updateQuantity(item.product.getId(), item.quantity - 1));
        
        Label qtyLabel = new Label(String.valueOf(item.quantity));
        qtyLabel.setStyle("-fx-font-weight: 600; -fx-min-width: 30px; -fx-alignment: center;");
        
        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("icon-button");
        plusBtn.setOnAction(e -> updateQuantity(item.product.getId(), item.quantity + 1));
        
        Label itemTotalLabel = new Label(String.format("$%.2f", item.getTotal()));
        itemTotalLabel.setStyle("-fx-font-weight: 700; -fx-text-fill: -primary;");
        
        Button removeBtn = new Button("🗑");
        removeBtn.getStyleClass().add("icon-button");
        removeBtn.setOnAction(e -> removeFromCart(item.product.getId()));
        
        controls.getChildren().addAll(minusBtn, qtyLabel, plusBtn, new Region(), itemTotalLabel, removeBtn);
        HBox.setHgrow(controls.getChildren().get(3), Priority.ALWAYS);

        card.getChildren().addAll(header, controls);
        return card;
    }

    private void updateQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeFromCart(productId);
            return;
        }

        CartItem item = cart.get(productId);
        if (item != null && newQuantity <= item.product.getStock()) {
            item.quantity = newQuantity;
            renderCart();
        } else {
            showError("No hay suficiente stock");
        }
    }

    private void removeFromCart(int productId) {
        cart.remove(productId);
        renderCart();
    }

    private void updateTotals() {
        double subtotal = cart.values().stream()
            .mapToDouble(CartItem::getTotal)
            .sum();
        
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));
    }

    // ==================== PROCESAR VENTA ====================

    private void processSale() {
        if (cart.isEmpty()) {
            showError("El carrito está vacío");
            return;
        }

        String customerName = customerNameField.getText();
        if (customerName == null || customerName.trim().isEmpty()) {
            showError("Ingrese el nombre del cliente");
            customerNameField.requestFocus();
            return;
        }

        try {
            // Crear factura
            Invoice invoice = new Invoice();
            invoice.setCustomerName(customerName);
            invoice.setCustomerEmail(customerEmailField.getText());
            
            double subtotal = cart.values().stream()
                .mapToDouble(CartItem::getTotal)
                .sum();
            
            double tax = subtotal * TAX_RATE;
            double total = subtotal + tax;
            
            invoice.setTotalAmount(total);
            invoice.setInvoiceDate(new java.sql.Date(System.currentTimeMillis()));
            
            // Guardar factura
            int invoiceId = invoiceService.addInvoice(invoice);
            
            // Guardar items y actualizar stock
            List<InvoiceItem> invoiceItems = new ArrayList<>();
            for (CartItem cartItem : cart.values()) {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoiceId(invoiceId);
                invoiceItem.setProductId(cartItem.product.getId());
                invoiceItem.setProductName(cartItem.product.getName());
                invoiceItem.setQuantity(cartItem.quantity);
                invoiceItem.setUnitPrice(cartItem.product.getPrice());
                
                invoiceService.addInvoiceItem(invoiceItem);
                invoiceService.updateProductStock(cartItem.product.getId(), -cartItem.quantity);
                invoiceItems.add(invoiceItem);
            }
            
            // Actualizar ID de la factura en los items
            invoice.setId(invoiceId);
            
            // Generar PDF
            try {
                String pdfPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "facturas" + File.separator + "factura_" + invoiceId + ".pdf";
                PdfGenerator.generateInvoicePdf(invoice, invoiceItems, pdfPath);
                showSuccess("Venta procesada exitosamente.\nFactura #" + invoiceId + "\n\nPDF guardado en:\n" + pdfPath);
            } catch (Exception pdfEx) {
                showSuccess("Venta procesada exitosamente. Factura #" + invoiceId + "\n\n(Nota: Error al generar PDF)");
            }
            
            clearCart();
            loadProducts(); // Recargar stock
            
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
            
        } catch (AlmacenException e) {
            showError("Error al procesar venta: " + e.getMessage());
        }
    }

    private void clearCart() {
        cart.clear();
        customerNameField.clear();
        customerEmailField.clear();
        renderCart();
    }

    private void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(productTable.getScene().getWindow());
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(productTable.getScene().getWindow());
        alert.showAndWait();
    }
}
