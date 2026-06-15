<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Client cards</h2>
    <div class="d-flex gap-2 no-print">
      <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center">
        <i class="bi bi-printer me-2"></i> Print
      </button>
      <%-- Додавання картки: і менеджер, і касир. --%>
      <a href="customers?action=new" class="btn btn-success d-flex align-items-center">
        <i class="bi bi-plus-circle me-2"></i> Add
      </a>
    </div>
  </div>

  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="customers" method="GET" class="row g-3 align-items-end">

        <%-- Пошук за прізвищем. --%>
        <div class="col-md-4">
          <label class="form-label small text-muted">Search by surname or card number</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control"
                   placeholder="Surname or Card number"
                   value="${param.searchQuery}">
          </div>
        </div>

        <%-- Фільтр по відсотку — лише менеджер. --%>
        <c:if test="${sessionScope.userRole == 'Manager'}">
          <div class="col-md-3">
            <label class="form-label small text-muted">Discount percentage</label>
            <div class="input-group">
              <input type="number" name="percent" class="form-control"
                     placeholder="e.g. 5" min="0" max="100"
                     value="${percentFilter}">
              <span class="input-group-text">%</span>
            </div>
          </div>
        </c:if>

        <div class="col-md-3">
          <label class="form-label small text-muted">Sort by</label>
          <select name="sortBy" class="form-select">
            <option value="surname_asc">Surname (A-Z)</option>
            <option value="percent_desc">Max percentage</option>
            <option value="card_number">Card number</option>
          </select>
        </div>

        <div class="col-auto">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-funnel me-1"></i> Apply
          </button>
          <a href="customers" class="btn btn-outline-secondary ms-1">
            <i class="bi bi-x-circle"></i>
          </a>
        </div>
      </form>
    </div>
  </div>

  <div class="card shadow-sm border-0">
    <div class="table-responsive">
      <table class="table table-striped table-hover align-middle mb-0">
        <thead class="table-dark">
          <tr>
            <th>Card number</th>
            <th>Full name</th>
            <th>Phone number</th>
            <th>Address</th>
            <th>Discount</th>
            <th class="text-end no-print">Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="cust" items="${customers}">
            <tr>
              <td><strong>${cust.card_number}</strong></td>
              <td>${cust.cust_surname} ${cust.cust_name}
                <c:if test="${not empty cust.cust_patronymic}"> ${cust.cust_patronymic}</c:if>
              </td>
              <td>${cust.phone_number}</td>
              <td>
                <span class="text-muted small">
                  <c:if test="${not empty cust.city}">${cust.city}, </c:if>
                  <c:if test="${not empty cust.street}">${cust.street}</c:if>
                  <c:if test="${not empty cust.zip_code}">, ${cust.zip_code}</c:if>
                </span>
              </td>
              <td><span class="badge bg-info text-dark">${cust.percent}%</span></td>
              <td class="text-end no-print">
                <%-- Редагування: і менеджер, і касир. --%>
                <a href="customers?action=edit&id=${cust.card_number}"
                   class="btn btn-sm btn-outline-warning me-1" title="Редагувати">
                  <i class="bi bi-pencil"></i>
                </a>
                <%-- Видалення — лише менеджер. --%>
                <c:if test="${sessionScope.userRole == 'Manager'}">
                  <a href="customers?action=delete&id=${cust.card_number}"
                     class="btn btn-sm btn-outline-danger"
                     onclick="return confirm('Delete card number #${cust.card_number}?')"
                     title="Delete">
                    <i class="bi bi-trash"></i>
                  </a>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty customers}">
            <tr>
              <td colspan="6" class="text-center text-muted py-4">
                <i class="bi bi-people fs-3 d-block mb-2"></i>
                Clients not found
              </td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>
