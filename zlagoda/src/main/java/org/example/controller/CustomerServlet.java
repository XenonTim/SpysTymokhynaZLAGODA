package org.example.controller;

import org.example.dao.CustomerCardDAO;
import org.example.model.CustomerCard;
import org.example.util.HtmlPage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/customers")
public class CustomerServlet extends HttpServlet {

    private final CustomerCardDAO customerCardDAO = new CustomerCardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = getRole(request);
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/auth_page.jsp");
            return;
        }
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            if (!"Manager".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only management is authorised to delete cards");
                return;
            }
            customerCardDAO.deleteCard(request.getParameter("id"));
            response.sendRedirect(request.getContextPath() + "/customers");
            return;
        }

        if ("new".equals(action) || "edit".equals(action)) {
            renderForm(request, response,
                    "edit".equals(action) ? customerCardDAO.getById(request.getParameter("id")) : null, null);
            return;
        }

        Integer percentFilter = null;
        String percentStr = request.getParameter("percent");
        if (percentStr != null && !percentStr.isBlank()) {
            try { percentFilter = Integer.parseInt(percentStr); } catch (NumberFormatException ignored) {}
        }

        request.setAttribute("customers",
                customerCardDAO.searchCards(request.getParameter("searchQuery"), percentFilter, request.getParameter("sortBy")));
        request.setAttribute("percentFilter", percentFilter);
        request.getRequestDispatcher("/index.jsp?page=clients").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String role = getRole(request);
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/auth_page.jsp");
            return;
        }
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            if (!"Manager".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            customerCardDAO.deleteCard(request.getParameter("card_number"));
        } else if ("add".equals(action)) {
            CustomerCard card = fromRequest(request);
            String err = validateCard(card);
            if (err != null) {
                renderForm(request, response, card, err);
                return;
            }
            customerCardDAO.insertCard(card);
        } else if ("update".equals(action)) {
            CustomerCard card = fromRequest(request);
            String err = validateCard(card);
            if (err != null) {
                renderForm(request, response, card, err);
                return;
            }
            customerCardDAO.updateCard(card);
        }
        response.sendRedirect(request.getContextPath() + "/customers");
    }

    private String validateCard(CustomerCard card) {
        if (card.getPhoneNumber() != null && card.getPhoneNumber().length() > 13) {
            return "Phone number can not be longer than 13 characters";
        }
        if (card.getPercent() < 0 || card.getPercent() > 100) {
            return "Discount percantage must be between 0 and 100";
        }
        return null;
    }

    private CustomerCard fromRequest(HttpServletRequest request) {
        CustomerCard c = new CustomerCard();
        c.setCardNumber(request.getParameter("card_number"));
        c.setCustSurname(request.getParameter("cust_surname"));
        c.setCustName(request.getParameter("cust_name"));
        c.setCustPatronymic(request.getParameter("cust_patronymic"));
        c.setPhoneNumber(request.getParameter("phone_number"));
        c.setCity(request.getParameter("city"));
        c.setStreet(request.getParameter("street"));
        c.setZipCode(request.getParameter("zip_code"));
        String pct = request.getParameter("percent");
        c.setPercent(pct != null && !pct.isBlank() ? Integer.parseInt(pct) : 0);
        return c;
    }

    private void renderForm(HttpServletRequest request, HttpServletResponse response,
                            CustomerCard cc, String error) throws IOException {
        String action = (cc != null && cc.getCardNumber() != null) ? "update" : "add";
        String hidden = "update".equals(action)
                ? "<input type=\"hidden\" name=\"card_number\" value=\"" + HtmlPage.esc(cc.getCardNumber()) + "\">" : "";
        String errorHtml = error != null
                ? "<div class=\"alert alert-danger\">" + HtmlPage.esc(error) + "</div>" : "";
        String body = """
                %s
                <div class="card shadow-sm">
                  <div class="card-body">
                    <form method="post" action="customers" class="row g-3">
                      <input type="hidden" name="action" value="%s">
                      %s
                      <div class="col-md-4"><label class="form-label">Surname</label><input class="form-control" name="cust_surname" required value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Name</label><input class="form-control" name="cust_name" required value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Patronymic</label><input class="form-control" name="cust_patronymic" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Phone number (up to 13 symbols)</label><input class="form-control" name="phone_number" maxlength="13" required value="%s"></div>
                      <div class="col-md-4"><label class="form-label">City</label><input class="form-control" name="city" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Street</label><input class="form-control" name="street" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Zip code</label><input class="form-control" name="zip_code" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Discount percantage (%%)</label><input class="form-control" name="percent" type="number" min="0" max="100" required value="%s"></div>
                      <div class="col-12 d-flex gap-2">
                        <button class="btn btn-primary" type="submit">Save</button>
                        <a class="btn btn-outline-secondary" href="customers">Cancel</a>
                      </div>
                    </form>
                  </div>
                </div>
                """.formatted(errorHtml, action, hidden,
                cc == null ? "" : HtmlPage.esc(cc.getCustSurname()),
                cc == null ? "" : HtmlPage.esc(cc.getCustName()),
                cc == null ? "" : HtmlPage.esc(cc.getCustPatronymic()),
                cc == null ? "" : HtmlPage.esc(cc.getPhoneNumber()),
                cc == null ? "" : HtmlPage.esc(cc.getCity()),
                cc == null ? "" : HtmlPage.esc(cc.getStreet()),
                cc == null ? "" : HtmlPage.esc(cc.getZipCode()),
                cc == null ? "0" : HtmlPage.esc(cc.getPercent()));
        HtmlPage.render(response, cc == null || cc.getCardNumber() == null
                        ? "New client card" : "Edit client card",
                body, request.getContextPath() + "/customers");
    }

    private String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object role = session.getAttribute("userRole");
        return role != null ? role.toString() : null;
    }
}