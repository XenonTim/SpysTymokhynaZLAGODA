package org.example.dao;

import org.example.model.Check;
import org.example.model.Sale;
import org.example.util.DBManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckDAO {

    public List<Check> getAllChecksSorted() {
        return searchChecks(null, null, null, null, "date_desc");
    }

    public List<Check> searchChecks(String query, String sortBy) {
        return searchChecks(query, null, null, null, sortBy);
    }

    public List<Check> searchChecks(String query, String employeeIdFilter, Timestamp dateFrom, Timestamp dateTo, String sortBy) {
        List<Check> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.check_number, c.id_employee, c.card_number, c.print_date, c.sum_total, c.vat, " +
                        "CONCAT(e.empl_surname, ' ', e.empl_name) AS employee_name, " +
                        "COALESCE(CONCAT(cc.cust_surname, ' ', cc.cust_name), '') AS card_owner " +
                        "FROM `Check` c " +
                        "JOIN Employee e ON c.id_employee = e.id_employee " +
                        "LEFT JOIN Customer_Card cc ON c.card_number = cc.card_number");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            conditions.add("(CAST(c.check_number AS CHAR) LIKE ? OR CAST(c.id_employee AS CHAR) LIKE ?)");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (employeeIdFilter != null && !employeeIdFilter.isBlank()) {
            conditions.add("c.id_employee = ?");
            params.add(employeeIdFilter);
        }
        if (dateFrom != null) {
            conditions.add("c.print_date >= ?");
            params.add(dateFrom);
        }
        if (dateTo != null) {
            conditions.add("c.print_date <= ?");
            params.add(dateTo);
        }
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY ");
        switch (sortBy == null ? "" : sortBy) {
            case "sum_desc" -> sql.append("c.sum_total DESC");
            case "number"   -> sql.append("c.check_number ASC");
            default         -> sql.append("c.print_date DESC, c.check_number DESC");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapCheck(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Check getById(String id) {
        String sql = "SELECT c.check_number, c.id_employee, c.card_number, c.print_date, c.sum_total, c.vat, " +
                "CONCAT(e.empl_surname, ' ', e.empl_name) AS employee_name, " +
                "COALESCE(CONCAT(cc.cust_surname, ' ', cc.cust_name), '') AS card_owner " +
                "FROM `Check` c JOIN Employee e ON c.id_employee = e.id_employee " +
                "LEFT JOIN Customer_Card cc ON c.card_number = cc.card_number WHERE c.check_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapCheck(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Sale> getSalesForCheck(String checkNumber) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.UPC, s.check_number, s.product_number, s.selling_price, p.product_name " +
                "FROM Sale s JOIN Store_Product sp ON s.UPC = sp.UPC JOIN Product p ON sp.id_product = p.id_product " +
                "WHERE s.check_number = ? ORDER BY p.product_name";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sale sale = new Sale();
                    sale.setUpc(String.valueOf(rs.getObject("UPC")));
                    sale.setCheckNumber(String.valueOf(rs.getObject("check_number")));
                    sale.setProductNumber(rs.getInt("product_number"));
                    sale.setSellingPrice(rs.getBigDecimal("selling_price"));
                    sale.setProductName(rs.getString("product_name"));
                    list.add(sale);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public BigDecimal getTotalSumByPeriod(String employeeId, Timestamp dateFrom, Timestamp dateTo) {
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(sum_total), 0) FROM `Check` WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (employeeId != null && !employeeId.isBlank()) {
            sql.append(" AND id_employee = ?");
            params.add(employeeId);
        }
        if (dateFrom != null) { sql.append(" AND print_date >= ?"); params.add(dateFrom); }
        if (dateTo != null)   { sql.append(" AND print_date <= ?"); params.add(dateTo); }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public int getTotalUnitsSoldByPeriod(String upc, Timestamp dateFrom, Timestamp dateTo) {
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(s.product_number), 0) FROM Sale s " +
                        "JOIN `Check` c ON s.check_number = c.check_number WHERE s.UPC = ?");
        List<Object> params = new ArrayList<>();
        params.add(upc);
        if (dateFrom != null) { sql.append(" AND c.print_date >= ?"); params.add(dateFrom); }
        if (dateTo != null)   { sql.append(" AND c.print_date <= ?"); params.add(dateTo); }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void createCheck(Check check, List<Sale> sales) {
        String checkSql = "INSERT INTO `Check` (id_employee, card_number, print_date, sum_total, vat) VALUES (?, ?, ?, ?, ?)";
        String saleSql  = "INSERT INTO Sale (UPC, check_number, product_number, selling_price) VALUES (?, ?, ?, ?)";
        String stockSql = "UPDATE Store_Product SET products_number = products_number - ? WHERE UPC = ? AND products_number >= ?";
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            String generatedCheckNumber;
            try (PreparedStatement stmt = conn.prepareStatement(checkSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, check.getIdEmployee());
                if (check.getCardNumber() == null || check.getCardNumber().isBlank()) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setString(2, check.getCardNumber());
                }
                stmt.setTimestamp(3, check.getPrintDate() == null ? new Timestamp(System.currentTimeMillis()) : check.getPrintDate());
                stmt.setBigDecimal(4, check.getSumTotal() == null ? BigDecimal.ZERO : check.getSumTotal());
                stmt.setBigDecimal(5, check.getVat() == null ? BigDecimal.ZERO : check.getVat());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedCheckNumber = String.valueOf(keys.getLong(1));
                    } else {
                        throw new SQLException("Failed to get check number");
                    }
                }
            }
            check.setCheckNumber(generatedCheckNumber);

            try (PreparedStatement saleStmt  = conn.prepareStatement(saleSql);
                 PreparedStatement stockStmt = conn.prepareStatement(stockSql)) {
                for (Sale sale : sales) {
                    saleStmt.setString(1, sale.getUpc());
                    saleStmt.setString(2, generatedCheckNumber);
                    saleStmt.setInt(3, sale.getProductNumber());
                    saleStmt.setBigDecimal(4, sale.getSellingPrice());
                    saleStmt.addBatch();

                    stockStmt.setInt(1, sale.getProductNumber());
                    stockStmt.setString(2, sale.getUpc());
                    stockStmt.setInt(3, sale.getProductNumber());
                    stockStmt.addBatch();
                }
                saleStmt.executeBatch();
                int[] stockUpdates = stockStmt.executeBatch();
                for (int upd : stockUpdates) {
                    if (upd == 0) {
                        throw new SQLException("Not enough product in stock");
                    }
                }
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public void updateCheck(Check check) {
        String sql = "UPDATE `Check` SET id_employee = ?, card_number = ?, print_date = ?, sum_total = ?, vat = ? WHERE check_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, check.getIdEmployee());
            if (check.getCardNumber() == null || check.getCardNumber().isBlank()) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setString(2, check.getCardNumber());
            }
            stmt.setTimestamp(3, check.getPrintDate());
            stmt.setBigDecimal(4, check.getSumTotal());
            stmt.setBigDecimal(5, check.getVat());
            stmt.setString(6, check.getCheckNumber());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCheck(String checkNumber) {
        String sql = "DELETE FROM `Check` WHERE check_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkNumber);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Check mapCheck(ResultSet rs) throws SQLException {
        Check check = new Check();
        check.setCheckNumber(String.valueOf(rs.getObject("check_number")));
        check.setIdEmployee(String.valueOf(rs.getObject("id_employee")));
        Object card = rs.getObject("card_number");
        check.setCardNumber(card == null ? null : String.valueOf(card));
        check.setPrintDate(rs.getTimestamp("print_date"));
        check.setSumTotal(rs.getBigDecimal("sum_total"));
        check.setVat(rs.getBigDecimal("vat"));
        check.setEmployeeName(rs.getString("employee_name"));
        check.setCardOwner(rs.getString("card_owner"));
        return check;
    }
}