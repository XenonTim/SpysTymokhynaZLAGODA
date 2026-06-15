package org.example.controller;

import org.example.dao.EmployeeDAO;
import org.example.model.Employee;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@WebServlet("/employees")
public class EmployeeServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isManager(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access only for management");
            return;
        }

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            employeeDAO.deleteEmployee(request.getParameter("id"));
            response.sendRedirect(request.getContextPath() + "/employees");
            return;
        }

        if ("new".equals(action) || "edit".equals(action)) {
            if ("edit".equals(action)) {
                request.setAttribute("employeeToEdit", employeeDAO.getById(request.getParameter("id")));
            }
            request.setAttribute("showForm", true);
        } else if ("search_phone".equals(action)) {
            String surname = request.getParameter("surname");
            if (surname != null && !surname.isBlank()) {
                request.setAttribute("employeesList", employeeDAO.findBySurname(surname));
                request.setAttribute("searchMode", "phone");
                request.setAttribute("searchSurname", surname);
            }
        } else {
            String roleFilter = request.getParameter("role");
            String query      = request.getParameter("searchQuery");
            String sortBy     = request.getParameter("sortBy");

            request.setAttribute("employeesList", employeeDAO.searchEmployees(query, roleFilter, sortBy));
            request.setAttribute("roleFilter", roleFilter);
        }

        request.getRequestDispatcher("/index.jsp?page=employees").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (!isManager(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access only for management");
            return;
        }

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            employeeDAO.deleteEmployee(request.getParameter("id_employee"));
            response.sendRedirect(request.getContextPath() + "/employees");
            return;
        }

        Employee emp = fromRequest(request);
        String error = validateEmployee(emp);

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("employeeToEdit", emp);
            request.setAttribute("showForm", true);
            request.setAttribute("employeesList", employeeDAO.getAllEmployeesSorted());
            request.getRequestDispatcher("/index.jsp?page=employees").forward(request, response);
            return;
        }

        if ("add".equals(action) || action == null) {
            employeeDAO.insertEmployee(emp);
        } else if ("update".equals(action)) {
            boolean updatePassword = emp.getPasswordHash() != null && !emp.getPasswordHash().isEmpty();
            employeeDAO.updateEmployee(emp, updatePassword);
        }

        response.sendRedirect(request.getContextPath() + "/employees");
    }

    // Допоміжні методи

    private String validateEmployee(Employee emp) {
        if (emp.getDateOfBirth() != null) {
            LocalDate dob = emp.getDateOfBirth().toLocalDate();
            int age = Period.between(dob, LocalDate.now()).getYears();
            if (age < 18) {
                return "Employee has to be of at least 18 years old.";
            }
        }
        if (emp.getPhoneNumber() != null && emp.getPhoneNumber().length() > 13) {
            return "Phone number cannot be longer than 13 characters(«+» included).";
        }
        if (emp.getSalary() != null && emp.getSalary().signum() < 0) {
            return "Salary cannot be negative.";
        }
        return null;
    }

    private Employee fromRequest(HttpServletRequest request) {
        Employee e = new Employee();
        e.setIdEmployee(blankToNull(request.getParameter("id_employee")));
        e.setEmplSurname(request.getParameter("empl_surname"));
        e.setEmplName(request.getParameter("empl_name"));
        e.setPatronymic(request.getParameter("empl_patronymic"));
        e.setEmplRole(request.getParameter("empl_role"));

        String salary = request.getParameter("salary");
        if (salary != null && !salary.isBlank()) {
            e.setSalary(new BigDecimal(salary));
        }

        String dob = request.getParameter("date_of_birth");
        if (dob != null && !dob.isBlank()) e.setDateOfBirth(java.sql.Date.valueOf(dob));

        String dos = request.getParameter("date_of_start");
        if (dos != null && !dos.isBlank()) e.setDateOfStart(java.sql.Date.valueOf(dos));

        e.setPhoneNumber(request.getParameter("phone_number"));
        e.setCity(request.getParameter("city"));
        e.setStreet(request.getParameter("street"));
        e.setZipCode(request.getParameter("zip_code"));
        e.setPasswordHash(blankToNull(request.getParameter("password")));
        return e;
    }

    private boolean isManager(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        return "Manager".equals(session.getAttribute("userRole"));
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}