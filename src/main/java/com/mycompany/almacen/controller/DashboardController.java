package com.mycompany.almacen.controller;

import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.service.ProductService;
import com.mycompany.almacen.service.InvoiceService;
import com.mycompany.almacen.exception.AlmacenException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para el Dashboard con KPIs.
 * Muestra métricas clave del inventario y ventas.
 */
public class DashboardController {

    @FXML private VBox dashboardRoot;
    
    // KPI Labels - Valor de Inventario
    @FXML private Label inventoryValueLabel;
    @FXML private Label inventoryValueChange;
    
    // KPI Labels - Ventas del Día
    @FXML private Label dailySalesLabel;
    @FXML private Label dailySalesChange;
    
    // KPI Labels - Stock Bajo
    @FXML private Label lowStockLabel;
    @FXML private Label lowStockChange;
    
    // KPI Labels - Total Productos
    @FXML private Label totalProductsLabel;
    @FXML private Label totalProductsChange;

    @FXML private Button refreshButton;

    private ProductService productService;
    private InvoiceService invoiceService;

    public DashboardController() {
    }

    public void initialize(ProductService productService, InvoiceService invoiceService) {
        this.productService = productService;
        this.invoiceService = invoiceService;
        loadDashboardData();
    }

    /**
     * Carga todas las métricas del dashboard.
     */
    public void loadDashboardData() {
        try {
            loadInventoryValue();
            loadDailySales();
            loadLowStock();
            loadTotalProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calcula y muestra el valor total del inventario.
     * Fórmula: SUM(precio * stock) para todos los productos
     */
    private void loadInventoryValue() {
        try {
            List<Product> products = productService.getAllProducts();
            double totalValue = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
            
            inventoryValueLabel.setText(String.format("$%,.2f", totalValue));
            
            // Simular cambio (en producción, comparar con período anterior)
            inventoryValueChange.setText("+0% vs mes anterior");
            
        } catch (AlmacenException e) {
            inventoryValueLabel.setText("$0.00");
            inventoryValueChange.setText("Error al cargar");
        }
    }

    /**
     * Calcula y muestra las ventas del día actual.
     */
    private void loadDailySales() {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            LocalDate today = LocalDate.now();
            
            double dailyTotal = invoices.stream()
                .filter(inv -> inv.getInvoiceDate() != null && 
                              LocalDate.from(inv.getInvoiceDate().toInstant()).equals(today))
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
            
            long dailyCount = invoices.stream()
                .filter(inv -> inv.getInvoiceDate() != null &&
                              LocalDate.from(inv.getInvoiceDate().toInstant()).equals(today))
                .count();
            
            dailySalesLabel.setText(String.format("$%,.2f", dailyTotal));
            dailySalesChange.setText("+" + dailyCount + " ventas");
            
        } catch (AlmacenException e) {
            dailySalesLabel.setText("$0.00");
            dailySalesChange.setText("Error al cargar");
        }
    }

    /**
     * Cuenta productos con stock bajo (<= 10 unidades).
     */
    private void loadLowStock() {
        try {
            List<Product> products = productService.getAllProducts();
            long lowStockCount = products.stream()
                .filter(p -> p.getStock() <= 10)
                .count();
            
            lowStockLabel.setText(lowStockCount + " productos");
            
            if (lowStockCount > 0) {
                lowStockChange.setText("Requieren atención");
                lowStockChange.getStyleClass().add("negative");
            } else {
                lowStockChange.setText("Todo en orden");
                lowStockChange.getStyleClass().remove("negative");
            }
            
        } catch (AlmacenException e) {
            lowStockLabel.setText("0 productos");
            lowStockChange.setText("Error al cargar");
        }
    }

    /**
     * Cuenta el total de productos en el catálogo.
     */
    private void loadTotalProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            totalProductsLabel.setText(String.valueOf(products.size()));
            totalProductsChange.setText("En catálogo");
            
        } catch (AlmacenException e) {
            totalProductsLabel.setText("0");
            totalProductsChange.setText("Error al cargar");
        }
    }

    /**
     * Actualiza un producto específico en el dashboard.
     * Útil para actualizaciones en tiempo real cuando cambia el stock.
     */
    public void refreshProduct(Product product) {
        // Recargar métricas afectadas
        loadInventoryValue();
        loadLowStock();
    }
}
