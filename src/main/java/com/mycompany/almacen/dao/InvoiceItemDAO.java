package com.mycompany.almacen.dao;

import com.mycompany.almacen.database.DatabaseManager;
import com.mycompany.almacen.model.InvoiceItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO {

    public void addInvoiceItem(InvoiceItem item) throws SQLException {
        String sql = "INSERT INTO invoice_items(invoice_id, product_id, quantity, unit_price) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, item.getInvoiceId());
            pstmt.setInt(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getUnitPrice());
            pstmt.executeUpdate();
        }
    }

    public List<InvoiceItem> getInvoiceItemsByInvoiceId(int invoiceId) throws SQLException {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT ii.id, ii.invoice_id, ii.product_id, ii.quantity, ii.unit_price, p.name as product_name " +
                "FROM invoice_items ii JOIN products p ON ii.product_id = p.id WHERE ii.invoice_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(new InvoiceItem(
                        rs.getInt("id"),
                        rs.getInt("invoice_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")));
            }
        }
        return items;
    }

    public void deleteInvoiceItemsByInvoiceId(int invoiceId) throws SQLException {
        String sql = "DELETE FROM invoice_items WHERE invoice_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            pstmt.executeUpdate();
        }
    }

    // You might want update methods for individual invoice items, but for now,
    // it's common to delete and re-add for simplicity in invoice management.
}
