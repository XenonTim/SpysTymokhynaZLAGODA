<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Чеки</h2>
    <div class="d-flex gap-2 no-print">
      <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center">
        <i class="bi bi-printer me-2"></i> Друк
      </button>
      <%-- Статистика доступна лише менеджеру (вимоги п.19, п.20, п.21) --%>
      <c:if test="${sessionScope.userRole == 'Менеджер'}">
        <a href="checks?action=stats" class="btn btn-outline-info d-flex align-items-center">
          <i class="bi bi-bar-chart me-2"></i> Аналітика
        </a>
      </c:if>
      <%-- Новий чек — лише касир (вимога: «Касир відповідає за здійснення продажу товарів») --%>
      <c:if test="${sessionScope.userRole == 'Касир'}">
        <a href="checks?action=new" class="btn btn-success d-flex align-items-center">
          <i class="bi bi-plus-circle me-2"></i> Новий чек
        </a>
      </c:if>
    </div>
  </div>

  <%-- Фільтри — менеджер може фільтрувати по касиру та датах (вимоги п.17, п.18) --%>
  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="checks" method="GET" class="row g-3 align-items-end">

        <div class="col-md-4">
          <label class="form-label small text-muted">Пошук за номером чеку</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control"
                   placeholder="Номер чеку або ID касира"
                   value="${param.searchQuery}">
          </div>
        </div>

        <%-- Фільтр по касиру — тільки менеджер (вимога п.17, п.18) --%>
        <c:if test="${sessionScope.userRole == 'Менеджер'}">
          <div class="col-md-3">
            <label class="form-label small text-muted">Касир</label>
            <select name="employeeId" class="form-select">
              <option value="">Усі касири</option>
              <c:forEach var="emp" items="${employees}">
                <option value="${emp.id_employee}"
                  <c:if test="${emp.id_employee == selectedEmployee}">selected</c:if>>
                  ${emp.empl_surname} ${emp.empl_name}
                </option>
              </c:forEach>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted">Дата від</label>
            <input type="date" class="form-control" name="dateFrom"
                   value="${dateFrom}">
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted">Дата до</label>
            <input type="date" class="form-control" name="dateTo"
                   value="${dateTo}">
          </div>
        </c:if>

        <div class="col-md-3">
          <label class="form-label small text-muted">Сортування</label>
          <select name="sortBy" class="form-select">
            <option value="date_desc">Дата (нові перші)</option>
            <option value="sum_desc">Сума (найбільша)</option>
            <option value="number">За номером чеку</option>
          </select>
        </div>

        <div class="col-auto">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-funnel me-1"></i> Застосувати
          </button>
          <a href="checks" class="btn btn-outline-secondary ms-1">
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
            <th>Чек №</th>
            <th>Касир</th>
            <th>Картка клієнта</th>
            <th>Дата і час</th>
            <th>ПДВ (20%)</th>
            <th>Загальна сума</th>
            <th class="text-end no-print">Дії</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="ch" items="${checks}">
            <tr>
              <td><strong>${ch.check_number}</strong></td>
              <td>
                <c:choose>
                  <c:when test="${not empty ch.employeeName}">${ch.employeeName}</c:when>
                  <c:otherwise><span class="text-muted">#${ch.id_employee}</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${not empty ch.card_number}">
                    <span class="badge bg-info text-dark">
                      <c:choose>
                        <c:when test="${not empty ch.cardOwner}">${ch.cardOwner}</c:when>
                        <c:otherwise>#${ch.card_number}</c:otherwise>
                      </c:choose>
                    </span>
                  </c:when>
                  <c:otherwise><span class="text-muted small">—</span></c:otherwise>
                </c:choose>
              </td>
              <td>${ch.print_date}</td>
              <td class="text-muted">${ch.vat} грн</td>
              <td class="fw-bold text-success">${ch.sum_total} грн</td>
              <td class="text-end no-print">
                <%-- Перегляд деталей доступний всім авторизованим (вимога касира п.11) --%>
                <a href="checks?action=view&id=${ch.check_number}"
                   class="btn btn-sm btn-outline-info me-1" title="Переглянути деталі">
                  <i class="bi bi-eye"></i>
                </a>
                <%-- Видалення лише менеджер (вимога: «Всі права на вилучення даних надаються менеджеру») --%>
                <c:if test="${sessionScope.userRole == 'Менеджер'}">
                  <a href="checks?action=delete&id=${ch.check_number}"
                     class="btn btn-sm btn-outline-danger"
                     onclick="return confirm('Видалити чек #${ch.check_number}?')"
                     title="Видалити">
                    <i class="bi bi-trash"></i>
                  </a>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty checks}">
            <tr>
              <td colspan="7" class="text-center text-muted py-4">
                <i class="bi bi-inbox fs-3 d-block mb-2"></i>
                Чеків не знайдено
              </td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>
