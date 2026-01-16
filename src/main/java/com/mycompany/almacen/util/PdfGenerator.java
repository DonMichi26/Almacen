package com.mycompany.almacen.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.mycompany.almacen.exception.SecurityException;
import com.mycompany.almacen.exception.ValidationException;
import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.model.InvoiceItem;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfGenerator {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static void generateInvoicePdf(Invoice invoice, List<InvoiceItem> items, String dest) throws FileNotFoundException, SecurityException, ValidationException {
        // Validar la ruta del archivo para evitar accesos no deseados
        if (!ValidationUtils.isValidPath(dest, System.getProperty("user.home"))) {
            throw new SecurityException("Ruta de archivo no permitida: " + dest);
        }

        // Validar extensión del archivo
        if (!ValidationUtils.hasValidExtension(dest, "pdf")) {
            throw new ValidationException("Extensión de archivo no válida. Solo se permiten archivos PDF.");
        }

        // Sanitizar el nombre del archivo
        String sanitizedDest = ValidationUtils.sanitizeFileName(dest);

        PdfWriter writer = new PdfWriter(sanitizedDest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("Factura de Almacén")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20));
        document.add(new Paragraph("Fecha: " + DATE_FORMAT.format(new Date()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // Invoice Details
        document.add(new Paragraph("Número de Factura: " + invoice.getId()));
        document.add(new Paragraph("Cliente: " + invoice.getCustomerName()));
        document.add(new Paragraph("Fecha de Compra: " + DATE_FORMAT.format(invoice.getInvoiceDate())));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // Items Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(new Paragraph("ID Producto").setBold());
        table.addHeaderCell(new Paragraph("Descripción").setBold());
        table.addHeaderCell(new Paragraph("Cantidad").setBold());
        table.addHeaderCell(new Paragraph("Precio Unitario").setBold());
        table.addHeaderCell(new Paragraph("Total").setBold());

        for (InvoiceItem item : items) {
            table.addCell(new Paragraph(String.valueOf(item.getProductId())));
            table.addCell(new Paragraph(item.getProductName()));
            table.addCell(new Paragraph(String.valueOf(item.getQuantity())));
            table.addCell(new Paragraph("S/ " + String.format("%.2f", item.getUnitPrice())));
            table.addCell(new Paragraph("S/ " + String.format("%.2f", item.getQuantity() * item.getUnitPrice())));
        }
        document.add(table);
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // Total Amount
        document.add(new Paragraph("Total a Pagar: S/ " + String.format("%.2f", invoice.getTotalAmount()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(14)
                .setBold());

        document.close();
        System.out.println("Invoice PDF generated: " + sanitizedDest);
    }

    public static void generateServiceInvoicePdf(Invoice invoice, String dest) throws FileNotFoundException, SecurityException, ValidationException {
        // Validar la ruta del archivo para evitar accesos no deseados
        if (!ValidationUtils.isValidPath(dest, System.getProperty("user.home"))) {
            throw new SecurityException("Ruta de archivo no permitida: " + dest);
        }

        // Validar extensión del archivo
        if (!ValidationUtils.hasValidExtension(dest, "pdf")) {
            throw new ValidationException("Extensión de archivo no válida. Solo se permiten archivos PDF.");
        }

        // Sanitizar el nombre del archivo
        String sanitizedDest = ValidationUtils.sanitizeFileName(dest);

        PdfWriter writer = new PdfWriter(sanitizedDest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("Recibo de Servicio")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20));
        document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // Invoice Details
        document.add(new Paragraph("Número de Recibo: " + invoice.getId()));
        document.add(new Paragraph("Cliente: " + invoice.getCustomerName()));
        document.add(new Paragraph("Fecha de Servicio: " + new SimpleDateFormat("dd/MM/yyyy").format(invoice.getInvoiceDate())));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // Service Description
        document.add(new Paragraph("Descripción del Servicio:")
                .setBold()
                .setFontSize(14));
        document.add(new Paragraph(invoice.getDescription())
                .setMultipliedLeading(1.5f) // Better line spacing for paragraphs
                .setPadding(10));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

        // Total Amount
        document.add(new Paragraph("Total a Pagar: S/ " + String.format("%.2f", invoice.getTotalAmount()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(16)
                .setBold());

        document.close();
        System.out.println("Service Receipt PDF generated: " + sanitizedDest);
    }
}
