package org.example.dao;

import org.example.model.CustomerCard;
import org.example.util.DBManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerCardDAO {

    public List<CustomerCard> getAllCardsSorted() {
        return searchCards(null, "surname_asc");
    }

    public List<CustomerCard> searchCards(String query, String sortBy) {
        return searchCards(query, null, sortBy);
    }

    /**
     * Пошук карток з необов'язковим фільтром по відсотку (вимога менеджера п.12).
     * @param percentFilter якщо != null — показати лише клієнтів з цим відсотком
     */
    public List<CustomerCard> searchCards(String query, Integer percentFilter, String sortBy) {
        List<CustomerCard> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT card_number, cust_surname, cust_name, cust_patronymic, phone_number, city, street, zip_code, percent FROM Customer_Card");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            conditions.add("(cust_surname LIKE ? OR cust_name LIKE ? OR CAST(card_number AS CHAR) LIKE ?)");
            String like = "%" + query.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (percentFilter != null) {
            conditions.add("percent = ?");
            params.add(percentFilter);
        }
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY ");
        switch (sortBy == null ? "" : sortBy) {
            case "percent_desc" -> sql.append("percent DESC, cust_surname ASC");
            case "card_number"  -> sql.append("card_number ASC");
            default             -> sql.append("cust_surname ASC");
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapCard(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public CustomerCard getById(String id) {
        String sql = "SELECT card_number, cust_surname, cust_name, cust_patronymic, phone_number, city, street, zip_code, percent FROM Customer_Card WHERE card_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapCard(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertCard(CustomerCard card) {
        String sql = "INSERT INTO Customer_Card (cust_surname, cust_name, cust_patronymic, phone_number, city, street, zip_code, percent) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, card.getCustSurname());
            stmt.setString(2, card.getCustName());
            stmt.setString(3, card.getCustPatronymic());
            stmt.setString(4, card.getPhoneNumber());
            stmt.setString(5, card.getCity());
            stmt.setString(6, card.getStreet());
            stmt.setString(7, card.getZipCode());
            stmt.setInt(8, card.getPercent());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) card.setCardNumber(String.valueOf(keys.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCard(CustomerCard card) {
        String sql = "UPDATE Customer_Card SET cust_surname = ?, cust_name = ?, cust_patronymic = ?, phone_number = ?, city = ?, street = ?, zip_code = ?, percent = ? WHERE card_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, card.getCustSurname());
            stmt.setString(2, card.getCustName());
            stmt.setString(3, card.getCustPatronymic());
            stmt.setString(4, card.getPhoneNumber());
            stmt.setString(5, card.getCity());
            stmt.setString(6, card.getStreet());
            stmt.setString(7, card.getZipCode());
            stmt.setInt(8, card.getPercent());
            stmt.setString(9, card.getCardNumber());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCard(String id) {
        String sql = "DELETE FROM Customer_Card WHERE card_number = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CustomerCard mapCard(ResultSet rs) throws SQLException {
        CustomerCard card = new CustomerCard();
        card.setCardNumber(String.valueOf(rs.getObject("card_number")));
        card.setCustSurname(rs.getString("cust_surname"));
        card.setCustName(rs.getString("cust_name"));
        card.setCustPatronymic(rs.getString("cust_patronymic"));
        card.setPhoneNumber(rs.getString("phone_number"));
        card.setCity(rs.getString("city"));
        card.setStreet(rs.getString("street"));
        card.setZipCode(rs.getString("zip_code"));
        card.setPercent(rs.getInt("percent"));
        return card;
    }
}