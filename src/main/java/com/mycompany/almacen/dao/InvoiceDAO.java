package com.mycompany.almacen.dao;

import com.mycompany.almacen.database.DatabaseManager;
import com.mycompany.almacen.model.Invoice;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

public class InvoiceDAO {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public int addInvoice(Invoice invoice) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            return addInvoice(conn, invoice);
        }
    }

    public int addInvoice(Connection conn, Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices(invoice_date, customer_name, total_amount, invoice_type, description) VALUES(?,?,?,?,?)";
        int invoiceId = -1;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, DATE_FORMAT.format(invoice.getInvoiceDate()));
            pstmt.setString(2, invoice.getCustomerName());
            pstmt.setDouble(3, invoice.getTotalAmount());
            pstmt.setString(4, invoice.getInvoiceType() != null ? invoice.getInvoiceType().toString() : "SALE");
            pstmt.setString(5, invoice.getDescription());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoiceId = generatedKeys.getInt(1);
                }
            }
        }
        return invoiceId;
    }

    public Invoice getInvoiceById(int id) throws SQLException {
        String sql = "SELECT id, invoice_date, customer_name, total_amount, invoice_type, description FROM invoices WHERE id = ?";
        Invoice invoice = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Date invoiceDate = DATE_FORMAT.parse(rs.getString("invoice_date"));
                invoice = new Invoice(
                    rs.getInt("id"),
                    invoiceDate,
                    rs.getString("customer_name"),
                    rs.getDouble("total_amount"),
                    Invoice.InvoiceType.valueOf(rs.getString("invoice_type")),
                    rs.getString("description")
                );
            }
        } catch (java.text.ParseException e) {
            throw new SQLException("Error parsing date: " + e.getMessage());
        }
        return invoice;
    }

    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT id, invoice_date, customer_name, total_amount, invoice_type, description FROM invoices ORDER BY invoice_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Date invoiceDate = DATE_FORMAT.parse(rs.getString("invoice_date"));
                invoices.add(new Invoice(
                    rs.getInt("id"),
                    invoiceDate,
                    rs.getString("customer_name"),
                    rs.getDouble("total_amount"),
                    Invoice.InvoiceType.valueOf(rs.getString("invoice_type")),
                    rs.getString("description")
                ));
            }
        } catch (java.text.ParseException e) {
            throw new SQLException("Error parsing date: " + e.getMessage());
        }
        return invoices;
    }

    public void updateInvoice(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET invoice_date = ?, customer_name = ?, total_amount = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, DATE_FORMAT.format(invoice.getInvoiceDate()));
            pstmt.setString(2, invoice.getCustomerName());
            pstmt.setDouble(3, invoice.getTotalAmount());
            pstmt.setInt(4, invoice.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteInvoice(int id) throws SQLException {
        String sql = "DELETE FROM invoices WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
