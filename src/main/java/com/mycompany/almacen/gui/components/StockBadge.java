package com.mycompany.almacen.gui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;

/**
 * Badge visual para estados de stock.
 * - Verde: Stock disponible (> 10)
 * - Amarillo: Stock bajo (1-10)
 * - Rojo: Sin stock (0)
 */
public class StockBadge extends StackPane {

    public enum StockStatus {
        IN_STOCK("En Stock", "in-stock"),
        LOW_STOCK("Stock Bajo", "low-stock"),
        OUT_OF_STOCK("Agotado", "out-of-stock");

        private final String text;
        private final String styleClass;

        StockStatus(String text, String styleClass) {
            this.text = text;
            this.styleClass = styleClass;
        }

        public String getText() {
            return text;
        }

        public String getStyleClass() {
            return styleClass;
        }
    }

    private int stock;
    private final Label label;

    public StockBadge(int stock) {
        this.stock = stock;
        this.label = new Label();
        initialize();
        updateStock(stock);
    }

    private void initialize() {
        label.getStyleClass().addAll("stock-badge");
        label.setPadding(new Insets(4, 10, 4, 10));
        getChildren().add(label);
    }

    /**
     * Actualiza el stock y refresca el badge.
     * Útil para actualizaciones en tiempo real.
     */
    public void updateStock(int newStock) {
        this.stock = newStock;
        StockStatus status = getStockStatus(newStock);
        
        label.setText(status.getText());
        label.getStyleClass().removeAll("in-stock", "low-stock", "out-of-stock");
        label.getStyleClass().add(status.getStyleClass());
        
        // Tooltip con cantidad exacta
        label.setTooltip(new Tooltip(stock + " unidades disponibles"));
    }

    private StockStatus getStockStatus(int stock) {
        if (stock <= 0) {
            return StockStatus.OUT_OF_STOCK;
        } else if (stock <= 10) {
            return StockStatus.LOW_STOCK;
        } else {
            return StockStatus.IN_STOCK;
        }
    }

    public int getStock() {
        return stock;
    }

    public StockStatus getStatus() {
        return getStockStatus(stock);
    }

    /**
     * Verifica si el stock es bajo (<= 10).
     */
    public boolean isLowStock() {
        return stock <= 10;
    }

    /**
     * Verifica si está agotado.
     */
    public boolean isOutOfStock() {
        return stock <= 0;
    }
}
