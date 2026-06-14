package org.example.controller;

import org.example.dao.EmployeeDAO;
import org.example.model.Employee;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object user = session.getAttribute("user");
            if (user instanceof Employee employee) {
                request.setAttribute("user", employee);
            } else {
                String id = (String) session.getAttribute("userId");
                if (id != null) request.setAttribute("user", employeeDAO.getById(id));
            }
        }
        request.getRequestDispatcher("/index.jsp?page=profile").forward(request, response);
    }
}
