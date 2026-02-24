package com.mycompany.almacen.controller;

import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.model.InvoiceItem;
import com.mycompany.almacen.service.InvoiceService;
import com.mycompany.almacen.exception.AlmacenException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Controlador para la gestión de facturas.
 * Separa la lógica de presentación de la lógica de negocio.
 */
public class InvoiceController {
    private InvoiceService invoiceService;
    private ObservableList<Invoice> invoiceData;
    private ObservableList<InvoiceItem> invoiceItemData;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.invoiceData = FXCollections.observableArrayList();
        this.invoiceItemData = FXCollections.observableArrayList();
    }

    /**
     * Carga todas las facturas en la lista observable.
     */
    public ObservableList<Invoice> loadInvoices() throws AlmacenException {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        invoiceData.setAll(invoices);
        return invoiceData;
    }

    /**
     * Agrega una nueva factura.
     * @return ID de la factura creada
     */
    public int addInvoice(Invoice invoice) throws AlmacenException {
        return invoiceService.addInvoice(invoice);
    }

    /**
     * Agrega un ítem a una factura.
     */
    public void addInvoiceItem(InvoiceItem item) throws AlmacenException {
        invoiceService.addInvoiceItem(item);
    }

    /**
     * Obtiene los ítems de una factura específica.
     */
    public ObservableList<InvoiceItem> loadInvoiceItems(int invoiceId) throws AlmacenException {
        List<InvoiceItem> items = invoiceService.getInvoiceItemsByInvoiceId(invoiceId);
        invoiceItemData.setAll(items);
        return invoiceItemData;
    }

    /**
     * Actualiza el stock de un producto.
     */
    public void updateProductStock(int productId, int quantityChange) throws AlmacenException {
        invoiceService.updateProductStock(productId, quantityChange);
    }

    /**
     * Obtiene la lista observable de facturas.
     */
    public ObservableList<Invoice> getInvoiceData() {
        return invoiceData;
    }

    /**
     * Obtiene la lista observable de ítems de factura.
     */
    public ObservableList<InvoiceItem> getInvoiceItemData() {
        return invoiceItemData;
    }
}
