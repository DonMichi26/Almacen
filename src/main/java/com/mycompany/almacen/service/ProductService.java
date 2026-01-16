package com.mycompany.almacen.service;

import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.model.Product;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public void addProduct(Product product) throws AlmacenException {
        try {
            // Validar producto antes de guardarlo
            validateProduct(product);
            productDAO.addProduct(product);
        } catch (SQLException e) {
            throw new AlmacenException("Error al agregar producto: " + e.getMessage(), e);
        }
    }

    public Product getProductById(int id) throws AlmacenException {
        try {
            return productDAO.getProductById(id);
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener producto por ID: " + e.getMessage(), e);
        }
    }

    public List<Product> getAllProducts() throws AlmacenException {
        try {
            return productDAO.getAllProducts();
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener todos los productos: " + e.getMessage(), e);
        }
    }

    public void updateProduct(Product product) throws AlmacenException {
        try {
            // Validar producto antes de actualizarlo
            validateProduct(product);
            productDAO.updateProduct(product);
        } catch (SQLException e) {
            throw new AlmacenException("Error al actualizar producto: " + e.getMessage(), e);
        }
    }

    public void deleteProduct(int id) throws AlmacenException {
        try {
            productDAO.deleteProduct(id);
        } catch (SQLException e) {
            throw new AlmacenException("Error al eliminar producto: " + e.getMessage(), e);
        }
    }

    public List<Product> searchProductsByName(String name) throws AlmacenException {
        try {
            return productDAO.searchProductsByName(name);
        } catch (SQLException e) {
            throw new AlmacenException("Error al buscar productos por nombre: " + e.getMessage(), e);
        }
    }

    private void validateProduct(Product product) throws AlmacenException {
        if (product == null) {
            throw new AlmacenException("El producto no puede ser nulo");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new AlmacenException("El nombre del producto es obligatorio");
        }
        if (product.getPrice() < 0) {
            throw new AlmacenException("El precio del producto debe ser mayor o igual a cero");
        }
        if (product.getStock() < 0) {
            throw new AlmacenException("El stock del producto debe ser mayor o igual a cero");
        }
        if (product.getBrandId() <= 0) {
            throw new AlmacenException("La marca del producto es obligatoria");
        }
    }
}