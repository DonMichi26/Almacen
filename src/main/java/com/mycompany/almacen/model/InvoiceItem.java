package com.mycompany.almacen.model;

public class InvoiceItem {
    private int id;
    private int invoiceId;
    private int productId;
    private String productName; // To store product name for easier display
    private int quantity;
    private double unitPrice;

    public InvoiceItem(int id, int invoiceId, int productId, String productName, int quantity, double unitPrice) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Constructor without ID for new invoice items
    public InvoiceItem(int invoiceId, int productId, String productName, int quantity, double unitPrice) {
        this(-1, invoiceId, productId, productName, quantity, unitPrice); // -1 indicates no ID yet
    }

    // Constructor vacío para frameworks
    public InvoiceItem() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "InvoiceItem{" +
               "id=" + id +
               ", invoiceId=" + invoiceId +
               ", productId=" + productId +
               ", productName='" + productName + "'" +
               ", quantity=" + quantity +
               ", unitPrice=" + unitPrice +
               '}';
    }
}
