package org.example.dao;

import org.example.model.Category;
import org.example.util.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategoriesSorted() {
        return searchCategories(null, "name_asc");
    }

    public List<Category> searchCategories(String query, String sortBy) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT category_number, category_name FROM Category");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append(" WHERE category_name LIKE ? OR CAST(category_number AS CHAR) LIKE ?");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY ");
        sql.append("number".equals(sortBy) ? "category_number" : "category_name");
        if ("name_desc".equals(sortBy)) {
            sql.append(" DESC");
        } else {
            sql.append(" ASC");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category c = new Category();
                    c.setCategoryNumber(rs.getInt("category_number"));
                    c.setCategoryName(rs.getString("category_name"));
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Category getById(int id) {
        String sql = "SELECT category_number, category_name FROM Category WHERE category_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Category c = new Category();
                    c.setCategoryNumber(rs.getInt("category_number"));
                    c.setCategoryName(rs.getString("category_name"));
                    return c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertCategory(Category category) {
        String sql = "INSERT INTO Category (category_name) VALUES (?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCategory(Category category) {
        String sql = "UPDATE Category SET category_name = ? WHERE category_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setInt(2, category.getCategoryNumber());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(int id) {
        String sql = "DELETE FROM Category WHERE category_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
