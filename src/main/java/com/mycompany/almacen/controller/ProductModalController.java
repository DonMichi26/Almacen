package com.mycompany.almacen.controller;

import com.mycompany.almacen.gui.components.StockBadge;
import com.mycompany.almacen.model.Brand;
import com.mycompany.almacen.model.Category;
import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.service.BrandService;
import com.mycompany.almacen.service.CategoryService;
import com.mycompany.almacen.service.ProductService;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.exception.ValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * Controller para el modal de Agregar/Editar Producto.
 */
public class ProductModalController {

    @FXML private BorderPane productModalRoot;
    @FXML private Label modalTitle;
    
    // Campos del formulario
    @FXML private TextField nameField;
    @FXML private TextField modelField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private ComboBox<Brand> brandComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    
    // Errores de validación
    @FXML private Label nameError;
    @FXML private Label priceError;
    @FXML private Label stockError;
    
    // Badge de stock
    @FXML private StackPane stockBadgeContainer;
    
    // Botones
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Button addBrandButton;

    private ProductService productService;
    private CategoryService categoryService;
    private BrandService brandService;
    private Product existingProduct;
    private Runnable onSaveCallback;
    private StockBadge stockBadge;

    public ProductModalController() {
    }

    public void initialize(ProductService productService, 
                          CategoryService categoryService, 
                          BrandService brandService,
                          Runnable onSaveCallback) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.onSaveCallback = onSaveCallback;
        
