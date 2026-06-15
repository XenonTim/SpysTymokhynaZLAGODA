package org.example.dao;

import org.example.model.Sale;
import org.example.util.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public List<Sale> getAllSalesSorted() {
        return searchSales(null, "check_number");
    }

    public List<Sale> searchSales(String query, String sortBy) {
        List<Sale> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.UPC, s.check_number, s.product_number, s.selling_price, p.product_name " +
                "FROM Sale s JOIN Store_Product sp ON s.UPC = sp.UPC JOIN Product p ON sp.id_product = p.id_product");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append(" WHERE CAST(s.check_number AS CHAR) LIKE ? OR CAST(s.UPC AS CHAR) LIKE ?");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY ");
        switch (sortBy == null ? "" : sortBy) {
            case "product_number_desc" -> sql.append("s.product_number DESC");
            case "price_desc" -> sql.append("s.selling_price DESC");
            default -> sql.append("s.check_number ASC, s.UPC ASC");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapSale(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Sale getById(String checkNumber, String upc) {
        String sql = "SELECT s.UPC, s.check_number, s.product_number, s.selling_price, p.product_name " +
                "FROM Sale s JOIN Store_Product sp ON s.UPC = sp.UPC JOIN Product p ON sp.id_product = p.id_product " +
                "WHERE s.check_number = ? AND s.UPC = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkNumber);
            stmt.setString(2, upc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapSale(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertSale(Sale sale) {
        String sql = "INSERT INTO Sale (UPC, check_number, product_number, selling_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sale.getUpc());
            stmt.setString(2, sale.getCheckNumber());
            stmt.setInt(3, sale.getProductNumber());
            stmt.setBigDecimal(4, sale.getSellingPrice());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSale(Sale sale) {
        String sql = "UPDATE Sale SET product_number = ?, selling_price = ? WHERE check_number = ? AND UPC = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sale.getProductNumber());
            stmt.setBigDecimal(2, sale.getSellingPrice());
            stmt.setString(3, sale.getCheckNumber());
            stmt.setString(4, sale.getUpc());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSale(String checkNumber, String upc) {
        String sql = "DELETE FROM Sale WHERE check_number = ? AND UPC = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkNumber);
            stmt.setString(2, upc);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Sale mapSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setUpc(String.valueOf(rs.getObject("UPC")));
        sale.setCheckNumber(String.valueOf(rs.getObject("check_number")));
        sale.setProductNumber(rs.getInt("product_number"));
        sale.setSellingPrice(rs.getBigDecimal("selling_price"));
        sale.setProductName(rs.getString("product_name"));
        return sale;
    }
}
