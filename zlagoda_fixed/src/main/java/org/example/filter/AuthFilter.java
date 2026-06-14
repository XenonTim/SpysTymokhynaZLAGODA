package org.example.filter;

import org.example.dao.EmployeeDAO;
import org.example.model.Employee;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req   = (HttpServletRequest) request;
        HttpServletResponse res  = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // Публічні ресурси — пропускаємо без перевірки
        if (uri.endsWith("/auth_page.jsp") || uri.endsWith("/login") || uri.endsWith("/logout")
                || uri.contains("/css/") || uri.contains("/js/") || uri.contains("/img/")
                || uri.endsWith(".ico")) {
            HttpSession current = req.getSession(false);
            // При відкритті сторінки входу — гарантовано знищуємо стару сесію
            if (uri.endsWith("/auth_page.jsp") && current != null
                    && current.getAttribute("userId") != null) {
                current.invalidate();
            }
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect(req.getContextPath() + "/auth_page.jsp");
            return;
        }

        // Відновлюємо/синхронізуємо об'єкт користувача та роль на КОЖЕН запит
        Object currentUser = session.getAttribute("user");
        if (currentUser instanceof Employee employee) {
            req.setAttribute("user", employee);
            // Гарантуємо, що userRole завжди відповідає реальній ролі
            session.setAttribute("userRole", employee.getEmplRole());
        } else {
            String userId = (String) session.getAttribute("userId");
            if (userId != null) {
                Employee employee = employeeDAO.getById(userId);
                if (employee != null) {
                    session.setAttribute("user", employee);
                    req.setAttribute("user", employee);
                    session.setAttribute("userRole", employee.getEmplRole());
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
