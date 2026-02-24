package com.mycompany.almacen.controller;

import com.mycompany.almacen.model.Product;
import com.mycompany.almacen.service.ProductService;
import com.mycompany.almacen.exception.AlmacenException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Controlador para la gestión de productos.
 * Separa la lógica de presentación de la lógica de negocio.
 */
public class ProductController {
    private ProductService productService;
    private ObservableList<Product> productData;

    public ProductController(ProductService productService) {
        this.productService = productService;
        this.productData = FXCollections.observableArrayList();
    }

    /**
     * Carga todos los productos en la lista observable.
     */
    public ObservableList<Product> loadProducts() throws AlmacenException {
        List<Product> products = productService.getAllProducts();
        productData.setAll(products);
        return productData;
    }

    /**
     * Agrega un nuevo producto y recarga la lista.
     */
    public void addProduct(Product product) throws AlmacenException {
        productService.addProduct(product);
        loadProducts();
    }

    /**
     * Actualiza un producto existente y recarga la lista.
     */
    public void updateProduct(Product product) throws AlmacenException {
        productService.updateProduct(product);
        loadProducts();
    }

    /**
     * Elimina un producto por ID y recarga la lista.
     */
    public void deleteProduct(int id) throws AlmacenException {
        productService.deleteProduct(id);
        loadProducts();
    }

    /**
     * Busca productos por término de búsqueda.
     */
    public ObservableList<Product> searchProducts(String searchTerm) throws AlmacenException {
        List<Product> results = productService.searchProductsByName(searchTerm);
        productData.setAll(results);
        return productData;
    }

    /**
     * Obtiene la lista observable de productos.
     */
    public ObservableList<Product> getProductData() {
        return productData;
    }

    /**
     * Limpia la selección de productos.
     */
    public void clearSelection() {
        productData.clear();
    }
}
