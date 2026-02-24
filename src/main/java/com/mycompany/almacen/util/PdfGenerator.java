package com.mycompany.almacen.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.mycompany.almacen.model.Invoice;
import com.mycompany.almacen.model.InvoiceItem;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfGenerator {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(79, 70, 229);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(249, 250, 251);
    private static final DeviceRgb DARK_TEXT = new DeviceRgb(31, 41, 55);

    public static void generateInvoicePdf(Invoice invoice, List<InvoiceItem> items, String dest) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("FACTURA DE VENTA")
                .setFontSize(20)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER));
        
        document.add(new Paragraph("Fecha: " + DATE_FORMAT.format(new Date()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));
        
        document.add(new Paragraph("--------------------------------------------------"));
        
        // Invoice Details
        document.add(new Paragraph("Numero de Factura: " + invoice.getId()));
        document.add(new Paragraph("Cliente: " + (invoice.getCustomerName() != null ? invoice.getCustomerName() : "General")));
        document.add(new Paragraph("Fecha de Compra: " + DATE_FORMAT.format(invoice.getInvoiceDate())));
        document.add(new Paragraph("--------------------------------------------------"));

        // Items Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(new Paragraph("ID").setBold());
        table.addHeaderCell(new Paragraph("Producto").setBold());
        table.addHeaderCell(new Paragraph("Cant").setBold());
        table.addHeaderCell(new Paragraph("Precio").setBold());
        table.addHeaderCell(new Paragraph("Total").setBold());

        for (InvoiceItem item : items) {
            table.addCell(new Paragraph(String.valueOf(item.getProductId())));
            table.addCell(new Paragraph(item.getProductName() != null ? item.getProductName() : "Producto"));
            table.addCell(new Paragraph(String.valueOf(item.getQuantity())));
            table.addCell(new Paragraph("$ " + String.format("%.2f", item.getUnitPrice())));
            table.addCell(new Paragraph("$ " + String.format("%.2f", item.getQuantity() * item.getUnitPrice())));
        }
        
        document.add(table);
        document.add(new Paragraph("--------------------------------------------------"));

        // Total Amount
        document.add(new Paragraph("Total a Pagar: $ " + String.format("%.2f", invoice.getTotalAmount()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR));

        document.close();
        System.out.println("Invoice PDF generated: " + dest);
    }

    public static void generateServiceInvoicePdf(Invoice invoice, String dest) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Title
        document.add(new Paragraph("RECIBO DE SERVICIO")
                .setFontSize(20)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER));
        
        document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));
        
        document.add(new Paragraph("--------------------------------------------------"));
        
        // Invoice Details
        document.add(new Paragraph("Numero de Recibo: " + invoice.getId()));
        document.add(new Paragraph("Cliente: " + (invoice.getCustomerName() != null ? invoice.getCustomerName() : "General")));
        document.add(new Paragraph("Fecha de Servicio: " + new SimpleDateFormat("dd/MM/yyyy").format(invoice.getInvoiceDate())));
        document.add(new Paragraph("--------------------------------------------------"));

        // Service Description
        document.add(new Paragraph("Descripcion del Servicio:")
                .setBold()
                .setFontSize(14));
        document.add(new Paragraph(invoice.getDescription() != null ? invoice.getDescription() : "Sin descripcion")
                .setMultipliedLeading(1.5f)
                .setPadding(10));
        document.add(new Paragraph("--------------------------------------------------"));

        // Total Amount
        document.add(new Paragraph("Total a Pagar: $ " + String.format("%.2f", invoice.getTotalAmount()))
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(16)
                .setBold()
                .setFontColor(PRIMARY_COLOR));

        document.close();
        System.out.println("Service Receipt PDF generated: " + dest);
    }
}
