package com.mycompany.almacen.util;

import com.mycompany.almacen.dao.BrandDAO;
import com.mycompany.almacen.dao.CategoryDAO;
import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.exception.SecurityException;
import com.mycompany.almacen.exception.ValidationException;
import com.mycompany.almacen.model.Brand;
import com.mycompany.almacen.model.Category;
import com.mycompany.almacen.model.Product;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsvManager {

    public static void exportProductsToCsv(List<Product> products, String filePath) throws IOException, SecurityException, ValidationException {
        // Validar la ruta del archivo para evitar accesos no deseados
        if (!ValidationUtils.isValidPath(filePath, System.getProperty("user.home"))) {
            throw new SecurityException("Ruta de archivo no permitida: " + filePath);
        }

        // Validar extensión del archivo
        if (!ValidationUtils.hasValidExtension(filePath, "csv")) {
            throw new ValidationException("Extensión de archivo no válida. Solo se permiten archivos CSV.");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            pw.println("id,name,description,price,stock,model,brand_id,brand_name,category_id,category_name");

            // Write products
            for (Product product : products) {
                pw.println(String.format("%d,%s,%s,%.2f,%d,%s,%d,%s,%d,%s",
                    product.getId(),
                    escapeCsvField(product.getName()),
                    escapeCsvField(product.getDescription()),
                    product.getPrice(),
                    product.getStock(),
                    escapeCsvField(product.getModel()),
                    product.getBrandId(),
                    getBrandNameById(product.getBrandId()),
                    product.getCategoryId(),
                    getCategoryNameById(product.getCategoryId())
                ));
            }
        }
    }

    public static List<Product> importProductsFromCsv(String filePath) throws IOException, SQLException, SecurityException, ValidationException {
        // Validar la ruta del archivo para evitar accesos no deseados
        if (!ValidationUtils.isValidPath(filePath, System.getProperty("user.home"))) {
            throw new SecurityException("Ruta de archivo no permitida: " + filePath);
        }

        // Validar extensión del archivo
        if (!ValidationUtils.hasValidExtension(filePath, "csv")) {
            throw new ValidationException("Extensión de archivo no válida. Solo se permiten archivos CSV.");
        }

        List<Product> products = new ArrayList<>();
        CategoryDAO categoryDAO = new CategoryDAO();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }

                String[] fields = parseCsvLine(line);
                if (fields.length >= 6) { // Minimum required fields
                    int id = Integer.parseInt(fields[0]);
                    String name = fields[1];
                    String description = fields[2];
                    double price = Double.parseDouble(fields[3]);
                    int stock = Integer.parseInt(fields[4]);
                    String model = fields.length > 5 ? fields[5] : null;
                    int brandId = fields.length > 6 ? Integer.parseInt(fields[6]) : 1; // Default to General brand
                    String brandName = fields.length > 7 ? fields[7] : null;
                    int categoryId = fields.length > 8 ? Integer.parseInt(fields[8]) : 1; // Default to General category
                    String categoryName = fields.length > 9 ? fields[9] : null;

                    // If brand name is provided and brandId is not set, try to match or create it
                    if (brandName != null && brandId == 1) {
                        Brand brand = new BrandDAO().getBrandByName(brandName);
                        if (brand != null) {
                            brandId = brand.getId();
                        } else {
                            // Create new brand if it doesn't exist
                            Brand newBrand = new Brand(0, brandName);
                            new BrandDAO().addBrand(newBrand);
                            // Get the newly created brand to get its ID
                            brand = new BrandDAO().getBrandByName(brandName);
                            if (brand != null) {
                                brandId = brand.getId();
                            }
                        }
                    }

                    // If category name is provided and categoryId is not set, try to match or create it
                    if (categoryName != null && categoryId == 1) {
                        Category category = categoryDAO.getCategoryByName(categoryName);
                        if (category != null) {
                            categoryId = category.getId();
                        } else {
                            // Create new category if it doesn't exist
                            Category newCategory = new Category(0, categoryName, "Categoría importada");
                            categoryDAO.addCategory(newCategory);
                            // Get the newly created category to get its ID
                            category = categoryDAO.getCategoryByName(categoryName);
                            if (category != null) {
                                categoryId = category.getId();
                            }
                        }
                    }

                    Product product = new Product(id, name, description, price, stock, categoryId, brandId);
                    products.add(product);
                }
            }
        }

        return products;
    }

    private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // Escape quotes by doubling them and wrap in quotes if field contains commas, quotes, or newlines
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Doubled quote inside quoted field
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    insideQuotes = !insideQuotes;
                }
            } else if (c == ',' && !insideQuotes) {
                // End of field
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }

        // Add last field
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }

    private static String getCategoryNameById(int categoryId) {
        try {
            CategoryDAO categoryDAO = new CategoryDAO();
            Category category = categoryDAO.getCategoryById(categoryId);
            return category != null ? category.getName() : "General";
        } catch (SQLException e) {
            e.printStackTrace();
            return "General"; // Default fallback
        }
    }

    private static String getBrandNameById(int brandId) {
        try {
            BrandDAO brandDAO = new BrandDAO();
            Brand brand = brandDAO.getBrandById(brandId);
            return brand != null ? brand.getName() : "General";
        } catch (SQLException e) {
            e.printStackTrace();
            return "General"; // Default fallback
        }
    }
}