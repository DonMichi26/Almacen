package com.mycompany.almacen.controller;

import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.service.ProductService;
import com.mycompany.almacen.service.CategoryService;
import com.mycompany.almacen.service.BrandService;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.controller.ProductModalController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.io.IOException;

/**
 * Controller para el Catálogo de Productos con TableView.
 */
public class ProductCatalogController {

    @FXML private VBox catalogRoot;
    @FXML private Button newProductButton;
    @FXML private TextField searchField;
    @FXML private Button clearSearchButton;
    
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colModel;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colBrand;
    @FXML private TableColumn<Product, Number> colPrice;
    @FXML private TableColumn<Product, Number> colStock;
    @FXML private TableColumn<Product, Void> colActions;

    private ProductService productService;
    private CategoryService categoryService;
    private BrandService brandService;
    private ObservableList<Product> productList;

    public ProductCatalogController() {
        this.productList = FXCollections.observableArrayList();
    }

    public void initialize(ProductService productService, 
                          CategoryService categoryService,
                          BrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        
        setupTableColumns();
        setupEventHandlers();
        loadProducts();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        
        colModel.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getModel() != null ? cellData.getValue().getModel() : ""));
        
        colCategory.setCellValueFactory(cellData -> {
            try {
                String catName = productService.getCategoryName(cellData.getValue().getCategoryId());
                return new javafx.beans.property.SimpleStringProperty(catName);
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("General");
            }
        });
        
        colBrand.setCellValueFactory(cellData -> {
            try {
                String brandName = productService.getBrandName(cellData.getValue().getBrandId());
                return new javafx.beans.property.SimpleStringProperty(brandName);
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("General");
            }
        });
        
        colPrice.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getPrice()));
        
        colStock.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStock()));
        
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            
            {
                editBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    openEditProductDialog(product);
                });
                deleteBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    deleteProduct(product);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
                }
            }
        });
        
        productsTable.setItems(productList);
    }

    private void setupEventHandlers() {
        searchField.textProperty().addListener((obs, old, newValue) -> {
            filterProducts(newValue);
        });

        clearSearchButton.setOnAction(e -> {
            searchField.clear();
            loadProducts();
        });

        newProductButton.setOnAction(e -> openNewProductDialog());
    }

    public void loadProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            productList.setAll(products);
        } catch (AlmacenException e) {
            showError("Error al cargar productos: " + e.getMessage());
        }
    }

    private void filterProducts(String searchTerm) {
        try {
            List<Product> allProducts = productService.getAllProducts();
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                productList.setAll(allProducts);
            } else {
                String term = searchTerm.toLowerCase();
                List<Product> filtered = allProducts.stream()
                    .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(term)) ||
                                 (p.getModel() != null && p.getModel().toLowerCase().contains(term)))
                    .collect(java.util.stream.Collectors.toList());
                productList.setAll(filtered);
            }
        } catch (AlmacenException e) {
            showError("Error al filtrar: " + e.getMessage());
        }
    }

    private void openNewProductDialog() {
        openEditProductDialog(null);
    }

    private void openEditProductDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/product-modal.fxml"));
            BorderPane modalContent = loader.load();
            
            ProductModalController controller = loader.getController();
            
            // Primero inicializar servicios
            controller.initialize(productService, categoryService, brandService, () -> {
                loadProducts();
            });
            
            // Luego cargar datos del producto si es edición
            if (product != null) {
                controller.setProduct(product);
            }
            
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(productsTable.getScene().getWindow());
            dialog.setTitle(product == null ? "Nuevo Producto" : "Editar Producto");
            
            Scene scene = new Scene(modalContent);
            scene.getStylesheets().add(getClass().getResource("/styles/modern-theme.css").toExternalForm());
            dialog.setScene(scene);
            
            dialog.showAndWait();
            
        } catch (Exception e) {
            showError("Error al abrir diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteProduct(Product product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro de eliminar el producto \"" + product.getName() + "\"?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    productService.deleteProduct(product.getId());
                    loadProducts();
                    showSuccess("Producto eliminado correctamente");
                } catch (AlmacenException e) {
                    showError("Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
