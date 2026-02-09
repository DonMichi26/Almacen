package com.mycompany.almacen;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.service.ProductService;
import com.mycompany.almacen.dao.BrandDAO;
import com.mycompany.almacen.dao.CategoryDAO;
import com.mycompany.almacen.model.Brand;
import com.mycompany.almacen.model.Category;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AlmacenFX extends Application {

    private ProductService productService;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;
    private ObservableList<Product> productData;
    private TableView<Product> tableView;
    private TextField nameField, descriptionField, priceField, stockField, modelField, searchField;
    private ComboBox<Category> categoryComboBox;
    private ComboBox<Brand> brandComboBox;

    @Override
    public void start(Stage primaryStage) {
        // Inicializar servicios
        productService = new ProductService();
        categoryDAO = new CategoryDAO();
        brandDAO = new BrandDAO();

        // Crear la interfaz de usuario
        createUI(primaryStage);
        
        // Cargar datos iniciales
        loadData();
    }

    private void createUI(Stage primaryStage) {
        // Configurar la escena principal
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Aplicar estilo CSS personalizado
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Crear campos de entrada
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));
        inputGrid.setStyle("-fx-border-color: #d0d0d0; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-background-color: white;");

        // Campos de entrada
        nameField = new TextField();
        nameField.setPromptText("Nombre del producto");
        nameField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        descriptionField = new TextField();
        descriptionField.setPromptText("Descripción");
        descriptionField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        priceField = new TextField();
        priceField.setPromptText("Precio");
        priceField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        stockField = new TextField();
        stockField.setPromptText("Stock");
        stockField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        modelField = new TextField();
        modelField.setPromptText("Modelo");
        modelField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        // Combos
        categoryComboBox = new ComboBox<>();
        categoryComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        brandComboBox = new ComboBox<>();
        brandComboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Añadir opción para crear nueva marca al inicio
        brandComboBox.getItems().add(new Brand(-1, "Agregar nueva marca..."));

        // Cargar categorías y marcas
        loadCategories();
        loadBrands();

        // Listener para manejar la selección de la opción especial
        brandComboBox.setOnAction(event -> {
            Brand selectedBrand = brandComboBox.getValue();
            if (selectedBrand != null && selectedBrand.getId() == -1) {
                // Mostrar diálogo para ingresar nueva marca
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Nueva Marca");
                dialog.setHeaderText("Crear nueva marca");
                dialog.setContentText("Nombre de la nueva marca:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent() && !result.get().trim().isEmpty()) {
                    String newBrandName = result.get().trim();

                    try {
                        // Crear la nueva marca
                        Brand newBrand = new Brand(0, newBrandName);
                        brandDAO.addBrand(newBrand);

                        // Obtener la marca recién creada con su ID asignado
                        Brand createdBrand = brandDAO.getBrandByName(newBrandName);

                        // Recargar las marcas para incluir la nueva
                        loadBrands();

                        // Seleccionar la nueva marca si se creó exitosamente
                        if (createdBrand != null) {
                            brandComboBox.getSelectionModel().select(createdBrand);
                        }
                    } catch (SQLException e) {
                        showAlert("Error", "Error al crear la nueva marca: " + e.getMessage());
                    }
                } else {
                    // Si el usuario canceló o no ingresó un nombre, volver a la selección anterior
                    // Por simplicidad, dejaremos la opción seleccionada, el usuario puede elegir otra
                }
            }
        });

        // Añadir campos al grid
        int row = 0;
        inputGrid.add(new Label("Categoría:"), 0, row);
        inputGrid.add(categoryComboBox, 1, row++);
        inputGrid.add(new Label("Marca:"), 0, row);
        inputGrid.add(brandComboBox, 1, row++);
        inputGrid.add(new Label("Modelo:"), 0, row);
        inputGrid.add(modelField, 1, row++);
        inputGrid.add(new Label("Nombre:"), 0, row);
        inputGrid.add(nameField, 1, row++);
        inputGrid.add(new Label("Descripción:"), 0, row);
        inputGrid.add(descriptionField, 1, row++);
        inputGrid.add(new Label("Precio:"), 0, row);
        inputGrid.add(priceField, 1, row++);
        inputGrid.add(new Label("Stock:"), 0, row);
        inputGrid.add(stockField, 1, row++);

        // Botones
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Agregar");
        Button updateButton = new Button("Actualizar");
        Button deleteButton = new Button("Eliminar");
        Button clearButton = new Button("Limpiar");

        // Estilo de botones
        String buttonStyle = "-fx-font-size: 14px; -fx-padding: 8px 15px; -fx-background-color: #4a90e2; -fx-text-fill: white; -fx-border-radius: 4px; -fx-background-radius: 4px;";
        addButton.setStyle(buttonStyle);
        updateButton.setStyle(buttonStyle.replace("#4a90e2", "#5cb85c"));
        deleteButton.setStyle(buttonStyle.replace("#4a90e2", "#d9534f"));
        clearButton.setStyle(buttonStyle.replace("#4a90e2", "#f0ad4e"));

        addButton.setOnAction(e -> addProduct());
        updateButton.setOnAction(e -> updateProduct());
        deleteButton.setOnAction(e -> deleteProduct());
        clearButton.setOnAction(e -> clearFields());

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);

        // Campo de búsqueda
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Buscar productos...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        Button searchButton = new Button("Buscar");
        searchButton.setStyle(buttonStyle.replace("#4a90e2", "#5bc0de"));
        searchButton.setOnAction(e -> searchProducts());

        searchBox.getChildren().addAll(new Label("Buscar:"), searchField, searchButton);

        // Tabla de productos
        tableView = createProductTable();

        // Añadir componentes al layout principal
        root.getChildren().addAll(
            new Label("Sistema de Gestión de Almacén") {{
                setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-alignment: center; -fx-padding: 10px;");
            }},
            inputGrid,
            buttonBox,
            searchBox,
            tableView
        );

        // Hacer que la tabla ocupe el espacio disponible
        VBox.setVgrow(tableView, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 800); // Aumentar el tamaño inicial
        // Intentar aplicar un CSS externo si existe
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        } catch (Exception e) {
            // Si no existe el archivo CSS, continuamos sin él
            System.out.println("No se encontró el archivo CSS externo, usando estilos básicos");
        }

        // Permitir redimensionamiento
        primaryStage.setTitle("Sistema de Gestión de Almacén");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000); // Establecer tamaño mínimo
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    private TableView<Product> createProductTable() {
        // Crear columnas
        TableColumn<Product, String> categoryCol = new TableColumn<>("Categoría");
        categoryCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                getCategoryName(data.getValue().getCategoryId())
            )
        );

        TableColumn<Product, String> brandCol = new TableColumn<>("Marca");
        brandCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                getBrandName(data.getValue().getBrandId())
            )
        );

        TableColumn<Product, String> modelCol = new TableColumn<>("Modelo");
        modelCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getModel())
        );

        TableColumn<Product, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getName())
        );

        TableColumn<Product, String> descCol = new TableColumn<>("Descripción");
        descCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription())
        );

        TableColumn<Product, Number> priceCol = new TableColumn<>("Precio");
        priceCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice())
        );

        TableColumn<Product, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock())
        );

        // Crear tabla
        TableView<Product> table = new TableView<>();
        table.getColumns().addAll(
            categoryCol, brandCol, modelCol, nameCol, descCol, priceCol, stockCol
        );

        // Configurar la tabla
        table.setItems(getProductData());
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                displaySelectedProduct();
            }
        });

        // Estilo de la tabla
        table.setStyle("-fx-font-size: 13px;");
        table.setPrefHeight(400);

        return table;
    }

    private String getCategoryName(int categoryId) {
        try {
            Category category = categoryDAO.getCategoryById(categoryId);
            return category != null ? category.getName() : "General";
        } catch (SQLException e) {
            return "General";
        }
    }

    private String getBrandName(int brandId) {
        try {
            Brand brand = brandDAO.getBrandById(brandId);
            return brand != null ? brand.getName() : "General";
        } catch (SQLException e) {
            return "General";
        }
    }

    private ObservableList<Product> getProductData() {
        if (productData == null) {
            productData = FXCollections.observableArrayList();
        }
        return productData;
    }

    private void loadData() {
        try {
            List<Product> products = productService.getAllProducts();
            getProductData().setAll(products);
        } catch (Exception e) {
            showAlert("Error", "Error al cargar productos: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            categoryComboBox.getItems().setAll(categories);
            // Establecer un renderer personalizado para mejorar la apariencia
            categoryComboBox.setCellFactory(lv -> new ListCell<Category>() {
                @Override
                protected void updateItem(Category item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.getName());
                    } else {
                        setText(null);
                    }
                }
            });
            // Establecer un string converter para mostrar el nombre de la categoría seleccionada
            categoryComboBox.setConverter(new javafx.util.StringConverter<Category>() {
                @Override
                public String toString(Category category) {
                    return category != null ? category.getName() : "";
                }

                @Override
                public Category fromString(String string) {
                    return null; // No es necesario para este uso
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar categorías: " + e.getMessage());
        }
    }

    private void loadBrands() {
        try {
            // Limpiar items excepto la opción de agregar nueva marca
            brandComboBox.getItems().clear();
            brandComboBox.getItems().add(new Brand(-1, "Agregar nueva marca..."));

            List<Brand> brands = brandDAO.getAllBrands();
            brandComboBox.getItems().addAll(brands);

            // Establecer un renderer personalizado para mejorar la apariencia
            brandComboBox.setCellFactory(lv -> new ListCell<Brand>() {
                @Override
                protected void updateItem(Brand item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.getName());
                    } else {
                        setText(null);
                    }
                }
            });
            // Establecer un string converter para mostrar el nombre de la marca seleccionada
            brandComboBox.setConverter(new javafx.util.StringConverter<Brand>() {
                @Override
                public String toString(Brand brand) {
                    return brand != null ? brand.getName() : "";
                }

                @Override
                public Brand fromString(String string) {
                    return null; // No es necesario para este uso
                }
            });
        } catch (SQLException e) {
            showAlert("Error", "Error al cargar marcas: " + e.getMessage());
        }
    }

    private void addProduct() {
        try {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                showAlert("Advertencia", "El nombre del producto no puede estar vacío.");
                return;
            }

            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            String model = modelField.getText();

            Category selectedCategory = categoryComboBox.getValue();
            Brand selectedBrand = brandComboBox.getValue();

            int categoryId = selectedCategory != null ? selectedCategory.getId() : 1;
            int brandId = selectedBrand != null && selectedBrand.getId() != -1 ? selectedBrand.getId() : 1; // Usar General si no hay selección válida

            Product product = new Product(0, name, description, price, stock, categoryId, brandId, model);
            productService.addProduct(product);

            showAlert("Éxito", "Producto agregado exitosamente.");
            clearFields();
            loadData();
        } catch (NumberFormatException e) {
            showAlert("Error", "Por favor, ingrese valores numéricos válidos para Precio y Stock.");
        } catch (Exception e) {
            showAlert("Error", "Error al agregar producto: " + e.getMessage());
        }
    }

    private void updateProduct() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Advertencia", "Por favor, seleccione un producto para actualizar.");
            return;
        }

        try {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                showAlert("Advertencia", "El nombre del producto no puede estar vacío.");
                return;
            }

            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            String model = modelField.getText();

            Category selectedCategory = categoryComboBox.getValue();
            Brand selectedBrand = brandComboBox.getValue();

            int categoryId = selectedCategory != null ? selectedCategory.getId() : 1;
            int brandId = selectedBrand != null && selectedBrand.getId() != -1 ? selectedBrand.getId() : 1; // Usar General si no hay selección válida

            Product product = new Product(selectedProduct.getId(), name, description, price, stock, categoryId, brandId, model);
            productService.updateProduct(product);

            showAlert("Éxito", "Producto actualizado exitosamente.");
            clearFields();
            loadData();
        } catch (NumberFormatException e) {
            showAlert("Error", "Por favor, ingrese valores numéricos válidos para Precio y Stock.");
        } catch (Exception e) {
            showAlert("Error", "Error al actualizar producto: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Advertencia", "Por favor, seleccione un producto para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar el producto?");
        alert.setContentText("Producto: " + selectedProduct.getName());

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                productService.deleteProduct(selectedProduct.getId());
                showAlert("Éxito", "Producto eliminado exitosamente.");
                clearFields();
                loadData();
            } catch (Exception e) {
                showAlert("Error", "Error al eliminar producto: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        stockField.clear();
        modelField.clear();
        categoryComboBox.setValue(null);
        brandComboBox.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

    private void displaySelectedProduct() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            nameField.setText(selectedProduct.getName());
            descriptionField.setText(selectedProduct.getDescription());
            priceField.setText(String.valueOf(selectedProduct.getPrice()));
            stockField.setText(String.valueOf(selectedProduct.getStock()));
            modelField.setText(selectedProduct.getModel());

            // Seleccionar categoría y marca correspondientes
            try {
                Category category = categoryDAO.getCategoryById(selectedProduct.getCategoryId());
                categoryComboBox.setValue(category);
                
                Brand brand = brandDAO.getBrandById(selectedProduct.getBrandId());
                brandComboBox.setValue(brand);
            } catch (SQLException e) {
                showAlert("Error", "Error al cargar datos del producto: " + e.getMessage());
            }
        }
    }

    private void searchProducts() {
        String searchTerm = searchField.getText();
        try {
            List<Product> products;
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                products = productService.getAllProducts();
            } else {
                // Implementar búsqueda aquí
                products = productService.getAllProducts().stream()
                    .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                 p.getDescription().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                 (p.getModel() != null && p.getModel().toLowerCase().contains(searchTerm.toLowerCase())))
                    .collect(java.util.stream.Collectors.toList());
            }
            getProductData().setAll(products);
        } catch (Exception e) {
            showAlert("Error", "Error al buscar productos: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}