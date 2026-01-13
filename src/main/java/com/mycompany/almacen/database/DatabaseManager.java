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

            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS products (");
            sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append("name TEXT NOT NULL UNIQUE,");
            sb.append("description TEXT,");
            sb.append("price REAL NOT NULL,");
            sb.append("stock INTEGER NOT NULL DEFAULT 0)");
            stmt.execute(sb.toString());

            sb.setLength(0); // Clear StringBuilder
            sb.append("CREATE TABLE IF NOT EXISTS invoices (");
            sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append("invoice_date TEXT NOT NULL,");
            sb.append("customer_name TEXT NOT NULL,");
            sb.append("total_amount REAL NOT NULL,");
            sb.append("invoice_type TEXT NOT NULL DEFAULT 'SALE',");
            sb.append("description TEXT)");
            stmt.execute(sb.toString());

            sb.setLength(0); // Clear StringBuilder
            sb.append("CREATE TABLE IF NOT EXISTS invoice_items (");
            sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append("invoice_id INTEGER NOT NULL,");
            sb.append("product_id INTEGER NOT NULL,");
            sb.append("quantity INTEGER NOT NULL,");
            sb.append("unit_price REAL NOT NULL,");
            sb.append("FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,");
            sb.append("FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)");
            stmt.execute(sb.toString());

            System.out.println("Tables created or already exist.");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void seedDatabaseWithSampleData() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Seeding database with sample data...");
                ProductDAO productDAO = new ProductDAO();
                productDAO.addProduct(new Product(0, "Laptop Gamer ASUS", "Laptop con RTX 4060, 16GB RAM, 1TB SSD", 5500.00, 10));
                productDAO.addProduct(new Product(0, "Mouse Logitech G502", "Mouse ergonómico para juegos con RGB", 250.00, 50));
                productDAO.addProduct(new Product(0, "Teclado Mecánico Redragon", "Teclado compacto con switches rojos", 350.00, 30));
                productDAO.addProduct(new Product(0, "Parlante Bluetooth JBL Flip 6", "Parlante portátil resistente al agua", 480.00, 40));
                productDAO.addProduct(new Product(0, "Funda para Laptop 15.6\"", "Funda de neopreno acolchada", 60.00, 100));
                System.out.println("Sample data seeded.");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
