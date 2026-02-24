package com.mycompany.almacen.service;

import com.mycompany.almacen.dao.CategoryDAO;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.model.Category;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la gestión de categorías.
 * Encapsula la lógica de negocio relacionada con categorías.
 */
public class CategoryService {
    private CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    /**
     * Obtiene inyección de dependencia DAO para testing.
     */
    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public void addCategory(Category category) throws AlmacenException {
        try {
            validateCategory(category);
            categoryDAO.addCategory(category);
        } catch (SQLException e) {
            throw new AlmacenException("Error al agregar categoría: " + e.getMessage(), e);
        }
    }

    public Category getCategoryById(int id) throws AlmacenException {
        try {
            return categoryDAO.getCategoryById(id);
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener categoría por ID: " + e.getMessage(), e);
        }
    }

    public List<Category> getAllCategories() throws AlmacenException {
        try {
            return categoryDAO.getAllCategories();
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener todas las categorías: " + e.getMessage(), e);
        }
    }

    public void updateCategory(Category category) throws AlmacenException {
        try {
            validateCategory(category);
            categoryDAO.updateCategory(category);
        } catch (SQLException e) {
            throw new AlmacenException("Error al actualizar categoría: " + e.getMessage(), e);
        }
    }

    public void deleteCategory(int id) throws AlmacenException {
        try {
            categoryDAO.deleteCategory(id);
        } catch (SQLException e) {
            throw new AlmacenException("Error al eliminar categoría: " + e.getMessage(), e);
        }
    }

    public Category getCategoryByName(String name) throws AlmacenException {
        try {
            return categoryDAO.getCategoryByName(name);
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener categoría por nombre: " + e.getMessage(), e);
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

    private void validateCategory(Category category) throws AlmacenException {
        if (category == null) {
            throw new AlmacenException("La categoría no puede ser nula");
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new AlmacenException("El nombre de la categoría es obligatorio");
        }
    }
}
