package org.example.controller;

import org.example.dao.CategoryDAO;
import org.example.model.Category;
import org.example.util.HtmlPage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Категорії товарів.
 * МЕНЕДЖЕР: CRUD (п.1, п.2, п.3, п.8)
 * КАСИР: лише перегляд (неявно через list)
 */
@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = getRole(request);
        String action = request.getParameter("action");

        if ("delete".equals(action) || "new".equals(action) || "edit".equals(action)) {
            if (!"Менеджер".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Тільки менеджер може змінювати категорії");
                return;
            }
            if ("delete".equals(action)) {
                categoryDAO.deleteCategory(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect(request.getContextPath() + "/categories");
                return;
            }
            renderForm(request, response,
                    "edit".equals(action) ? categoryDAO.getById(Integer.parseInt(request.getParameter("id"))) : null);
            return;
        }

        request.setAttribute("categoriesList",
                categoryDAO.searchCategories(request.getParameter("searchQuery"), request.getParameter("sortBy")));
        request.getRequestDispatcher("/index.jsp?page=categories").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"Менеджер".equals(getRole(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            Category c = new Category();
            c.setCategoryName(request.getParameter("category_name"));
            categoryDAO.insertCategory(c);
        } else if ("update".equals(action)) {
            Category c = new Category();
            c.setCategoryNumber(Integer.parseInt(request.getParameter("category_number")));
            c.setCategoryName(request.getParameter("category_name"));
            categoryDAO.updateCategory(c);
        } else if ("delete".equals(action)) {
            categoryDAO.deleteCategory(Integer.parseInt(request.getParameter("category_number")));
        }
        response.sendRedirect(request.getContextPath() + "/categories");
    }

    private void renderForm(HttpServletRequest request, HttpServletResponse response, Category category) throws IOException {
        String action = category == null ? "add" : "update";
        String idField = category == null ? ""
                : "<input type=\"hidden\" name=\"category_number\" value=\"" + HtmlPage.esc(category.getCategoryNumber()) + "\">";
        String body = """
                <div class="card shadow-sm">
                  <div class="card-body">
                    <form method="post" action="categories" class="row g-3">
                      <input type="hidden" name="action" value="%s">
                      %s
                      <div class="col-12">
                        <label class="form-label">Назва категорії</label>
                        <input class="form-control" name="category_name" required value="%s">
                      </div>
                      <div class="col-12 d-flex gap-2">
                        <button class="btn btn-primary" type="submit">Зберегти</button>
                        <a class="btn btn-outline-secondary" href="categories">Скасувати</a>
                      </div>
                    </form>
                  </div>
                </div>
                """.formatted(action, idField, category == null ? "" : HtmlPage.esc(category.getCategoryName()));
        HtmlPage.render(response,
                category == null ? "Нова категорія" : "Редагування категорії",
                body, request.getContextPath() + "/categories");
    }

    private String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object role = session.getAttribute("userRole");
        return role != null ? role.toString() : null;
    }
}