        setupEventHandlers();
        loadComboBoxes();
        setupStockBadgeListener();
    }

    private void setupEventHandlers() {
        cancelButton.setOnAction(e -> close());
        saveButton.setOnAction(e -> saveProduct());
        addBrandButton.setOnAction(e -> addNewBrand());
        
        // Validación en tiempo real
        nameField.textProperty().addListener((obs, old, newValue) -> validateName());
        priceField.textProperty().addListener((obs, old, newValue) -> validatePrice());
        stockField.textProperty().addListener((obs, old, newValue) -> {
            validateStock();
            updateStockBadge();
        });
    }

    private void setupStockBadgeListener() {
        stockBadge = new StockBadge(0);
        stockBadgeContainer.getChildren().add(stockBadge);
    }

    private void loadComboBoxes() {
        try {
            // Cargar categorías
            List<Category> categories = categoryService.getAllCategories();
            categoryComboBox.getItems().setAll(categories);
            
            // Cargar marcas
            List<Brand> brands = brandService.getAllBrands();
            brandComboBox.getItems().setAll(brands);
            
            // Seleccionar primera opción por defecto
            if (!categories.isEmpty()) {
                categoryComboBox.getSelectionModel().selectFirst();
            }
            if (!brands.isEmpty()) {
                brandComboBox.getSelectionModel().selectFirst();
            }
        } catch (AlmacenException e) {
            showError("Error al cargar datos: " + e.getMessage());
        }
    }

    /**
     * Configura el modal para editar un producto existente.
     */
    public void setProduct(Product product) {
        this.existingProduct = product;
        modalTitle.setText("Editar Producto");
        fillFields(product);
    }

    private void fillFields(Product product) {
        nameField.setText(product.getName());
        modelField.setText(product.getModel() != null ? product.getModel() : "");
        descriptionField.setText(product.getDescription() != null ? product.getDescription() : "");
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
        
        // Seleccionar categoría
        try {
            Category category = categoryService.getCategoryById(product.getCategoryId());
            if (category != null) {
                categoryComboBox.getSelectionModel().select(category);
            }
        } catch (AlmacenException e) {
            // Ignorar
        }
        
        // Seleccionar marca
        try {
            Brand brand = brandService.getBrandById(product.getBrandId());
            if (brand != null) {
                brandComboBox.getSelectionModel().select(brand);
            }
        } catch (AlmacenException e) {
            // Ignorar
        }
        
        updateStockBadge();
    }

    private void saveProduct() {
        if (!validateForm()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String model = modelField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            
            Category category = categoryComboBox.getValue();
            Brand brand = brandComboBox.getValue();
            
            int categoryId = category != null ? category.getId() : 1;
            int brandId = brand != null ? brand.getId() : 1;

            if (existingProduct != null) {
                // Actualizar producto existente
                Product product = new Product(
                    existingProduct.getId(), name, description, price, stock, 
                    categoryId, brandId, model
                );
                productService.updateProduct(product);
            } else {
                // Crear nuevo producto
                Product product = new Product(
                    0, name, description, price, stock, 
                    categoryId, brandId, model
                );
                productService.addProduct(product);
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            close();

        } catch (ValidationException e) {
            showValidationErrors(e);
        } catch (AlmacenException e) {
            showError("Error al guardar: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Por favor ingrese valores numéricos válidos");
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        
        valid &= validateName();
        valid &= validatePrice();
        valid &= validateStock();
        
        return valid;
    }

    private boolean validateName() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameError.setText("El nombre es obligatorio");
            nameError.setVisible(true);
            nameError.setManaged(true);
            nameField.getStyleClass().add("error");
            return false;
        }
        nameError.setVisible(false);
        nameError.setManaged(false);
        nameField.getStyleClass().remove("error");
        return true;
    }

    private boolean validatePrice() {
        String priceText = priceField.getText().trim();
        if (priceText.isEmpty()) {
            priceError.setText("El precio es obligatorio");
            priceError.setVisible(true);
            priceError.setManaged(true);
            priceField.getStyleClass().add("error");
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                priceError.setText("El precio debe ser >= 0");
                priceError.setVisible(true);
                priceError.setManaged(true);
                return false;
            }
        } catch (NumberFormatException e) {
            priceError.setText("Valor inválido");
            priceError.setVisible(true);
            priceError.setManaged(true);
            return false;
        }
        
        priceError.setVisible(false);
        priceError.setManaged(false);
        priceField.getStyleClass().remove("error");
        return true;
    }

    private boolean validateStock() {
        String stockText = stockField.getText().trim();
        if (stockText.isEmpty()) {
            stockError.setText("El stock es obligatorio");
            stockError.setVisible(true);
            stockError.setManaged(true);
            stockField.getStyleClass().add("error");
            return false;
        }
        
        try {
            int stock = Integer.parseInt(stockText);
            if (stock < 0) {
                stockError.setText("El stock debe ser >= 0");
                stockError.setVisible(true);
                stockError.setManaged(true);
                return false;
            }
        } catch (NumberFormatException e) {
            stockError.setText("Valor inválido");
            stockError.setVisible(true);
            stockError.setManaged(true);
            return false;
        }
        
        stockError.setVisible(false);
        stockError.setManaged(false);
        stockField.getStyleClass().remove("error");
        return true;
    }

    private void updateStockBadge() {
        try {
            int stock = Integer.parseInt(stockField.getText().trim());
            stockBadge.updateStock(stock);
        } catch (NumberFormatException e) {
            stockBadge.updateStock(0);
        }
    }

    private void addNewBrand() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Marca");
        dialog.setHeaderText("Crear nueva marca");
        dialog.setContentText("Nombre de la marca:");
        
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                try {
                    Brand newBrand = new Brand(0, name.trim());
                    brandService.addBrand(newBrand);
                    loadComboBoxes();
                    
                    // Seleccionar la nueva marca
                    Brand createdBrand = brandService.getBrandByName(name.trim());
                    if (createdBrand != null) {
                        brandComboBox.getSelectionModel().select(createdBrand);
                    }
                } catch (AlmacenException e) {
                    showError("Error al crear marca: " + e.getMessage());
                }
            }
        });
    }

    private void showValidationErrors(ValidationException e) {
        if (e.hasFieldError("name")) {
            nameError.setText(e.getFieldError("name"));
            nameError.setVisible(true);
            nameError.setManaged(true);
        }
        if (e.hasFieldError("price")) {
            priceError.setText(e.getFieldError("price"));
            priceError.setVisible(true);
            priceError.setManaged(true);
        }
        if (e.hasFieldError("stock")) {
            stockError.setText(e.getFieldError("stock"));
            stockError.setVisible(true);
            stockError.setManaged(true);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(productModalRoot.getScene().getWindow());
        alert.showAndWait();
    }

    private void close() {
        productModalRoot.getScene().getWindow().hide();
    }
}
