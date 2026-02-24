package com.mycompany.almacen.model;

import java.util.Date;
import java.util.List;

public class Invoice {
    public enum InvoiceType { SALE, SERVICE }

    private int id;
    private Date invoiceDate;
    private String customerName;
    private double totalAmount;
    private InvoiceType invoiceType;
    private String description;
    private List<InvoiceItem> items; // Not persisted directly in DB for Invoice table

    // Full constructor
    public Invoice(int id, Date invoiceDate, String customerName, double totalAmount, InvoiceType invoiceType, String description) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.invoiceType = invoiceType;
        this.description = description;
    }

    // Constructor for new sales invoices from GUI
    public Invoice(Date invoiceDate, String customerName, double totalAmount) {
        this(-1, invoiceDate, customerName, totalAmount, InvoiceType.SALE, null);
    }
    
    // Constructor for new service invoices from GUI
    public Invoice(Date invoiceDate, String customerName, double totalAmount, String description) {
        this(-1, invoiceDate, customerName, totalAmount, InvoiceType.SERVICE, description);
    }

    // Constructor vacío para frameworks y reflexión
    public Invoice() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    public InvoiceType getInvoiceType() { return invoiceType; }
    public void setInvoiceType(InvoiceType invoiceType) { this.invoiceType = invoiceType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // Setter para email del cliente (campo adicional)
    private String customerEmail;
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    @Override
    public String toString() {
        return "Invoice{" +
               "id=" + id +
               ", invoiceDate=" + invoiceDate +
               ", customerName='" + customerName + "'" +
               ", totalAmount=" + totalAmount +
               ", type=" + invoiceType +
               ", description='" + description + "'" +
               ", items=" + items +
               '}';
    }
}
