package org.example.dao;

import org.example.model.Product;
import org.example.util.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProductsSorted() {
        return searchProducts(null, "name_asc");
    }

    public List<Product> searchProducts(String query, String sortBy) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.id_product, p.category_number, c.category_name, p.product_name, p.characteristics " +
                        "FROM Product p JOIN Category c ON p.category_number = c.category_number");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append(" WHERE p.product_name LIKE ? OR CAST(p.id_product AS CHAR) LIKE ?");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY ");
        switch (sortBy == null ? "" : sortBy) {
            case "name_desc" -> sql.append("p.product_name DESC");
            case "id"        -> sql.append("p.id_product ASC");
            default          -> sql.append("p.product_name ASC");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Пошук товарів певної категорії, відсортованих за назвою (вимога п.13, п.5 касира) */
    public List<Product> getProductsByCategory(int categoryNum) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id_product, p.category_number, c.category_name, p.product_name, p.characteristics " +
                "FROM Product p JOIN Category c ON p.category_number = c.category_number " +
                "WHERE p.category_number = ? ORDER BY p.product_name ASC";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryNum);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Product getById(int id) {
        String sql = "SELECT p.id_product, p.category_number, c.category_name, p.product_name, p.characteristics " +
                "FROM Product p JOIN Category c ON p.category_number = c.category_number WHERE p.id_product = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapProduct(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertProduct(Product p) {
        String sql = "INSERT INTO Product (category_number, product_name, characteristics) VALUES (?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getCategoryNumber());
            stmt.setString(2, p.getProductName());
            stmt.setString(3, p.getCharacteristics());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product p) {
        String sql = "UPDATE Product SET category_number = ?, product_name = ?, characteristics = ? WHERE id_product = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getCategoryNumber());
            stmt.setString(2, p.getProductName());
            stmt.setString(3, p.getCharacteristics());
            stmt.setInt(4, p.getIdProduct());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProduct(int id) {
        String sql = "DELETE FROM Product WHERE id_product = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setIdProduct(rs.getInt("id_product"));
        p.setCategoryNumber(rs.getInt("category_number"));
        p.setCategoryName(rs.getString("category_name"));
        p.setProductName(rs.getString("product_name"));
        p.setCharacteristics(rs.getString("characteristics"));
        return p;
    }
}