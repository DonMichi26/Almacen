package com.mycompany.almacen.model;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int categoryId;
    private int brandId;
    private String model;

    public Product(int id, String name, String description, double price, int stock, int categoryId, int brandId, String model) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.model = model;
    }

    // Constructor without model for backward compatibility
    public Product(int id, String name, String description, double price, int stock, int categoryId, int brandId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.model = null;
    }

    // Constructor without brandId for backward compatibility
    public Product(int id, String name, String description, double price, int stock, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.brandId = 1; // Default to General brand
        this.model = null;
    }

    // Constructor without category for backward compatibility
    public Product(int id, String name, String description, double price, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = 1; // Default to General category
        this.brandId = 1; // Default to General brand
        this.model = null;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Product{" +
               "id=" + id + ", " +
               "name='" + name + "', " +
               "description='" + description + "', " +
               "price=" + price + ", " +
               "stock=" + stock + ", " +
               "categoryId=" + categoryId + ", " +
               "brandId=" + brandId + ", " +
               "model='" + model + "'" +
               '}';
    }
}
