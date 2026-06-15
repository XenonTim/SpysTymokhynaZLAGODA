package org.example.controller;

import org.example.dao.CheckDAO;
import org.example.dao.SaleDAO;
import org.example.dao.StoreProductDAO;
import org.example.model.Check;
import org.example.model.Sale;
import org.example.model.StoreProduct;
import org.example.util.HtmlPage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/sales")
public class SaleServlet extends HttpServlet {

    private final SaleDAO saleDAO = new SaleDAO();
    private final StoreProductDAO storeProductDAO = new StoreProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = getRole(request);

        if ("Cashier".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/checks");
            return;
        }

        String action = request.getParameter("action");
        if ("delete".equals(action) || "new".equals(action) || "edit".equals(action)) {
            if ("delete".equals(action)) {
                saleDAO.deleteSale(request.getParameter("check"), request.getParameter("upc"));
                response.sendRedirect(request.getContextPath() + "/sales");
                return;
            }
            Sale saleToEdit = null;
            if ("edit".equals(action)) {
            }
            renderForm(request, response, saleToEdit);
            return;
        }

        request.setAttribute("sales", saleDAO.searchSales(request.getParameter("searchQuery"), request.getParameter("sortBy")));
        request.getRequestDispatcher("/index.jsp?page=sales").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"Manager".equals(getRole(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String action = request.getParameter("action");
        Sale sale = new Sale();
        sale.setCheckNumber(request.getParameter("check_number"));
        sale.setUpc(request.getParameter("upc"));
        sale.setProductNumber(Integer.parseInt(request.getParameter("product_number")));
        sale.setSellingPrice(new BigDecimal(request.getParameter("selling_price")));

        if ("add".equals(action)) {
            saleDAO.insertSale(sale);
        } else if ("update".equals(action)) {
            saleDAO.updateSale(sale);
        }
        response.sendRedirect(request.getContextPath() + "/sales");
    }

    private void renderForm(HttpServletRequest request, HttpServletResponse response, Sale sale) throws IOException {
        String action = sale == null ? "add" : "update";
        String hidden = "update".equals(action)
                ? "<input type=\"hidden\" name=\"check_number\" value=\"" + HtmlPage.esc(sale.getCheckNumber()) + "\">" +
                  "<input type=\"hidden\" name=\"upc\" value=\"" + HtmlPage.esc(sale.getUpc()) + "\">"
                : "";

        List<StoreProduct> products = storeProductDAO.getAllStoreProductsSortedByQty();
        StringBuilder upcOptions = new StringBuilder("<option value=\"\">Pick the product</option>");
        for (StoreProduct sp : products) {
            boolean selected = sale != null && sp.getUpc().equals(sale.getUpc());
            upcOptions.append("<option value=\"").append(HtmlPage.esc(sp.getUpc())).append("\"")
                    .append(selected ? " selected" : "").append(">")
                    .append(HtmlPage.esc(sp.getProductName())).append(" (UPC: ").append(HtmlPage.esc(sp.getUpc())).append(")</option>");
        }

        String body = """
                <div class="card shadow-sm">
                  <div class="card-body">
                    <form method="post" action="sales" class="row g-3">
                      <input type="hidden" name="action" value="%s">
                      %s
                      <div class="col-md-6">
                        <label class="form-label">Check ID</label>
                        <input class="form-control" name="check_number" required value="%s" %s>
                      </div>
                      <div class="col-md-6">
                        <label class="form-label">Product in stock</label>
                        <select class="form-select" name="upc" required %s>
                            %s
                        </select>
                      </div>
                      <div class="col-md-6">
                        <label class="form-label">Amount</label>
                        <input class="form-control" name="product_number" type="number" min="1" required value="%s">
                      </div>
                      <div class="col-md-6">
                        <label class="form-label">Selling price</label>
                        <input class="form-control" name="selling_price" type="number" step="0.01" min="0" required value="%s">
                      </div>
                      <div class="col-12 d-flex gap-2">
                        <button class="btn btn-primary" type="submit">Save</button>
                        <a class="btn btn-outline-secondary" href="sales">Cancel</a>
                      </div>
                    </form>
                  </div>
                </div>
                """.formatted(
                action, hidden,
                sale == null ? "" : HtmlPage.esc(sale.getCheckNumber()),
                "update".equals(action) ? "readonly" : "",
                "update".equals(action) ? "disabled" : "",
                upcOptions.toString(),
                sale == null ? "1" : HtmlPage.esc(sale.getProductNumber()),
                sale == null ? "0.00" : HtmlPage.esc(sale.getSellingPrice()));

        HtmlPage.render(response,
                sale == null ? "New sales item" : "Edit sales item",
                body, request.getContextPath() + "/sales");
    }

    private String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object role = session.getAttribute("userRole");
        return role != null ? role.toString() : null;
    }
}