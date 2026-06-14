<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">

<%-- ===== ФОРМА ДОДАВАННЯ / РЕДАГУВАННЯ ===== --%>
<c:if test="${showForm == true}">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0">
      <c:choose>
        <c:when test="${not empty employeeToEdit.id_employee}">Редагування працівника</c:when>
        <c:otherwise>Новий працівник</c:otherwise>
      </c:choose>
    </h2>
    <a class="btn btn-outline-secondary" href="employees"><i class="bi bi-arrow-left me-1"></i> Назад</a>
  </div>

  <c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
  </c:if>

  <div class="card shadow-sm border-0">
    <div class="card-body">
      <form method="post" action="employees" class="row g-3">
        <c:choose>
          <c:when test="${not empty employeeToEdit.id_employee}">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id_employee" value="${employeeToEdit.id_employee}">
          </c:when>
          <c:otherwise>
            <input type="hidden" name="action" value="add">
          </c:otherwise>
        </c:choose>

        <%-- ID лише при додаванні, необов'язковий (якщо порожній — AUTO_INCREMENT) --%>
        <c:if test="${empty employeeToEdit.id_employee}">
          <div class="col-md-4">
            <label class="form-label">ID працівника <small class="text-muted">(необов'язково — якщо порожньо, генерується автоматично)</small></label>
            <input class="form-control" name="id_employee" placeholder="Наприклад: 111" value="">
          </div>
        </c:if>

        <div class="col-md-4">
          <label class="form-label">Прізвище *</label>
          <input class="form-control" name="empl_surname" required value="${employeeToEdit.empl_surname}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Ім'я *</label>
          <input class="form-control" name="empl_name" required value="${employeeToEdit.empl_name}">
        </div>
        <div class="col-md-4">
          <label class="form-label">По батькові</label>
          <input class="form-control" name="empl_patronymic" value="${employeeToEdit.empl_patronymic}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Посада *</label>
          <select class="form-select" name="empl_role" required>
            <option value="Менеджер" <c:if test="${employeeToEdit.empl_role == 'Менеджер'}">selected</c:if>>Менеджер</option>
            <option value="Касир"    <c:if test="${employeeToEdit.empl_role == 'Касир'}">selected</c:if>>Касир</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label">Зарплата (грн) *</label>
          <input class="form-control" name="salary" type="number" step="0.01" min="0" required value="${employeeToEdit.salary}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Дата народження *</label>
          <input class="form-control" name="date_of_birth" type="date" required value="${employeeToEdit.date_of_birth}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Дата початку роботи *</label>
          <input class="form-control" name="date_of_start" type="date" required value="${employeeToEdit.date_of_start}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Телефон (до 13 симв.)</label>
          <input class="form-control" name="phone_number" maxlength="13" placeholder="+380XXXXXXXXX" value="${employeeToEdit.phone_number}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Місто *</label>
          <input class="form-control" name="city" required value="${employeeToEdit.city}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Вулиця *</label>
          <input class="form-control" name="street" required value="${employeeToEdit.street}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Поштовий індекс *</label>
          <input class="form-control" name="zip_code" required value="${employeeToEdit.zip_code}">
        </div>
        <div class="col-md-4">
          <label class="form-label">Пароль
            <c:if test="${not empty employeeToEdit.id_employee}">
              <small class="text-muted">(порожньо = без змін)</small>
            </c:if>
          </label>
          <input class="form-control" name="password" type="password"
                 <c:if test="${empty employeeToEdit.id_employee}">required</c:if>>
        </div>
        <div class="col-12 d-flex gap-2 pt-2">
          <button class="btn btn-primary" type="submit"><i class="bi bi-floppy me-1"></i> Зберегти</button>
          <a class="btn btn-outline-secondary" href="employees">Скасувати</a>
        </div>
      </form>
    </div>
  </div>
</c:if>

<%-- ===== СПИСОК ПРАЦІВНИКІВ ===== --%>
<c:if test="${showForm != true}">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Працівники</h2>
    <div class="d-flex gap-2 no-print">
      <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center">
        <i class="bi bi-printer me-2"></i> Друк
      </button>
      <a href="employees?action=new" class="btn btn-success d-flex align-items-center">
        <i class="bi bi-person-plus me-2"></i> Додати
      </a>
    </div>
  </div>

  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="employees" method="GET" class="row g-3 align-items-end">
        <div class="col-md-4">
          <label class="form-label small text-muted">Пошук за прізвищем або ID</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control" placeholder="Прізвище, ID..." value="${param.searchQuery}">
          </div>
        </div>
        <div class="col-md-3">
          <label class="form-label small text-muted">Посада</label>
          <select name="role" class="form-select">
            <option value="">Усі посади</option>
            <option value="Менеджер" <c:if test="${roleFilter == 'Менеджер'}">selected</c:if>>Менеджер</option>
            <option value="Касир"    <c:if test="${roleFilter == 'Касир'}">selected</c:if>>Касир</option>
          </select>
        </div>
        <div class="col-md-3">
          <label class="form-label small text-muted">Сортування</label>
          <select name="sortBy" class="form-select">
            <option value="surname_asc">Прізвище (А-Я)</option>
            <option value="salary_desc">Зарплата (найбільша)</option>
            <option value="role">За посадою</option>
          </select>
        </div>
        <div class="col-auto">
          <button type="submit" class="btn btn-primary"><i class="bi bi-funnel me-1"></i> Застосувати</button>
          <a href="employees" class="btn btn-outline-secondary ms-1"><i class="bi bi-x-circle"></i></a>
        </div>
      </form>

      <hr class="my-3">
      <form action="employees" method="GET" class="row g-2 align-items-end">
        <input type="hidden" name="action" value="search_phone">
        <div class="col-md-5">
          <label class="form-label small text-muted">
            <i class="bi bi-telephone me-1"></i> Знайти телефон та адресу за прізвищем
          </label>
          <div class="input-group">
            <input type="text" name="surname" class="form-control" placeholder="Прізвище працівника" required value="${searchSurname}">
            <button type="submit" class="btn btn-outline-primary">Знайти</button>
          </div>
        </div>
      </form>
    </div>
  </div>

  <c:if test="${searchMode == 'phone'}">
    <div class="card shadow-sm border-0 mb-4 border-start border-primary border-3">
      <div class="card-body">
        <h6 class="text-primary mb-3"><i class="bi bi-telephone-fill me-2"></i>Телефон та адреса для прізвища «${searchSurname}»</h6>
        <c:choose>
          <c:when test="${not empty employeesList}">
            <table class="table table-sm table-bordered mb-0">
              <thead class="table-light"><tr><th>ПІБ</th><th>Посада</th><th>Телефон</th><th>Адреса</th></tr></thead>
              <tbody>
                <c:forEach var="emp" items="${employeesList}">
                  <tr>
                    <td>${emp.empl_surname} ${emp.empl_name} ${emp.empl_patronymic}</td>
                    <td><span class="badge bg-secondary">${emp.empl_role}</span></td>
                    <td><strong>${emp.phone_number}</strong></td>
                    <td>${emp.city}, ${emp.street}, ${emp.zip_code}</td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </c:when>
          <c:otherwise><p class="text-muted mb-0">Працівників з прізвищем «${searchSurname}» не знайдено.</p></c:otherwise>
        </c:choose>
      </div>
    </div>
  </c:if>

  <c:if test="${searchMode != 'phone'}">
    <div class="card shadow-sm border-0">
      <div class="table-responsive">
        <table class="table table-striped table-hover align-middle mb-0">
          <thead class="table-dark">
            <tr>
              <th>ID</th><th>ПІБ</th><th>Посада</th><th>Зарплата</th>
              <th>Дата нар.</th><th>Телефон</th><th>Адреса</th>
              <th class="text-end no-print">Дії</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="emp" items="${employeesList}">
              <tr>
                <td><strong>${emp.id_employee}</strong></td>
                <td>${emp.empl_surname} ${emp.empl_name}
                  <c:if test="${not empty emp.empl_patronymic}"> ${emp.empl_patronymic}</c:if>
                </td>
                <td>
                  <span class="badge ${emp.empl_role == 'Менеджер' ? 'bg-primary' : 'bg-secondary'}">
                    ${emp.empl_role}
                  </span>
                </td>
                <td>${emp.salary} грн</td>
                <td>${emp.date_of_birth}</td>
                <td>${emp.phone_number}</td>
                <td><span class="text-muted small">${emp.city}, ${emp.street}</span></td>
                <td class="text-end no-print">
                  <a href="employees?action=edit&id=${emp.id_employee}" class="btn btn-sm btn-outline-warning me-1" title="Редагувати"><i class="bi bi-pencil"></i></a>
                  <a href="employees?action=delete&id=${emp.id_employee}" class="btn btn-sm btn-outline-danger"
                     onclick="return confirm('Видалити працівника ${emp.empl_surname}?')" title="Видалити"><i class="bi bi-trash"></i></a>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty employeesList}">
              <tr><td colspan="8" class="text-center text-muted py-4"><i class="bi bi-people fs-3 d-block mb-2"></i>Працівників не знайдено</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </c:if>
</c:if>

</div>
