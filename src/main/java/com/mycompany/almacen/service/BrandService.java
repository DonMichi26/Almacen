package com.mycompany.almacen.service;

import com.mycompany.almacen.dao.BrandDAO;
import com.mycompany.almacen.exception.AlmacenException;
import com.mycompany.almacen.model.Brand;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la gestión de marcas.
 * Encapsula la lógica de negocio relacionada con marcas.
 */
public class BrandService {
    private BrandDAO brandDAO;

    public BrandService() {
        this.brandDAO = new BrandDAO();
    }

    /**
     * Obtiene inyección de dependencia DAO para testing.
     */
    public BrandService(BrandDAO brandDAO) {
        this.brandDAO = brandDAO;
    }

    public void addBrand(Brand brand) throws AlmacenException {
        try {
            validateBrand(brand);
            brandDAO.addBrand(brand);
        } catch (SQLException e) {
            throw new AlmacenException("Error al agregar marca: " + e.getMessage(), e);
        }
    }

    public Brand getBrandById(int id) throws AlmacenException {
        try {
            return brandDAO.getBrandById(id);
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener marca por ID: " + e.getMessage(), e);
        }
    }

    public List<Brand> getAllBrands() throws AlmacenException {
        try {
            return brandDAO.getAllBrands();
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener todas las marcas: " + e.getMessage(), e);
        }
    }

    public void updateBrand(Brand brand) throws AlmacenException {
        try {
            validateBrand(brand);
            brandDAO.updateBrand(brand);
        } catch (SQLException e) {
            throw new AlmacenException("Error al actualizar marca: " + e.getMessage(), e);
        }
    }

    public void deleteBrand(int id) throws AlmacenException {
        try {
            brandDAO.deleteBrand(id);
        } catch (SQLException e) {
            throw new AlmacenException("Error al eliminar marca: " + e.getMessage(), e);
        }
    }

    public Brand getBrandByName(String name) throws AlmacenException {
        try {
            return brandDAO.getBrandByName(name);
        } catch (SQLException e) {
            throw new AlmacenException("Error al obtener marca por nombre: " + e.getMessage(), e);
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

    private void validateBrand(Brand brand) throws AlmacenException {
        if (brand == null) {
            throw new AlmacenException("La marca no puede ser nula");
        }
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            throw new AlmacenException("El nombre de la marca es obligatorio");
        }
    }
}
