package com.mycompany.almacen.database;

import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.model.Product;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:almacen.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if categories table exists, if not create it
            if (!tableExists(conn, "categories")) {
                StringBuilder sb = new StringBuilder();
                sb.append("CREATE TABLE categories (");
                sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
                sb.append("name TEXT NOT NULL UNIQUE,");
                sb.append("description TEXT)");
                stmt.execute(sb.toString());
                System.out.println("Categories table created.");
            }

            // Check if category_id column exists in products table, if not add it
            if (!columnExists(conn, "products", "category_id")) {
                stmt.execute("ALTER TABLE products ADD COLUMN category_id INTEGER DEFAULT 1");
                System.out.println("category_id column added to products table.");
            }

            // Check if invoice_type column exists in invoices table, if not add it
            if (!columnExists(conn, "invoices", "invoice_type")) {
                stmt.execute("ALTER TABLE invoices ADD COLUMN invoice_type TEXT NOT NULL DEFAULT 'SALE'");
                System.out.println("invoice_type column added to invoices table.");
            }

            // Create products table if it doesn't exist (for fresh installations)
            if (!tableExists(conn, "products")) {
                StringBuilder sb = new StringBuilder();
                sb.append("CREATE TABLE products (");
                sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
                sb.append("name TEXT NOT NULL UNIQUE,");
                sb.append("description TEXT,");
                sb.append("price REAL NOT NULL,");
                sb.append("stock INTEGER NOT NULL DEFAULT 0,");
                sb.append("category_id INTEGER DEFAULT 1)");
                stmt.execute(sb.toString());
                System.out.println("Products table created.");
            }

            // Create invoices table if it doesn't exist (for fresh installations)
            if (!tableExists(conn, "invoices")) {
                StringBuilder sb = new StringBuilder();
                sb.append("CREATE TABLE invoices (");
                sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
                sb.append("invoice_date TEXT NOT NULL,");
                sb.append("customer_name TEXT NOT NULL,");
                sb.append("total_amount REAL NOT NULL,");
                sb.append("invoice_type TEXT NOT NULL DEFAULT 'SALE',");
                sb.append("description TEXT)");
                stmt.execute(sb.toString());
                System.out.println("Invoices table created.");
            }

            // Create invoice_items table if it doesn't exist (for fresh installations)
            if (!tableExists(conn, "invoice_items")) {
                StringBuilder sb = new StringBuilder();
                sb.append("CREATE TABLE invoice_items (");
                sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
                sb.append("invoice_id INTEGER NOT NULL,");
                sb.append("product_id INTEGER NOT NULL,");
                sb.append("quantity INTEGER NOT NULL,");
                sb.append("unit_price REAL NOT NULL)");
                stmt.execute(sb.toString());
                System.out.println("Invoice items table created.");
            }

            // Insert default category if none exists
            insertDefaultCategory(stmt);

            System.out.println("Database schema is up to date.");

        } catch (SQLException e) {
            System.err.println("Error creating/updating database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    private static void insertDefaultCategory(Statement stmt) throws SQLException {
        // Check if default category already exists
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE name='General'");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.execute("INSERT INTO categories (name, description) VALUES ('General', 'Categoría por defecto')");
        }
    }

    public static void seedDatabaseWithSampleData() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Seeding database with sample data...");
                ProductDAO productDAO = new ProductDAO();
                productDAO.addProduct(new Product(0, "Laptop Gamer ASUS", "Laptop con RTX 4060, 16GB RAM, 1TB SSD", 5500.00, 10, 1)); // General category
                productDAO.addProduct(new Product(0, "Mouse Logitech G502", "Mouse ergonómico para juegos con RGB", 250.00, 50, 1)); // General category
                productDAO.addProduct(new Product(0, "Teclado Mecánico Redragon", "Teclado compacto con switches rojos", 350.00, 30, 1)); // General category
                productDAO.addProduct(new Product(0, "Parlante Bluetooth JBL Flip 6", "Parlante portátil resistente al agua", 480.00, 40, 1)); // General category
                productDAO.addProduct(new Product(0, "Funda para Laptop 15.6\"", "Funda de neopreno acolchada", 60.00, 100, 1)); // General category
                System.out.println("Sample data seeded.");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
