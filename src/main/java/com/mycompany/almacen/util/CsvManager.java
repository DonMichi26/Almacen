package com.mycompany.almacen.util;

import com.mycompany.almacen.dao.CategoryDAO;
import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.model.Category;
import com.mycompany.almacen.model.Product;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CsvManager {

    public static void exportProductsToCsv(List<Product> products, String filePath) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            pw.println("id,name,description,price,stock,category_id,category_name");
            
            // Write products
            for (Product product : products) {
                pw.println(String.format("%d,%s,%s,%.2f,%d,%d,%s",
                    product.getId(),
                    escapeCsvField(product.getName()),
                    escapeCsvField(product.getDescription()),
                    product.getPrice(),
                    product.getStock(),
                    product.getCategoryId(),
                    getCategoryNameById(product.getCategoryId())
                ));
            }
        }
    }

    public static List<Product> importProductsFromCsv(String filePath) throws IOException, SQLException {
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
                    int categoryId = Integer.parseInt(fields[5]);
                    
                    // If category name is provided in the 7th field, try to match or create it
                    if (fields.length >= 7) {
                        String categoryName = fields[6];
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
                    
                    Product product = new Product(id, name, description, price, stock, categoryId);
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
}