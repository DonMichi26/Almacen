package com.mycompany.almacen.service;

import com.mycompany.almacen.dao.ProductDAO;
import com.mycompany.almacen.dao.CategoryDAO;
import com.mycompany.almacen.dao.BrandDAO;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.exception.ValidationException;
import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.model.Category;
import com.mycompany.almacen.model.Brand;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la gestión de productos.
 * Encapsula la lógica de negocio relacionada con productos.
 */
public class ProductService {
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;

    /**
     * Constructor por defecto con inicialización interna de DAOs.
     */
    public ProductService() {
        this(new ProductDAO(), new CategoryDAO(), new BrandDAO());
    }

    /**
     * Constructor con inyección de dependencias para testing.
     */
    public ProductService(ProductDAO productDAO, CategoryDAO categoryDAO, BrandDAO brandDAO) {
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;
        this.brandDAO = brandDAO;
    }

    /**
     * Constructor parcial con solo ProductDAO para compatibilidad.
     */
    public ProductService(ProductDAO productDAO) {
        this(productDAO, new CategoryDAO(), new BrandDAO());
    }

    public void addProduct(Product product) throws AlmacenException {
        try {
            validateProduct(product);
            productDAO.addProduct(product);
        } catch (ValidationException e) {
            throw e;
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
            validateProduct(product);
            productDAO.updateProduct(product);
        } catch (ValidationException e) {
            throw e;
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

    /**
     * Obtiene el nombre de una categoría por su ID.
     * Método utilitario para la UI.
     */
    public String getCategoryName(int categoryId) throws AlmacenException {
        try {
            Category category = categoryDAO.getCategoryById(categoryId);
            return category != null ? category.getName() : "General";
        } catch (SQLException e) {
            return "General";
        }
    }

    /**
     * Obtiene el nombre de una marca por su ID.
     * Método utilitario para la UI.
     */
    public String getBrandName(int brandId) throws AlmacenException {
        try {
            Brand brand = brandDAO.getBrandById(brandId);
            return brand != null ? brand.getName() : "General";
        } catch (SQLException e) {
            return "General";
        }
    }

    private void validateProduct(Product product) throws ValidationException {
        ValidationException exception = new ValidationException("Errores de validación en el producto");

        if (product == null) {
            exception.addFieldError("product", "El producto no puede ser nulo");
        } else {
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                exception.addFieldError("name", "El nombre del producto es obligatorio");
            }
            if (product.getPrice() < 0) {
                exception.addFieldError("price", "El precio debe ser mayor o igual a cero");
            }
            if (product.getStock() < 0) {
                exception.addFieldError("stock", "El stock debe ser mayor o igual a cero");
            }
            if (product.getBrandId() <= 0) {
                exception.addFieldError("brandId", "La marca del producto es obligatoria");
            }
        }

        if (exception.hasErrors()) {
            throw exception;
        }
    }
}