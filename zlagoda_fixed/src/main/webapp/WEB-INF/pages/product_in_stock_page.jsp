<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Товари у магазині</h2>
    <div class="d-flex gap-2 no-print">
      <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center">
        <i class="bi bi-printer me-2"></i> Друк
      </button>
      <c:if test="${sessionScope.userRole == 'Менеджер'}">
        <a href="store-products?action=new" class="btn btn-success d-flex align-items-center">
          <i class="bi bi-plus-circle me-2"></i> Додати
        </a>
      </c:if>
    </div>
  </div>

  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="store-products" method="GET" class="row g-3 align-items-end">

        <div class="col-md-4">
          <label class="form-label small text-muted">Пошук</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control"
                   placeholder="UPC, назва товару..."
                   value="${param.searchQuery}">
          </div>
        </div>

        <%-- Фільтр акційні/неакційні (вимога менеджера п.15, п.16; касира п.12, п.13) --%>
        <div class="col-md-3">
          <label class="form-label small text-muted">Тип товару</label>
          <select name="promo" class="form-select">
            <option value="">Всі товари</option>
            <option value="true"  <c:if test="${promoFilter == 'true'}">selected</c:if>>
              Акційні товари
            </option>
            <option value="false" <c:if test="${promoFilter == 'false'}">selected</c:if>>
              Звичайні товари
            </option>
          </select>
        </div>

        <%-- Сортування по кількості АБО по назві (вимога менеджера п.15, п.16; касира п.12, п.13) --%>
        <div class="col-md-3">
          <label class="form-label small text-muted">Сортування</label>
          <select name="sortBy" class="form-select">
            <option value="name_asc">Назва (А-Я)</option>
            <option value="name_desc">Назва (Я-А)</option>
            <option value="number_desc">Кількість (спадно)</option>
            <option value="number_asc">Кількість (зростання)</option>
            <option value="price_asc">Ціна (зростання)</option>
            <option value="upc">За UPC</option>
          </select>
        </div>

        <div class="col-auto">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-funnel me-1"></i> Застосувати
          </button>
          <a href="store-products" class="btn btn-outline-secondary ms-1">
            <i class="bi bi-x-circle"></i>
          </a>
        </div>

      </form>

      <%-- Швидкий пошук за UPC (вимога менеджера п.14; касира п.14) --%>
      <hr class="my-3">
      <form action="store-products" method="GET" class="row g-2 align-items-end">
        <input type="hidden" name="action" value="lookup">
        <div class="col-md-4">
          <label class="form-label small text-muted">Пошук за UPC — ціна, кількість, характеристики</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-upc text-muted"></i></span>
            <input type="text" name="upc" class="form-control" placeholder="Введіть UPC товару" required>
            <button type="submit" class="btn btn-outline-primary">Знайти</button>
          </div>
        </div>
      </form>
    </div>
  </div>

  <div class="card shadow-sm border-0">
    <div class="table-responsive">
      <table class="table table-striped table-hover align-middle mb-0">
        <thead class="table-dark">
          <tr>
            <th>UPC</th>
            <th>Назва товару</th>
            <th>Ціна продажу</th>
            <th>Кількість</th>
            <th>Статус</th>
            <th class="text-end no-print">Дії</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="sprod" items="${storeProducts}">
            <tr>
              <td><strong>${sprod.UPC}</strong></td>
              <td>${sprod.product_name}</td>
              <td class="fw-bold">${sprod.selling_price} грн</td>
              <td>
                <span class="${sprod.products_number == 0 ? 'text-danger fw-bold' : ''}">
                  ${sprod.products_number} шт.
                </span>
              </td>
              <td>
                <c:choose>
                  <c:when test="${sprod.promotional_product}">
                    <span class="badge bg-danger">
                      <i class="bi bi-tags-fill me-1"></i>Акційний
                      <c:if test="${not empty sprod.UPC_prom}"> (пром: ${sprod.UPC_prom})</c:if>
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge bg-success">Звичайний</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="text-end no-print">
                <c:choose>
                  <c:when test="${sessionScope.userRole == 'Менеджер'}">
                    <a href="store-products?action=edit&id=${sprod.UPC}"
                       class="btn btn-sm btn-outline-warning me-1" title="Редагувати">
                      <i class="bi bi-pencil"></i>
                    </a>
                    <a href="store-products?action=delete&id=${sprod.UPC}"
                       class="btn btn-sm btn-outline-danger"
                       onclick="return confirm('Видалити товар UPC ${sprod.UPC} зі складу?')"
                       title="Видалити">
                      <i class="bi bi-trash"></i>
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a href="store-products?action=lookup&upc=${sprod.UPC}"
                       class="btn btn-sm btn-outline-info" title="Деталі товару">
                      <i class="bi bi-info-circle"></i>
                    </a>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty storeProducts}">
            <tr>
              <td colspan="6" class="text-center text-muted py-4">
                <i class="bi bi-box-seam fs-3 d-block mb-2"></i>
                Товарів не знайдено
              </td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>
