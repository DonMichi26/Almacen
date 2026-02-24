package com.mycompany.almacen.service;

import com.mycompany.almacen.dao.InvoiceDAO;
import com.mycompany.almacen.dao.InvoiceItemDAO;
import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.database.DatabaseManager;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.exception.ValidationException;
import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.model.InvoiceItem;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la gestión de facturas.
 * Encapsula la lógica de negocio relacionada con facturas e ítems.
 */
public class InvoiceService {
    private InvoiceDAO invoiceDAO;
    private InvoiceItemDAO invoiceItemDAO;
    private ProductDAO productDAO;

    /**
     * Constructor por defecto con inicialización interna de DAOs.
     */
    public InvoiceService() {
        this(new InvoiceDAO(), new InvoiceItemDAO(), new ProductDAO());
    }

    /**
     * Constructor con inyección de dependencias para testing.
     */
    public InvoiceService(InvoiceDAO invoiceDAO, InvoiceItemDAO invoiceItemDAO, ProductDAO productDAO) {
        this.invoiceDAO = invoiceDAO;
        this.invoiceItemDAO = invoiceItemDAO;
        this.productDAO = productDAO;
    }

    public int addInvoice(Invoice invoice) throws AlmacenException {
        try {
            validateInvoice(invoice);
            return invoiceDAO.addInvoice(invoice);
        } catch (ValidationException e) {
            throw e;
        } catch (SQLException e) {
            throw new AlmacenException("Error al agregar factura: " + e.getMessage(), e);
        }
    }

    public Invoice getInvoiceById(int id) throws AlmacenException {
        try {
            return invoiceDAO.getInvoiceById(id);
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener factura por ID: " + e.getMessage(), e);
        }
    }

    public List<Invoice> getAllInvoices() throws AlmacenException {
        try {
            return invoiceDAO.getAllInvoices();
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener todas las facturas: " + e.getMessage(), e);
        }
    }

    public void addInvoiceItem(InvoiceItem item) throws AlmacenException {
        try {
            validateInvoiceItem(item);
            invoiceItemDAO.addInvoiceItem(item);
        } catch (ValidationException e) {
            throw e;
        } catch (SQLException e) {
            throw new AlmacenException("Error al agregar ítem de factura: " + e.getMessage(), e);
        }
    }

    public List<InvoiceItem> getInvoiceItemsByInvoiceId(int invoiceId) throws AlmacenException {
        try {
            return invoiceItemDAO.getInvoiceItemsByInvoiceId(invoiceId);
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener ítems de factura: " + e.getMessage(), e);
        }
    }

    public void updateProductStock(int productId, int quantityChange) throws AlmacenException {
        try {
            productDAO.updateProductStock(productId, quantityChange);
        } catch (SQLException e) {
            throw new AlmacenException("Error al actualizar stock del producto: " + e.getMessage(), e);
        }
    }

    public int processSale(Invoice invoice, List<InvoiceItem> items) throws AlmacenException {
        validateInvoice(invoice);
        
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                invoice.setId(0);
                int invoiceId = invoiceDAO.addInvoice(conn, invoice);
                
                for (InvoiceItem item : items) {
                    validateInvoiceItem(item);
                    item.setInvoiceId(invoiceId);
                    invoiceItemDAO.addInvoiceItem(conn, item);
                    productDAO.updateProductStock(item.getProductId(), -item.getQuantity());
                }
                
                conn.commit();
                return invoiceId;
                
            } catch (Exception e) {
                conn.rollback();
                throw new AlmacenException("Error al procesar venta: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new AlmacenException("Error de base de datos al procesar venta: " + e.getMessage(), e);
        }
    }

    private void validateInvoice(Invoice invoice) throws ValidationException {
        ValidationException exception = new ValidationException("Errores de validación en la factura");

        if (invoice == null) {
            exception.addFieldError("invoice", "La factura no puede ser nula");
        } else {
            if (invoice.getCustomerName() == null || invoice.getCustomerName().trim().isEmpty()) {
                exception.addFieldError("customerName", "El nombre del cliente es obligatorio");
            }
            if (invoice.getTotalAmount() < 0) {
                exception.addFieldError("totalAmount", "El monto total debe ser mayor o igual a cero");
            }
        }

        if (exception.hasErrors()) {
            throw exception;
        }
    }

    private void validateInvoiceItem(InvoiceItem item) throws ValidationException {
        ValidationException exception = new ValidationException("Errores de validación en el ítem de factura");

        if (item == null) {
            exception.addFieldError("item", "El ítem de factura no puede ser nulo");
        } else {
            if (item.getProductId() <= 0) {
                exception.addFieldError("productId", "El ID del producto es inválido");
            }
            if (item.getQuantity() <= 0) {
                exception.addFieldError("quantity", "La cantidad debe ser mayor a cero");
            }
            if (item.getUnitPrice() < 0) {
                exception.addFieldError("unitPrice", "El precio unitario debe ser mayor o igual a cero");
            }
        }

        if (exception.hasErrors()) {
            throw exception;
        }
    }
}