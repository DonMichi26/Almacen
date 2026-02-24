package com.mycompany.almacen;

import com.mycompany.almacen.controller.*;
import com.mycompany.almacen.database.DatabaseManager;
import com.mycompany.almacen.service.*;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX con UI moderna.
 * Integra Dashboard, Catálogo de Productos y Venta Rápida.
 */
public class AlmacenFX extends Application {

    // Servicios
    private ProductService productService;
    private CategoryService categoryService;
    private BrandService brandService;
    private InvoiceService invoiceService;

    // Controladores
    private DashboardController dashboardController;
    private ProductCatalogController catalogController;

    // UI Principal
    private BorderPane mainLayout;
    private TabPane tabPane;

    @Override
    public void start(Stage primaryStage) {
        initializeServices();
        createMainLayout(primaryStage);
    }

    /**
     * Inicializa servicios con inyección de dependencias.
     */
    private void initializeServices() {
        DatabaseManager.createTables();
        DatabaseManager.seedDatabaseWithSampleData();

        productService = new ProductService();
        categoryService = new CategoryService();
        brandService = new BrandService();
        invoiceService = new InvoiceService();
    }

    /**
     * Crea el layout principal con navegación lateral.
     */
    private void createMainLayout(Stage primaryStage) {
        mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // Sidebar de navegación
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        // Área de contenido (TabPane)
        tabPane = createTabPane();
        mainLayout.setCenter(tabPane);

        // Cargar contenido de las tabs
        loadDashboard();
        loadProductCatalog();
        loadInvoices();

        // Crear escena
        Scene scene = new Scene(mainLayout, 1400, 900);
        scene
            .getStylesheets()
            .add(
                getClass()
                    .getResource("/styles/modern-theme.css")
                    .toExternalForm()
            );

        // Configurar stage
        primaryStage.setTitle("Sistema de Gestión de Almacén");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }

    /**
     * Crea la barra lateral de navegación.
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        // Logo / Título
        Label title = new Label("📦 Almacén");
        title.setStyle(
            "-fx-font-size: 20px; -fx-font-weight: 700; -fx-text-fill: -primary;"
        );
        title.setStyle("-fx-padding: 0 0 20px 0;");

        // Botones de navegación
        Button dashboardBtn = createNavButton("📊 Dashboard", () ->
            tabPane.getSelectionModel().select(0)
        );
        Button productsBtn = createNavButton("🏷️ Productos", () ->
            tabPane.getSelectionModel().select(1)
        );
        Button salesBtn = createNavButton("💰 Ventas", this::openQuickSale);
        Button invoicesBtn = createNavButton("📄 Facturas", () ->
            tabPane.getSelectionModel().select(2)
        );

        separator = new Separator();
        separator.getStyleClass().add("separator");

        // Botón de configuración
        Button settingsBtn = createNavButton("⚙️ Configuración", () ->
            showSettings()
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Botón de ayuda
        Button helpBtn = createNavButton("❓ Ayuda", this::showHelp);

        sidebar
            .getChildren()
            .addAll(
                title,
                dashboardBtn,
                productsBtn,
                salesBtn,
                invoicesBtn,
                separator,
                settingsBtn,
                spacer,
                helpBtn
            );

        return sidebar;
    }

    private Separator separator;

    /**
     * Crea un botón de navegación.
     */
    private Button createNavButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().addAll("button", "ghost");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());
        return button;
    }

    /**
     * Crea el TabPane para el contenido principal.
     */
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("content-area");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabPane;
    }

    /**
     * Carga el Dashboard en una pestaña.
     */
    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/dashboard.fxml")
            );
            VBox dashboard = loader.load();

            dashboardController = loader.getController();
            dashboardController.initialize(productService, invoiceService);

            Tab tab = new Tab("Dashboard", dashboard);
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            showError("Error al cargar Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga el Catálogo de Productos en una pestaña.
     */
    private void loadProductCatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/product-catalog.fxml")
            );
            VBox catalog = loader.load();

            catalogController = loader.getController();
            catalogController.initialize(
                productService,
                categoryService,
                brandService
            );

            Tab tab = new Tab("Productos", catalog);
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            showError("Error al cargar Catálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre el modal de Venta Rápida.
     */
    private void openQuickSale() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/quick-sale-modal.fxml")
            );
            BorderPane modalContent = loader.load();

            QuickSaleModalController controller = loader.getController();
            controller.initialize(productService, invoiceService, () -> {
                // Callback al cerrar: refrescar dashboard y catálogo
                refreshDashboard();
                if (catalogController != null) {
                    catalogController.loadProducts();
                }
            });

            // Crear diálogo modal
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(mainLayout.getScene().getWindow());
            dialog.setTitle("Venta Rápida");

            Scene scene = new Scene(modalContent);
            scene
                .getStylesheets()
                .add(
                    getClass()
                        .getResource("/styles/modern-theme.css")
                        .toExternalForm()
                );
            dialog.setScene(scene);

            dialog.showAndWait();
        } catch (IOException e) {
            showError("Error al abrir Venta Rápida: " + e.getMessage());
        }
    }

    /**
     * Carga la pestaña de Facturas (placeholder).
     */
    private void loadInvoices() {
        VBox content = new VBox(20);
        content.getStyleClass().add("main-container");
        content.setPadding(new javafx.geometry.Insets(20));

        Label title = new Label("📄 Gestión de Facturas");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 700;");

        Label description = new Label("Módulo de facturación en desarrollo...");
        description.setStyle(
            "-fx-font-size: 14px; -fx-text-fill: -text-secondary;"
        );

        content.getChildren().addAll(title, description);

        Tab tab = new Tab("Facturas", content);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    /**
     * Refresca los datos del Dashboard.
     */
    private void refreshDashboard() {
        if (dashboardController != null) {
            dashboardController.loadDashboardData();
        }
    }

    /**
     * Muestra la configuración (placeholder).
     */
    private void showSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuración");
        alert.setHeaderText("Configuración del Sistema");
        alert.setContentText("Módulo de configuración en desarrollo...");
        alert.showAndWait();
    }

    /**
     * Muestra la ayuda (placeholder).
     */
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ayuda");
        alert.setHeaderText("Sistema de Gestión de Almacén");

        String helpText =
            "Atajos de teclado:\n" +
            "• Ctrl+D: Ir al Dashboard\n" +
            "• Ctrl+P: Ir a Productos\n" +
            "• Ctrl+V: Nueva Venta Rápida\n" +
            "• F5: Actualizar datos\n\n" +
            "Funcionalidades:\n" +
            "• Dashboard: Métricas en tiempo real\n" +
            "• Productos: Catálogo con búsqueda inteligente\n" +
            "• Ventas: Proceso rápido de facturación\n";

        TextArea textArea = new TextArea(helpText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(300);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de error.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
