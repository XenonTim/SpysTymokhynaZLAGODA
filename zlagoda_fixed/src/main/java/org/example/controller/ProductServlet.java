package org.example.controller;

import org.example.dao.CategoryDAO;
import org.example.dao.ProductDAO;
import org.example.model.Category;
import org.example.model.Product;
import org.example.util.HtmlPage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Товари (Product).
 * МЕНЕДЖЕР: CRUD + пошук по категорії (п.9, п.13).
 * КАСИР: перегляд, пошук по назві та категорії (п.1, п.4, п.5).
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = getRole(request);
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/auth_page.jsp");
            return;
        }

        String action = request.getParameter("action");

        // CRUD — лише менеджер
        if ("delete".equals(action) || "new".equals(action) || "edit".equals(action)) {
            if (!"Менеджер".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Тільки менеджер може змінювати товари");
                return;
            }
            if ("delete".equals(action)) {
                productDAO.deleteProduct(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }
            renderForm(request, response,
                    "edit".equals(action) ? productDAO.getById(Integer.parseInt(request.getParameter("id"))) : null);
            return;
        }

        // Пошук товарів за категорією (вимога п.13, п.5)
        String categoryParam = request.getParameter("category");
        List<Product> products;
        if (categoryParam != null && !categoryParam.isBlank()) {
            try {
                int catNum = Integer.parseInt(categoryParam);
                products = productDAO.getProductsByCategory(catNum);
            } catch (NumberFormatException e) {
                products = productDAO.searchProducts(request.getParameter("searchQuery"), request.getParameter("sortBy"));
            }
        } else {
            products = productDAO.searchProducts(request.getParameter("searchQuery"), request.getParameter("sortBy"));
        }

        request.setAttribute("products", products);
        request.setAttribute("categories", categoryDAO.getAllCategoriesSorted());
        request.setAttribute("selectedCategory", categoryParam);
        request.getRequestDispatcher("/index.jsp?page=products").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"Менеджер".equals(getRole(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            productDAO.insertProduct(fromRequest(request));
        } else if ("update".equals(action)) {
            Product p = fromRequest(request);
            p.setIdProduct(Integer.parseInt(request.getParameter("id_product")));
            productDAO.updateProduct(p);
        } else if ("delete".equals(action)) {
            productDAO.deleteProduct(Integer.parseInt(request.getParameter("id_product")));
        }
        response.sendRedirect(request.getContextPath() + "/products");
    }

    private Product fromRequest(HttpServletRequest request) {
        Product p = new Product();
        p.setCategoryNumber(Integer.parseInt(request.getParameter("category_number")));
        p.setProductName(request.getParameter("product_name"));
        p.setCharacteristics(request.getParameter("characteristics"));
        return p;
    }

    private void renderForm(HttpServletRequest request, HttpServletResponse response, Product product) throws IOException {
        List<Category> categories = categoryDAO.getAllCategoriesSorted();
        StringBuilder options = new StringBuilder();
        for (Category c : categories) {
            boolean selected = product != null && product.getCategoryNumber() == c.getCategoryNumber();
            options.append("<option value=\"").append(c.getCategoryNumber()).append("\"")
                    .append(selected ? " selected" : "")
                    .append(">").append(HtmlPage.esc(c.getCategoryName())).append("</option>");
        }
        String action = product == null ? "add" : "update";
        String hiddenId = product == null ? ""
                : "<input type=\"hidden\" name=\"id_product\" value=\"" + HtmlPage.esc(product.getIdProduct()) + "\">";
        String body = """
                <div class="card shadow-sm">
                  <div class="card-body">
                    <form method="post" action="products" class="row g-3">
                      <input type="hidden" name="action" value="%s">
                      %s
                      <div class="col-md-4">
                        <label class="form-label">Категорія</label>
                        <select class="form-select" name="category_number" required>%s</select>
                      </div>
                      <div class="col-md-8">
                        <label class="form-label">Назва товару</label>
                        <input class="form-control" name="product_name" required value="%s">
                      </div>
                      <div class="col-12">
                        <label class="form-label">Характеристики</label>
                        <textarea class="form-control" name="characteristics" rows="3" required>%s</textarea>
                      </div>
                      <div class="col-12 d-flex gap-2">
                        <button class="btn btn-primary" type="submit">Зберегти</button>
                        <a class="btn btn-outline-secondary" href="products">Скасувати</a>
                      </div>
                    </form>
                  </div>
                </div>
                """.formatted(action, hiddenId, options,
                product == null ? "" : HtmlPage.esc(product.getProductName()),
                product == null ? "" : HtmlPage.esc(product.getCharacteristics()));
        HtmlPage.render(response, product == null ? "Новий товар" : "Редагування товару",
                body, request.getContextPath() + "/products");
    }

    private String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object role = session.getAttribute("userRole");
        return role != null ? role.toString() : null;
    }
}