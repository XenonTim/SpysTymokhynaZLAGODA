package org.example.controller;

import org.example.dao.CheckDAO;
import org.example.dao.CustomerCardDAO;
import org.example.dao.EmployeeDAO;
import org.example.dao.StoreProductDAO;
import org.example.model.Check;
import org.example.model.CustomerCard;
import org.example.model.Employee;
import org.example.model.Sale;
import org.example.model.StoreProduct;
import org.example.util.HtmlPage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Чеки.
 * МЕНЕДЖЕР: перегляд, пошук за датою/касиром, видалення, агрегатні запити (п.17–21).
 * КАСИР: створення чеків, перегляд своїх чеків (п.7, п.9, п.10, п.11).
 */
@WebServlet("/checks")
public class CheckServlet extends HttpServlet {

    private final CheckDAO checkDAO = new CheckDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final CustomerCardDAO customerCardDAO = new CustomerCardDAO();
    private final StoreProductDAO storeProductDAO = new StoreProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String role = getRole(request);
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/auth_page.jsp");
            return;
        }

        String action = request.getParameter("action");

        // Видалення — лише менеджер (вимога: "Всі права на вилучення даних надаються менеджеру")
        if ("delete".equals(action)) {
            if (!"Менеджер".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Видалення чеків лише для менеджера");
                return;
            }
            checkDAO.deleteCheck(request.getParameter("id"));
            response.sendRedirect(request.getContextPath() + "/checks");
            return;
        }

        // Форма нового чеку — лише касир
        if ("new".equals(action)) {
            if (!"Касир".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Створення чеків лише для касира");
                return;
            }
            renderCreateCheckForm(request, response, null);
            return;
        }

        // Перегляд конкретного чеку — всі авторизовані
        if ("view".equals(action)) {
            renderDetail(request, response, request.getParameter("id"));
            return;
        }

        // Агрегатні звіти — лише менеджер (п.19, п.20, п.21)
        if ("stats".equals(action)) {
            if (!"Менеджер".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            renderStats(request, response);
            return;
        }

        // Список чеків
        String employeeId = request.getParameter("employeeId");
        String dateFromStr = request.getParameter("dateFrom");
        String dateToStr   = request.getParameter("dateTo");

        // Касир бачить лише свої чеки (п.9, п.10)
        if ("Касир".equals(role)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                employeeId = (String) session.getAttribute("userId");
            }
        }

        Timestamp dateFrom = parseTimestampStart(dateFromStr);
        Timestamp dateTo   = parseTimestampEnd(dateToStr);

        List<Check> checks = checkDAO.searchChecks(
                request.getParameter("searchQuery"),
                employeeId,
                dateFrom,
                dateTo,
                request.getParameter("sortBy")
        );

        request.setAttribute("checks", checks);
        request.setAttribute("employees", "Менеджер".equals(role) ? employeeDAO.getCashiersSorted() : List.of());
        request.setAttribute("selectedEmployee", employeeId);
        request.setAttribute("dateFrom", dateFromStr);
        request.setAttribute("dateTo", dateToStr);
        request.getRequestDispatcher("/index.jsp?page=checks").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String role = getRole(request);
        String action = request.getParameter("action");

        // Видалення — лише менеджер
        if ("delete".equals(action)) {
            if (!"Менеджер".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            checkDAO.deleteCheck(request.getParameter("check_number"));
            response.sendRedirect(request.getContextPath() + "/checks");
            return;
        }

        // Створення чеку — лише касир (вимога: касир "здійснює продаж товарів")
        if ("add".equals(action)) {
            if (!"Касир".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Створення чеків лише для касира");
                return;
            }
            try {
                HttpSession session = request.getSession(false);
                String cashierId = session != null ? (String) session.getAttribute("userId") : null;
                Check check = buildCheckFromRequest(request, cashierId);
                List<Sale> sales = parseSalesFromRequest(request, check);
                checkDAO.createCheck(check, sales);
            } catch (RuntimeException e) {
                renderCreateCheckForm(request, response, "Помилка: " + e.getMessage());
                return;
            }
            response.sendRedirect(request.getContextPath() + "/checks");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/checks");
    }

    // -------------------------------------------------------
    private Check buildCheckFromRequest(HttpServletRequest request, String cashierId) {
        Check c = new Check();

        // АВТОГЕНЕРАЦІЯ НОМЕРА ЧЕКУ: номер чеку — це PK з AUTO_INCREMENT,
        // тому касир його не вводить, а БД згенерує його сама (createCheck
        // зчитує згенерований ключ через RETURN_GENERATED_KEYS).
        c.setCheckNumber(null);

        c.setIdEmployee(cashierId != null ? cashierId : request.getParameter("id_employee"));
        c.setCardNumber(blankToNull(request.getParameter("card_number")));
        c.setPrintDate(new Timestamp(System.currentTimeMillis()));

        // Розраховуємо суму і ПДВ з позицій продажу
        BigDecimal sumTotal = BigDecimal.ZERO;
        String[] upcs = request.getParameterValues("item_upc");
        String[] qtys = request.getParameterValues("item_qty");

        // Знижка по картці клієнта
        BigDecimal discountRate = BigDecimal.ZERO;
        if (c.getCardNumber() != null) {
            CustomerCard card = customerCardDAO.getById(c.getCardNumber());
            if (card != null && card.getPercent() > 0) {
                discountRate = new BigDecimal(card.getPercent()).divide(BigDecimal.valueOf(100));
            }
        }

        if (upcs != null) {
            for (int i = 0; i < upcs.length; i++) {
                String upc = upcs[i].trim();
                // 2. Захист: якщо випадково залишили порожній рядок товару у формі — пропускаємо його
                if (upc.isEmpty()) continue;

                int qty = Integer.parseInt(qtys[i]);
                StoreProduct sp = storeProductDAO.getByUpc(upc);
                if (sp != null) {
                    BigDecimal lineTotal = sp.getSellingPrice().multiply(BigDecimal.valueOf(qty));
                    sumTotal = sumTotal.add(lineTotal);
                }
            }
        }

        // Застосовуємо знижку по картці
        if (discountRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = sumTotal.multiply(discountRate);
            sumTotal = sumTotal.subtract(discount);
        }

        BigDecimal vat = sumTotal.multiply(new BigDecimal("0.2")).setScale(2, RoundingMode.HALF_UP);
        sumTotal = sumTotal.setScale(2, RoundingMode.HALF_UP);

        c.setSumTotal(sumTotal);
        c.setVat(vat);
        return c;
    }

    private List<Sale> parseSalesFromRequest(HttpServletRequest request, Check check) {
        List<Sale> sales = new ArrayList<>();
        String[] upcs = request.getParameterValues("item_upc");
        String[] qtys = request.getParameterValues("item_qty");
        if (upcs == null) return sales;
        for (int i = 0; i < upcs.length; i++) {
            String upc = upcs[i].trim();
            if (upc.isEmpty()) continue;
            int qty = Integer.parseInt(qtys[i]);
            StoreProduct sp = storeProductDAO.getByUpc(upc);
            if (sp == null) throw new RuntimeException("Товар з UPC " + upc + " не знайдено");
            Sale sale = new Sale();
            sale.setUpc(upc);
            sale.setCheckNumber(check.getCheckNumber());
            sale.setProductNumber(qty);
            sale.setSellingPrice(sp.getSellingPrice());
            sales.add(sale);
        }
        return sales;
    }

    private void renderCreateCheckForm(HttpServletRequest request, HttpServletResponse response, String error) throws IOException {
        List<CustomerCard> cards = customerCardDAO.getAllCardsSorted();
        List<StoreProduct> storeProducts = storeProductDAO.searchStoreProducts(null, "name_asc");

        StringBuilder cardOptions = new StringBuilder("<option value=\"\">Без картки</option>");
        for (CustomerCard c : cards) {
            cardOptions.append("<option value=\"").append(HtmlPage.esc(c.getCardNumber())).append("\">")
                    .append(HtmlPage.esc(c.getCardNumber() + " — " + c.getCustSurname() + " " + c.getCustName()
                            + " (" + c.getPercent() + "% знижки)"))
                    .append("</option>");
        }

        StringBuilder productOptions = new StringBuilder();
        for (StoreProduct sp : storeProducts) {
            if (sp.getProductsNumber() > 0) {
                productOptions.append("<option value=\"").append(HtmlPage.esc(sp.getUpc())).append("\" data-price=\"")
                        .append(sp.getSellingPrice()).append("\">")
                        .append(HtmlPage.esc(sp.getUpc() + " — " + sp.getProductName()
                                + " (ціна: " + sp.getSellingPrice() + " грн, залишок: " + sp.getProductsNumber() + ")"))
                        .append("</option>");
            }
        }

        String errorHtml = error != null ? "<div class=\"alert alert-danger\">" + HtmlPage.esc(error) + "</div>" : "";
        String body = """
                %s
                <div class="card shadow-sm">
                  <div class="card-body">
                    <form method="post" action="checks" id="checkForm" class="row g-3">
                      <input type="hidden" name="action" value="add">
                      <div class="col-md-6">
                        <label class="form-label">Картка клієнта (необов'язково)</label>
                        <select class="form-select" name="card_number" id="cardSelect">%s</select>
                      </div>
                      <div class="col-12">
                        <label class="form-label fw-bold">Товари</label>
                        <div id="itemsContainer">
                          <div class="row g-2 mb-2 item-row">
                            <div class="col-md-7">
                              <select class="form-select" name="item_upc" required>
                                <option value="">Оберіть товар</option>
                                %s
                              </select>
                            </div>
                            <div class="col-md-3">
                              <input type="number" class="form-control" name="item_qty" value="1" min="1" placeholder="Кількість" required>
                            </div>
                            <div class="col-md-2">
                              <button type="button" class="btn btn-outline-danger w-100" onclick="this.closest('.item-row').remove(); updateTotal()">✕</button>
                            </div>
                          </div>
                        </div>
                        <button type="button" class="btn btn-outline-primary mt-2" onclick="addItem()">＋ Додати товар</button>
                      </div>
                      <div class="col-12">
                        <div class="alert alert-info">
                          <strong>Загальна сума:</strong> <span id="totalDisplay">0.00</span> грн
                          (ПДВ: <span id="vatDisplay">0.00</span> грн)
                        </div>
                      </div>
                      <div class="col-12 d-flex gap-2">
                        <button class="btn btn-success btn-lg" type="submit">🧾 Оформити чек</button>
                        <a class="btn btn-outline-secondary" href="checks">Скасувати</a>
                      </div>
                    </form>
                  </div>
                </div>
                <script>
                function addItem() {
                  const container = document.getElementById('itemsContainer');
                  const firstRow = container.querySelector('.item-row');
                  const newRow = firstRow.cloneNode(true);
                  newRow.querySelector('select').value = '';
                  newRow.querySelector('input').value = 1;
                  container.appendChild(newRow);
                }
                function updateTotal() {
                  let total = 0;
                  document.querySelectorAll('.item-row').forEach(row => {
                    const sel = row.querySelector('select[name="item_upc"]');
                    const qty = parseInt(row.querySelector('input[name="item_qty"]').value) || 0;
                    const opt = sel.options[sel.selectedIndex];
                    if (opt && opt.dataset.price) {
                      total += parseFloat(opt.dataset.price) * qty;
                    }
                  });
                  document.getElementById('totalDisplay').textContent = total.toFixed(2);
                  document.getElementById('vatDisplay').textContent = (total * 0.2).toFixed(2);
                }
                document.getElementById('itemsContainer').addEventListener('change', updateTotal);
                document.getElementById('itemsContainer').addEventListener('input', updateTotal);
                </script>
                """.formatted(errorHtml, cardOptions, productOptions);
        HtmlPage.render(response, "Новий чек", body, request.getContextPath() + "/checks");
    }

    private void renderDetail(HttpServletRequest request, HttpServletResponse response, String checkId) throws IOException {
        Check check = checkDAO.getById(checkId);
        List<Sale> sales = checkDAO.getSalesForCheck(checkId);
        StringBuilder rows = new StringBuilder();
        for (Sale sale : sales) {
            rows.append("<tr><td>").append(HtmlPage.esc(sale.getUpc())).append("</td><td>")
                    .append(HtmlPage.esc(sale.getProductName())).append("</td><td>")
                    .append(HtmlPage.esc(sale.getProductNumber())).append("</td><td>")
                    .append(HtmlPage.esc(sale.getSellingPrice())).append("</td><td>")
                    .append(HtmlPage.esc(sale.getSellingPrice().multiply(BigDecimal.valueOf(sale.getProductNumber())))).append("</td></tr>");
        }
        String printBtn = """
                <button class="btn btn-outline-secondary" onclick="window.print()"><i class="bi bi-printer me-1"></i>Друк</button>
                """;
        String body = """
                <div class="d-flex gap-2 mb-3 no-print">
                  %s
                </div>
                <div class="card shadow-sm mb-4" id="printArea">
                  <div class="card-body">
                    <h4 class="text-center mb-3">ЧЕКА № %s</h4>
                    <div class="row g-3">
                      <div class="col-md-4"><strong>Касир:</strong> %s</div>
                      <div class="col-md-4"><strong>Картка:</strong> %s</div>
                      <div class="col-md-4"><strong>Дата:</strong> %s</div>
                    </div>
                    <hr>
                    <table class="table table-bordered">
                      <thead><tr><th>UPC</th><th>Товар</th><th>К-сть</th><th>Ціна</th><th>Сума</th></tr></thead>
                      <tbody>%s</tbody>
                    </table>
                    <div class="text-end fw-bold">
                      <div>ПДВ (20%%): %s грн</div>
                      <div class="fs-5">До сплати: %s грн</div>
                    </div>
                  </div>
                </div>
                <style>@media print { .no-print { display: none; } }</style>
                """.formatted(
                printBtn,
                HtmlPage.esc(check == null ? "" : check.getCheckNumber()),
                HtmlPage.esc(check == null ? "" : check.getEmployeeName()),
                HtmlPage.esc(check == null ? "" : check.getCardOwner()),
                HtmlPage.esc(check == null ? "" : check.getPrintDate()),
                rows,
                HtmlPage.esc(check == null ? "" : check.getVat()),
                HtmlPage.esc(check == null ? "" : check.getSumTotal())
        );
        HtmlPage.render(response, "Деталі чеку", body, request.getContextPath() + "/checks");
    }

    private void renderStats(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String employeeId = blankToNull(request.getParameter("employeeId"));
        String dateFromStr = request.getParameter("dateFrom");
        String dateToStr   = request.getParameter("dateTo");
        String upc         = blankToNull(request.getParameter("upc"));

        Timestamp dateFrom = parseTimestampStart(dateFromStr);
        Timestamp dateTo   = parseTimestampEnd(dateToStr);

        BigDecimal totalSum  = checkDAO.getTotalSumByPeriod(employeeId, dateFrom, dateTo);
        int totalUnits = upc != null ? checkDAO.getTotalUnitsSoldByPeriod(upc, dateFrom, dateTo) : -1;

        List<Employee> cashiers = employeeDAO.getCashiersSorted();
        List<StoreProduct> products = storeProductDAO.getAllStoreProductsSortedByQty();

        StringBuilder cashierOptions = new StringBuilder("<option value=\"\">Усі касири</option>");
        for (Employee e : cashiers) {
            boolean sel = e.getIdEmployee().equals(employeeId);
            cashierOptions.append("<option value=\"").append(HtmlPage.esc(e.getIdEmployee())).append("\"")
                    .append(sel ? " selected" : "").append(">")
                    .append(HtmlPage.esc(e.getEmplSurname() + " " + e.getEmplName())).append("</option>");
        }

        StringBuilder productOptions = new StringBuilder("<option value=\"\">Оберіть товар</option>");
        for (StoreProduct sp : products) {
            boolean sel = sp.getUpc().equals(upc);
            productOptions.append("<option value=\"").append(HtmlPage.esc(sp.getUpc())).append("\"")
                    .append(sel ? " selected" : "").append(">")
                    .append(HtmlPage.esc(sp.getUpc() + " — " + sp.getProductName())).append("</option>");
        }

        String unitsResult = upc != null ? "<div class=\"alert alert-info\">Кількість одиниць товару <strong>" +
                HtmlPage.esc(upc) + "</strong> за вибраний період: <strong>" + totalUnits + "</strong></div>" : "";

        String body = """
                <div class="card shadow-sm mb-4">
                  <div class="card-header fw-bold">Аналітика продажів (Менеджер)</div>
                  <div class="card-body">
                    <form method="get" action="checks" class="row g-3">
                      <input type="hidden" name="action" value="stats">
                      <div class="col-md-4">
                        <label class="form-label">Касир</label>
                        <select class="form-select" name="employeeId">%s</select>
                      </div>
                      <div class="col-md-4">
                        <label class="form-label">Дата від</label>
                        <input type="date" class="form-control" name="dateFrom" value="%s">
                      </div>
                      <div class="col-md-4">
                        <label class="form-label">Дата до</label>
                        <input type="date" class="form-control" name="dateTo" value="%s">
                      </div>
                      <div class="col-md-6">
                        <label class="form-label">Товар (для підрахунку кількості)</label>
                        <select class="form-select" name="upc">%s</select>
                      </div>
                      <div class="col-12">
                        <button class="btn btn-primary" type="submit">Розрахувати</button>
                      </div>
                    </form>
                  </div>
                </div>
                <div class="alert alert-success fs-5">
                  Загальна сума продажів: <strong>%s грн</strong>
                </div>
                %s
                """.formatted(
                cashierOptions, nvl(dateFromStr), nvl(dateToStr), productOptions,
                totalSum.setScale(2, RoundingMode.HALF_UP), unitsResult);
        HtmlPage.render(response, "Аналітика продажів", body, request.getContextPath() + "/checks");
    }

    // -------------------------------------------------------

    private String getRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object role = session.getAttribute("userRole");
        return role != null ? role.toString() : null;
    }

    private Timestamp parseTimestampStart(String date) {
        if (date == null || date.isBlank()) return null;
        try { return Timestamp.valueOf(date + " 00:00:00"); } catch (Exception e) { return null; }
    }

    private Timestamp parseTimestampEnd(String date) {
        if (date == null || date.isBlank()) return null;
        try { return Timestamp.valueOf(date + " 23:59:59"); } catch (Exception e) { return null; }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}