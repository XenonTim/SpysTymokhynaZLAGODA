package org.example.controller;

import org.example.dao.ProductDAO;
import org.example.dao.StoreProductDAO;
import org.example.model.Product;
import org.example.model.StoreProduct;
import org.example.util.HtmlPage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Товари у магазині (Store_Product).
 * МЕНЕДЖЕР: CRUD, фільтрація акційних/неакційних по кількості та назві.
 * КАСИР: лише перегляд, фільтрація акційних/неакційних.
 */
@WebServlet("/store-products")
public class StoreProductServlet extends HttpServlet {

    private final StoreProductDAO storeProductDAO = new StoreProductDAO();
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = getRole(request);
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/auth_page.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            if (!"Manager".equals(role)) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
            storeProductDAO.deleteStoreProduct(request.getParameter("id"));
            response.sendRedirect(request.getContextPath() + "/store-products");
            return;
        }
        if ("new".equals(action) || "edit".equals(action)) {
            if (!"Manager".equals(role)) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
            renderForm(request, response, "edit".equals(action) ? storeProductDAO.getByUpc(request.getParameter("id")) : null, null);
            return;
        }

        Boolean promotionalFilter = null;
        String promoParam = request.getParameter("promo");
        if ("true".equals(promoParam))  promotionalFilter = Boolean.TRUE;
        if ("false".equals(promoParam)) promotionalFilter = Boolean.FALSE;

        if ("lookup".equals(action)) {
            String upc = request.getParameter("upc");
            StoreProduct sp = storeProductDAO.getByUpc(upc);
            renderUpcLookup(request, response, sp, upc);
            return;
        }

        List<StoreProduct> products = storeProductDAO.searchStoreProducts(
                request.getParameter("searchQuery"), promotionalFilter, request.getParameter("sortBy"));

        request.setAttribute("storeProducts", products);
        request.setAttribute("promoFilter", promoParam);
        request.getRequestDispatcher("/index.jsp?page=products_in_stock").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"Manager".equals(getRole(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only management is authorised to edit products");
            return;
        }
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            StoreProduct sp = fromRequest(request);
            String err = validate(sp);
            if (err != null) {
                renderForm(request, response, sp, err);
                return;
            }
            storeProductDAO.insertStoreProduct(sp);
        } else if ("update".equals(action)) {
            StoreProduct sp = fromRequest(request);
            String err = validate(sp);
            if (err != null) {
                renderForm(request, response, sp, err);
                return;
            }
            storeProductDAO.updateStoreProduct(sp);
        } else if ("delete".equals(action)) {
            storeProductDAO.deleteStoreProduct(request.getParameter("upc"));
        }
        response.sendRedirect(request.getContextPath() + "/store-products");
    }

    private String validate(StoreProduct sp) {
        if (sp.getSellingPrice() == null || sp.getSellingPrice().signum() < 0) {
            return "Selling price must be greater than or equal to zero.";
        }
        if (sp.getProductsNumber() < 0) {
            return "Product number must be greater than or equal to zero.";
        }
        if (sp.isPromotionalProduct() && sp.getUpcProm() != null) {
            StoreProduct base = storeProductDAO.getByUpc(sp.getUpcProm());
            if (base != null && base.getSellingPrice() != null) {
                sp.setSellingPrice(base.getSellingPrice()
                        .multiply(new java.math.BigDecimal("0.8"))
                        .setScale(2, java.math.RoundingMode.HALF_UP));
            }
        }
        return null;
    }


    private StoreProduct fromRequest(HttpServletRequest request) {
        StoreProduct sp = new StoreProduct();
        sp.setUpc(request.getParameter("upc"));
        String upcProm = request.getParameter("upc_prom");
        sp.setUpcProm(upcProm != null && !upcProm.isBlank() ? upcProm : null);
        sp.setIdProduct(Integer.parseInt(request.getParameter("id_product")));
        sp.setSellingPrice(new java.math.BigDecimal(request.getParameter("selling_price")));
        sp.setProductsNumber(Integer.parseInt(request.getParameter("products_number")));
        sp.setPromotionalProduct(request.getParameter("promotional_product") != null);
        return sp;
    }

    private void renderForm(HttpServletRequest request, HttpServletResponse response, StoreProduct product, String error) throws IOException {
        List<Product> products = productDAO.getAllProductsSorted();
        StringBuilder options = new StringBuilder();
        for (Product p : products) {
            boolean selected = product != null && product.getIdProduct() == p.getIdProduct();
            options.append("<option value=\"").append(p.getIdProduct()).append("\"")
                    .append(selected ? " selected" : "")
                    .append(">").append(HtmlPage.esc(p.getIdProduct() + " — " + p.getProductName())).append("</option>");
        }
        String action = (product != null && product.getUpc() != null && !product.getUpc().isBlank()) ? "update" : "add";
        String hiddenUpc = "update".equals(action)
                ? "<input type=\"hidden\" name=\"upc\" value=\"" + HtmlPage.esc(product.getUpc()) + "\">" : "";
        String errorHtml = error != null ? "<div class=\"alert alert-danger\">" + HtmlPage.esc(error) + "</div>" : "";
        String body = """
                %s
                <div class="card shadow-sm">
                  <div class="card-body">
                    <form method="post" action="store-products" class="row g-3">
                      <input type="hidden" name="action" value="%s">
                      %s
                      <div class="col-md-4">
                        <label class="form-label">non-discounted product UPC</label>
                        <input class="form-control" name="upc_prom" value="%s" placeholder="NA">
                      </div>
                      <div class="col-md-8">
                        <label class="form-label">Product</label>
                        <select class="form-select" name="id_product" required>%s</select>
                      </div>
                      <div class="col-md-4">
                        <label class="form-label">Selling price (VAT included)</label>
                        <input class="form-control" name="selling_price" type="number" step="0.01" min="0" required value="%s">
                        <div class="form-text">For discounted products price includes %%20 off.</div>
                      </div>
                      <div class="col-md-4">
                        <label class="form-label">Amount</label>
                        <input class="form-control" name="products_number" type="number" min="0" required value="%s">
                      </div>
                      <div class="col-md-4 d-flex align-items-end">
                        <div class="form-check">
                          <input class="form-check-input" type="checkbox" name="promotional_product" id="promoChk" %s>
                          <label class="form-check-label" for="promoChk">Discounted product</label>
                        </div>
                      </div>
                      <div class="col-12 d-flex gap-2">
                        <button class="btn btn-primary" type="submit">Save</button>
                        <a class="btn btn-outline-secondary" href="store-products">Cancel</a>
                      </div>
                    </form>
                  </div>
                </div>
                """.formatted(errorHtml, action, hiddenUpc,
                product == null ? "" : HtmlPage.esc(product.getUpcProm()),
                options,
                product == null ? "" : HtmlPage.esc(product.getSellingPrice()),
                product == null ? "0" : HtmlPage.esc(product.getProductsNumber()),
                product != null && product.isPromotionalProduct() ? "checked" : "");
        HtmlPage.render(response, product == null ? "New product in store" : "Edit product in store",
                body, request.getContextPath() + "/store-products");
    }

    private void renderUpcLookup(HttpServletRequest request, HttpServletResponse response,
                                 StoreProduct sp, String upc) throws IOException {
        String body;
        if (sp == null) {
            body = "<div class=\"alert alert-warning\">Product with UPC «" + HtmlPage.esc(upc) + "» not found.</div>";
        } else {
            body = """
                    <div class="card shadow-sm">
                      <div class="card-body">
                        <h5>Information about product by UPC</h5>
                        <dl class="row">
                          <dt class="col-sm-3">UPC</dt><dd class="col-sm-9">%s</dd>
                          <dt class="col-sm-3">Name / Characteristics</dt><dd class="col-sm-9">%s</dd>
                          <dt class="col-sm-3">Selling price</dt><dd class="col-sm-9">%s грн</dd>
                          <dt class="col-sm-3">Amount available</dt><dd class="col-sm-9">%s</dd>
                          <dt class="col-sm-3">Discounted</dt><dd class="col-sm-9">%s</dd>
                        </dl>
                      </div>
                    </div>
                    """.formatted(
                    HtmlPage.esc(sp.getUpc()),
                    HtmlPage.esc(sp.getProductName()),
                    HtmlPage.esc(sp.getSellingPrice()),
                    HtmlPage.esc(sp.getProductsNumber()),
                    sp.isPromotionalProduct() ? "✅ Yes" : "❌ No");
        }
        HtmlPage.render(response, "Search by UPC", body, request.getContextPath() + "/store-products");
    }

    private String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object role = session.getAttribute("userRole");
        return role != null ? role.toString() : null;
    }
}