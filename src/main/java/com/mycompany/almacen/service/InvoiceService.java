package com.mycompany.almacen.service;

import com.mycompany.almacen.dao.InvoiceDAO;
import com.mycompany.almacen.dao.InvoiceItemDAO;
import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.model.InvoiceItem;


import java.sql.SQLException;
import java.util.List;

public class InvoiceService {
    private InvoiceDAO invoiceDAO;
    private InvoiceItemDAO invoiceItemDAO;
    private ProductDAO productDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
        this.invoiceItemDAO = new InvoiceItemDAO();
        this.productDAO = new ProductDAO();
    }

    public int addInvoice(Invoice invoice) throws AlmacenException {
        try {
            validateInvoice(invoice);
            return invoiceDAO.addInvoice(invoice);
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

    private void validateInvoice(Invoice invoice) throws AlmacenException {
        if (invoice == null) {
            throw new AlmacenException("La factura no puede ser nula");
        }
        if (invoice.getCustomerName() == null || invoice.getCustomerName().trim().isEmpty()) {
            throw new AlmacenException("El nombre del cliente es obligatorio");
        }
        if (invoice.getTotalAmount() < 0) {
            throw new AlmacenException("El monto total de la factura debe ser mayor o igual a cero");
        }
    }

    private void validateInvoiceItem(InvoiceItem item) throws AlmacenException {
        if (item == null) {
            throw new AlmacenException("El ítem de factura no puede ser nulo");
        }
        if (item.getProductId() <= 0) {
            throw new AlmacenException("El ID del producto en el ítem de factura es inválido");
        }
        if (item.getQuantity() <= 0) {
            throw new AlmacenException("La cantidad del ítem de factura debe ser mayor a cero");
        }
        if (item.getUnitPrice() < 0) {
            throw new AlmacenException("El precio unitario del ítem de factura debe ser mayor o igual a cero");
        }
    }
}