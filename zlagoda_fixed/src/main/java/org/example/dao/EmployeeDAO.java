package org.example.dao;

import org.example.model.Employee;
import org.example.util.DBManager;
import org.example.util.PasswordUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee getEmployeeForAuth(String id) {
        String sql = "SELECT e.*, ua.password_hash FROM Employee e LEFT JOIN User_Auth ua ON e.id_employee = ua.id_employee WHERE e.id_employee = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapEmployee(rs, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Employee getById(String id) {
        return getEmployeeForAuth(id);
    }

    public List<Employee> getAllEmployeesSorted() {
        return searchEmployees(null, null, "surname_asc");
    }

    public List<Employee> getCashiersSorted() {
        return searchEmployees(null, "Касир", "surname_asc");
    }

    public List<Employee> searchEmployees(String query, String roleFilter, String sortBy) {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT e.id_employee, e.empl_surname, e.empl_name, e.empl_patronymic, e.empl_role, " +
                        "e.salary, e.date_of_birth, e.date_of_start, e.phone_number, e.city, e.street, e.zip_code " +
                        "FROM Employee e");
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            conditions.add("(e.empl_surname LIKE ? OR CAST(e.id_employee AS CHAR) LIKE ? OR e.empl_role LIKE ?)");
            String like = "%" + query.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (roleFilter != null && !roleFilter.isBlank()) {
            conditions.add("e.empl_role = ?");
            params.add(roleFilter);
        }
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        sql.append(" ORDER BY ");
        switch (sortBy == null ? "" : sortBy) {
            case "salary_desc" -> sql.append("e.salary DESC, e.empl_surname ASC");
            case "role"        -> sql.append("e.empl_role ASC, e.empl_surname ASC");
            default            -> sql.append("e.empl_surname ASC");
        }
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapEmployee(rs, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Employee> searchEmployees(String query, String sortBy) {
        return searchEmployees(query, null, sortBy);
    }

    public List<Employee> findBySurname(String surname) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.id_employee, e.empl_surname, e.empl_name, e.empl_patronymic, e.empl_role, " +
                "e.salary, e.date_of_birth, e.date_of_start, e.phone_number, e.city, e.street, e.zip_code " +
                "FROM Employee e WHERE e.empl_surname LIKE ? ORDER BY e.empl_surname ASC";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + surname.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapEmployee(rs, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertEmployee(Employee emp) {
        // Якщо ID не вказано — дозволяємо AUTO_INCREMENT згенерувати його
        String insertAutoId = "INSERT INTO Employee (empl_surname, empl_name, empl_patronymic, empl_role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertWithId = "INSERT INTO Employee (id_employee, empl_surname, empl_name, empl_patronymic, empl_role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertAuthSql = "INSERT INTO User_Auth (id_employee, password_hash) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);

            String generatedId;
            boolean hasExplicitId = emp.getIdEmployee() != null && !emp.getIdEmployee().isBlank();

            if (hasExplicitId) {
                try (PreparedStatement stmt = conn.prepareStatement(insertWithId)) {
                    stmt.setString(1, emp.getIdEmployee());
                    setCommonEmployeeFields(stmt, emp, 2);
                    stmt.executeUpdate();
                }
                generatedId = emp.getIdEmployee();
            } else {
                // AUTO_INCREMENT — зчитуємо згенерований ID
                try (PreparedStatement stmt = conn.prepareStatement(insertAutoId, Statement.RETURN_GENERATED_KEYS)) {
                    setCommonEmployeeFields(stmt, emp, 1);
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            generatedId = String.valueOf(keys.getLong(1));
                            emp.setIdEmployee(generatedId);
                        } else {
                            throw new SQLException("Не вдалося отримати згенерований ID працівника");
                        }
                    }
                }
            }

            if (emp.getPasswordHash() != null && !emp.getPasswordHash().isBlank()) {
                try (PreparedStatement stmt = conn.prepareStatement(insertAuthSql)) {
                    stmt.setString(1, generatedId);
                    stmt.setString(2, PasswordUtil.hashPassword(emp.getPasswordHash()));
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return false;
    }

    public boolean updateEmployee(Employee emp, boolean updatePassword) {
        String updateSql = "UPDATE Employee SET empl_surname = ?, empl_name = ?, empl_patronymic = ?, empl_role = ?, salary = ?, date_of_birth = ?, date_of_start = ?, phone_number = ?, city = ?, street = ?, zip_code = ? WHERE id_employee = ?";
        String updateAuthSql = "UPDATE User_Auth SET password_hash = ? WHERE id_employee = ?";
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, emp.getEmplSurname());
                stmt.setString(2, emp.getEmplName());
                stmt.setString(3, emp.getPatronymic());
                stmt.setString(4, emp.getEmplRole());
                stmt.setBigDecimal(5, emp.getSalary() == null ? BigDecimal.ZERO : emp.getSalary());
                stmt.setDate(6, emp.getDateOfBirth());
                stmt.setDate(7, emp.getDateOfStart());
                stmt.setString(8, emp.getPhoneNumber());
                stmt.setString(9, emp.getCity());
                stmt.setString(10, emp.getStreet());
                stmt.setString(11, emp.getZipCode());
                stmt.setString(12, emp.getIdEmployee());
                stmt.executeUpdate();
            }
            if (updatePassword && emp.getPasswordHash() != null && !emp.getPasswordHash().isBlank()) {
                try (PreparedStatement stmt = conn.prepareStatement(updateAuthSql)) {
                    stmt.setString(1, PasswordUtil.hashPassword(emp.getPasswordHash()));
                    stmt.setString(2, emp.getIdEmployee());
                    if (stmt.executeUpdate() == 0) {
                        try (PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO User_Auth (id_employee, password_hash) VALUES (?, ?)")) {
                            ins.setString(1, emp.getIdEmployee());
                            ins.setString(2, PasswordUtil.hashPassword(emp.getPasswordHash()));
                            ins.executeUpdate();
                        }
                    }
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return false;
    }

    public boolean deleteEmployee(String id) {
        String sql = "DELETE FROM Employee WHERE id_employee = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- Допоміжні методи ---

    /** Встановлює поля починаючи з startIndex (без id_employee) */
    private void setCommonEmployeeFields(PreparedStatement stmt, Employee emp, int startIndex) throws SQLException {
        int i = startIndex;
        stmt.setString(i++, emp.getEmplSurname());
        stmt.setString(i++, emp.getEmplName());
        stmt.setString(i++, emp.getPatronymic());
        stmt.setString(i++, emp.getEmplRole());
        stmt.setBigDecimal(i++, emp.getSalary() == null ? BigDecimal.ZERO : emp.getSalary());
        stmt.setDate(i++, emp.getDateOfBirth());
        stmt.setDate(i++, emp.getDateOfStart());
        stmt.setString(i++, emp.getPhoneNumber());
        stmt.setString(i++, emp.getCity());
        stmt.setString(i++, emp.getStreet());
        stmt.setString(i, emp.getZipCode());
    }

    private Employee mapEmployee(ResultSet rs, boolean withPassword) throws SQLException {
        Employee emp = new Employee();
        emp.setIdEmployee(String.valueOf(rs.getObject("id_employee")));
        emp.setEmplSurname(rs.getString("empl_surname"));
        emp.setEmplName(rs.getString("empl_name"));
        emp.setPatronymic(rs.getString("empl_patronymic"));
        emp.setEmplRole(rs.getString("empl_role"));
        emp.setSalary(rs.getBigDecimal("salary"));
        emp.setDateOfBirth(rs.getDate("date_of_birth"));
        emp.setDateOfStart(rs.getDate("date_of_start"));
        emp.setPhoneNumber(rs.getString("phone_number"));
        emp.setCity(rs.getString("city"));
        emp.setStreet(rs.getString("street"));
        emp.setZipCode(rs.getString("zip_code"));
        if (withPassword) emp.setPasswordHash(rs.getString("password_hash"));
        return emp;
    }

    private void rollback(Connection conn) {
        if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
    }
}
