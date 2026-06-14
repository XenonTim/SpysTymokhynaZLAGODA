package org.example.controller;

import org.example.dao.CustomerCardDAO;
import org.example.model.CustomerCard;
import org.example.util.HtmlPage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Картки клієнтів.
 * Доступ:
 * - МЕНЕДЖЕР: CRUD + фільтрація по відсотку знижки (п.12)
 * - КАСИР: перегляд, додавання, редагування (п.6, п.8)
 * Видалення — лише менеджер.
 */
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
            if (!"Менеджер".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Видалення карток лише для менеджера");
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

        // Фільтр по відсотку (вимога менеджера п.12: клієнти з певним відсотком)
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
            if (!"Менеджер".equals(role)) {
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
            return "Номер телефону не може перевищувати 13 символів";
        }
        if (card.getPercent() < 0 || card.getPercent() > 100) {
            return "Відсоток знижки повинен бути від 0 до 100";
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
                      <div class="col-md-4"><label class="form-label">Прізвище</label><input class="form-control" name="cust_surname" required value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Ім'я</label><input class="form-control" name="cust_name" required value="%s"></div>
                      <div class="col-md-4"><label class="form-label">По батькові</label><input class="form-control" name="cust_patronymic" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Телефон (до 13 симв.)</label><input class="form-control" name="phone_number" maxlength="13" required value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Місто</label><input class="form-control" name="city" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Вулиця</label><input class="form-control" name="street" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Індекс</label><input class="form-control" name="zip_code" value="%s"></div>
                      <div class="col-md-4"><label class="form-label">Відсоток знижки (%%)</label><input class="form-control" name="percent" type="number" min="0" max="100" required value="%s"></div>
                      <div class="col-12 d-flex gap-2">
                        <button class="btn btn-primary" type="submit">Зберегти</button>
                        <a class="btn btn-outline-secondary" href="customers">Скасувати</a>
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
                        ? "Нова карта клієнта" : "Редагування карти клієнта",
                body, request.getContextPath() + "/customers");
    }

    private String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object role = session.getAttribute("userRole");
        return role != null ? role.toString() : null;
    }
}