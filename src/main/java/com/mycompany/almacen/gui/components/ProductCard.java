package com.mycompany.almacen.gui.components;

import com.mycompany.almacen.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Tarjeta de producto para el catálogo moderno.
 * Diseño limpio con información esencial y badge de stock.
 */
public class ProductCard extends VBox {

    private final Product product;
    private final Runnable onSelectCallback;

    public ProductCard(Product product) {
        this(product, null);
    }

    public ProductCard(Product product, Runnable onSelectCallback) {
        this.product = product;
        this.onSelectCallback = onSelectCallback;
        initialize();
    }

    private void initialize() {
        getStyleClass().addAll("product-card");
        setPadding(new Insets(16));
        setSpacing(10);
        setCursor(Cursor.HAND);

        // Header: Nombre y Modelo
        HBox header = createHeader();
        
        // Descripción
        Label descriptionLabel = createDescription();
        
        // Precio y Stock
        HBox footer = createFooter();

        getChildren().addAll(header, descriptionLabel, footer);

        // Click handler
        setOnMouseClicked(e -> {
            getStyleClass().remove("selected");
            if (onSelectCallback != null) {
                onSelectCallback.run();
            }
        });
    }

    private HBox createHeader() {
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        if (product.getModel() != null && !product.getModel().isEmpty()) {
            Label modelLabel = new Label(product.getModel());
            modelLabel.getStyleClass().add("product-model");
            header.getChildren().add(modelLabel);
        }

        header.getChildren().add(nameLabel);
        return header;
    }

    private Label createDescription() {
        Label label = new Label(
            product.getDescription() != null && !product.getDescription().isEmpty()
                ? product.getDescription()
                : "Sin descripción"
        );
        label.getStyleClass().add("product-description");
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);

        // Precio
        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        // Badge de Stock
        StockBadge stockBadge = new StockBadge(product.getStock());

        footer.getChildren().addAll(priceLabel, stockBadge);
        return footer;
    }

    public Product getProduct() {
        return product;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            getStyleClass().add("selected");
        } else {
            getStyleClass().remove("selected");
        }
    }
}
