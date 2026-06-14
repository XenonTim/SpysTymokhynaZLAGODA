package org.example.dao;

import org.example.model.StoreProduct;
import org.example.util.DBManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreProductDAO {

    public List<StoreProduct> getAllStoreProductsSortedByQty() {
        return searchStoreProducts(null, null, "number_desc");
    }

    public List<StoreProduct> searchStoreProducts(String query, String sortBy) {
        return searchStoreProducts(query, null, sortBy);
    }

    /**
     * Пошук товарів з необов'язковим фільтром isPromotional.
     * Сортування: за кількістю АБО за назвою — відповідно до вимог п.15, п.16 менеджера та п.12, п.13 касира.
     *
     * @param promotionalFilter null — всі; true — лише акційні; false — лише неакційні
     */
    public List<StoreProduct> searchStoreProducts(String query, Boolean promotionalFilter, String sortBy) {
        List<StoreProduct> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT sp.UPC, sp.UPC_prom, sp.id_product, p.product_name, sp.selling_price, sp.products_number, sp.promotional_product " +
                        "FROM Store_Product sp JOIN Product p ON sp.id_product = p.id_product");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            conditions.add("(CAST(sp.UPC AS CHAR) LIKE ? OR CAST(sp.id_product AS CHAR) LIKE ? OR p.product_name LIKE ?)");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (promotionalFilter != null) {
            conditions.add("sp.promotional_product = ?");
            params.add(promotionalFilter);
        }
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY ");
        switch (sortBy == null ? "" : sortBy) {
            case "name_asc"    -> sql.append("p.product_name ASC");
            case "name_desc"   -> sql.append("p.product_name DESC");
            case "price_asc"   -> sql.append("sp.selling_price ASC");
            case "number_asc"  -> sql.append("sp.products_number ASC");
            case "upc"         -> sql.append("sp.UPC ASC");
            default            -> sql.append("sp.products_number DESC"); // number_desc
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapStoreProduct(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * За UPC повертає ціну продажу, кількість, назву та характеристики товару (вимога п.14 менеджера та п.14 касира).
     */
    public StoreProduct getByUpc(String upc) {
        String sql = "SELECT sp.UPC, sp.UPC_prom, sp.id_product, p.product_name, p.characteristics, " +
                "sp.selling_price, sp.products_number, sp.promotional_product " +
                "FROM Store_Product sp JOIN Product p ON sp.id_product = p.id_product WHERE sp.UPC = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, upc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    StoreProduct sp = mapStoreProduct(rs);
                    // characteristics — зберігаємо в окреме поле ProductName-розширення через toString,
                    // або використаємо productCharacteristics поле моделі якщо є.
                    // Якщо немає — додаємо характеристики до productName для відображення
                    String chars = rs.getString("characteristics");
                    if (chars != null && !chars.isBlank()) {
                        sp.setProductName(sp.getProductName() + " [" + chars + "]");
                    }
                    return sp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertStoreProduct(StoreProduct sp) {
        String sql = "INSERT INTO Store_Product (UPC_prom, id_product, selling_price, products_number, promotional_product) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUpcProm(stmt, 1, sp.getUpcProm());
            stmt.setInt(2, sp.getIdProduct());
            stmt.setBigDecimal(3, sp.getSellingPrice() == null ? BigDecimal.ZERO : sp.getSellingPrice());
            stmt.setInt(4, sp.getProductsNumber());
            stmt.setBoolean(5, sp.isPromotionalProduct());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next() && (sp.getUpc() == null || sp.getUpc().isBlank())) {
                    sp.setUpc(String.valueOf(keys.getInt(1)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStoreProduct(StoreProduct sp) {
        String sql = "UPDATE Store_Product SET UPC_prom = ?, id_product = ?, selling_price = ?, products_number = ?, promotional_product = ? WHERE UPC = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setUpcProm(stmt, 1, sp.getUpcProm());
            stmt.setInt(2, sp.getIdProduct());
            stmt.setBigDecimal(3, sp.getSellingPrice());
            stmt.setInt(4, sp.getProductsNumber());
            stmt.setBoolean(5, sp.isPromotionalProduct());
            stmt.setString(6, sp.getUpc());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStoreProduct(String upc) {
        String sql = "DELETE FROM Store_Product WHERE UPC = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, upc);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpcProm(PreparedStatement stmt, int idx, String upcProm) throws SQLException {
        if (upcProm == null || upcProm.isBlank()) {
            stmt.setNull(idx, Types.INTEGER);
        } else {
            stmt.setString(idx, upcProm);
        }
    }

    private StoreProduct mapStoreProduct(ResultSet rs) throws SQLException {
        StoreProduct sp = new StoreProduct();
        sp.setUpc(String.valueOf(rs.getObject("UPC")));
        Object upcProm = rs.getObject("UPC_prom");
        sp.setUpcProm(upcProm == null ? null : String.valueOf(upcProm));
        sp.setIdProduct(rs.getInt("id_product"));
        sp.setProductName(rs.getString("product_name"));
        sp.setSellingPrice(rs.getBigDecimal("selling_price"));
        sp.setProductsNumber(rs.getInt("products_number"));
        sp.setPromotionalProduct(rs.getBoolean("promotional_product"));
        return sp;
    }
}