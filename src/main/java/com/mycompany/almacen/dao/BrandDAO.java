package com.mycompany.almacen.dao;

import com.mycompany.almacen.database.DatabaseManager;
import com.mycompany.almacen.model.Brand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    public void addBrand(Brand brand) throws SQLException {
        String sql = "INSERT INTO brands(name) VALUES(?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brand.getName());
            pstmt.executeUpdate();
        }
    }

    public Brand getBrandById(int id) throws SQLException {
        String sql = "SELECT id, name FROM brands WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Brand(
                    rs.getInt("id"),
                    rs.getString("name")
                );
            }
        }
        return null;
    }

    public List<Brand> getAllBrands() throws SQLException {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT id, name FROM brands ORDER BY name";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                brands.add(new Brand(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        }
        return brands;
    }

    public void updateBrand(Brand brand) throws SQLException {
        String sql = "UPDATE brands SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brand.getName());
            pstmt.setInt(2, brand.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteBrand(int id) throws SQLException {
        String sql = "DELETE FROM brands WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public Brand getBrandByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM brands WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Brand(
                    rs.getInt("id"),
                    rs.getString("name")
                );
            }
        }
        return null;
    }
}