package org.example.controller;

import org.example.dao.EmployeeDAO;
import org.example.model.Employee;
import org.example.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String empId = request.getParameter("employee_id");
        String password = request.getParameter("password");

        Employee emp = employeeDAO.getEmployeeForAuth(empId);

        if (emp != null && emp.getPasswordHash() != null && PasswordUtil.verifyPassword(password, emp.getPasswordHash())) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", emp);
            session.setAttribute("userId", emp.getIdEmployee());
            session.setAttribute("userRole", emp.getEmplRole());

            // Усіх користувачів відправляємо на сторінку профілю
            String redirect = request.getContextPath() + "/";
            response.sendRedirect(redirect);
            return;
        }

        request.setAttribute("error", "Invalid login and/or password");
        request.getRequestDispatcher("/auth_page.jsp").forward(request, response);
    }
}