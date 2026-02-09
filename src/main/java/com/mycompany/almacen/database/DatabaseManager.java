package com.mycompany.almacen.database;

import com.mycompany.almacen.dao.BrandDAO;
import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.model.Brand;
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

                // Insert default categories
                stmt.execute("INSERT INTO categories (name, description) VALUES ('Celulares', 'Dispositivos móviles')");
                stmt.execute("INSERT INTO categories (name, description) VALUES ('Parlantes', 'Altavoces y sistemas de audio')");
                stmt.execute("INSERT INTO categories (name, description) VALUES ('Accesorios', 'Accesorios para dispositivos')");
                stmt.execute("INSERT INTO categories (name, description) VALUES ('Tablets', 'Dispositivos tablet')");
                stmt.execute("INSERT INTO categories (name, description) VALUES ('Computadoras', 'Laptops y computadoras personales')");
            } else {
                // Check if default categories exist, if not add them
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE name='Celulares'");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO categories (name, description) VALUES ('Celulares', 'Dispositivos móviles')");
                }
                rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE name='Parlantes'");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO categories (name, description) VALUES ('Parlantes', 'Altavoces y sistemas de audio')");
                }
                rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE name='Accesorios'");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO categories (name, description) VALUES ('Accesorios', 'Accesorios para dispositivos')");
                }
                rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE name='Tablets'");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO categories (name, description) VALUES ('Tablets', 'Dispositivos tablet')");
                }
                rs = stmt.executeQuery("SELECT COUNT(*) FROM categories WHERE name='Computadoras'");
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO categories (name, description) VALUES ('Computadoras', 'Laptops y computadoras personales')");
                }
            }

            // Check if category_id column exists in products table, if not add it
            if (!columnExists(conn, "products", "category_id")) {
                stmt.execute("ALTER TABLE products ADD COLUMN category_id INTEGER DEFAULT 1");
                System.out.println("category_id column added to products table.");
            } else {
                // Update existing products to have a valid category_id if they have NULL
                stmt.execute("UPDATE products SET category_id = 1 WHERE category_id IS NULL OR category_id = ''");
            }

            // Check if invoice_type column exists in invoices table, if not add it
            if (!columnExists(conn, "invoices", "invoice_type")) {
                stmt.execute("ALTER TABLE invoices ADD COLUMN invoice_type TEXT NOT NULL DEFAULT 'SALE'");
                System.out.println("invoice_type column added to invoices table.");
            }

            // Check if description column exists in invoices table, if not add it
            if (!columnExists(conn, "invoices", "description")) {
                stmt.execute("ALTER TABLE invoices ADD COLUMN description TEXT");
                System.out.println("description column added to invoices table.");
            }

            // Check if brand_id column exists in products table, if not add it
            if (!columnExists(conn, "products", "brand_id")) {
                stmt.execute("ALTER TABLE products ADD COLUMN brand_id INTEGER DEFAULT 1");
                System.out.println("brand_id column added to products table.");
            }

            // Check if model column exists in products table, if not add it
            if (!columnExists(conn, "products", "model")) {
                stmt.execute("ALTER TABLE products ADD COLUMN model TEXT");
                System.out.println("model column added to products table.");
            }

            // Create brands table if it doesn't exist
            if (!tableExists(conn, "brands")) {
                StringBuilder sb = new StringBuilder();
                sb.append("CREATE TABLE brands (");
                sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT,");
                sb.append("name TEXT NOT NULL UNIQUE)");
                stmt.execute(sb.toString());
                System.out.println("Brands table created.");

                // Insert default brands
                stmt.execute("INSERT INTO brands (name) VALUES ('General')");
                stmt.execute("INSERT INTO brands (name) VALUES ('Apple')");
                stmt.execute("INSERT INTO brands (name) VALUES ('Samsung')");
                stmt.execute("INSERT INTO brands (name) VALUES ('Honor')");
                stmt.execute("INSERT INTO brands (name) VALUES ('Xiaomi')");
                stmt.execute("INSERT INTO brands (name) VALUES ('Motorola')");
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
                sb.append("category_id INTEGER DEFAULT 1,");
                sb.append("brand_id INTEGER DEFAULT 1,");
                sb.append("model TEXT)");
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
                BrandDAO brandDAO = new BrandDAO();

                // Get brand IDs
                Brand asusBrand = brandDAO.getBrandByName("Apple");
                Brand logitechBrand = brandDAO.getBrandByName("Samsung");
                Brand redragonBrand = brandDAO.getBrandByName("Honor");
                Brand jblBrand = brandDAO.getBrandByName("Xiaomi");
                Brand genericBrand = brandDAO.getBrandByName("General");

                // If brands don't exist, use General (ID 1)
                int asusId = asusBrand != null ? asusBrand.getId() : 1;
                int logitechId = logitechBrand != null ? logitechBrand.getId() : 1;
                int redragonId = redragonBrand != null ? redragonBrand.getId() : 1;
                int jblId = jblBrand != null ? jblBrand.getId() : 1;
                int genericId = genericBrand != null ? genericBrand.getId() : 1;

                productDAO.addProduct(new Product(0, "iPhone 15 Pro", "Smartphone con chip A17 Pro y cámara de 48MP", 4500.00, 15, 1, asusId)); // General category
                productDAO.addProduct(new Product(0, "Samsung Galaxy S24", "Smartphone con IA integrada y pantalla Dynamic AMOLED", 3800.00, 20, 1, logitechId)); // General category
                productDAO.addProduct(new Product(0, "Honor Magic 6 Pro", "Smartphone con cámara de 50MP y carga rápida de 66W", 2800.00, 25, 1, redragonId)); // General category
                productDAO.addProduct(new Product(0, "Xiaomi 14 Ultra", "Smartphone con cámara Leica y cuerpo de titanio", 3200.00, 18, 1, jblId)); // General category
                productDAO.addProduct(new Product(0, "Funda iPhone 15 Pro", "Funda protectora para iPhone 15 Pro", 80.00, 100, 1, genericId)); // General category
                System.out.println("Sample data seeded.");
            }
        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
