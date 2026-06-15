<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Checks</h2>
    <div class="d-flex gap-2 no-print">
      <button onclick="openPrintPreview('Checks', 'report-table')" class="btn btn-outline-secondary d-flex align-items-center">
        <i class="bi bi-printer me-2"></i> Report
      </button>
      
      <c:if test="${sessionScope.userRole == 'Manager'}">
        <a href="checks?action=stats" class="btn btn-outline-info d-flex align-items-center">
          <i class="bi bi-bar-chart me-2"></i> Analytics
        </a>
      </c:if>
      
      <c:if test="${sessionScope.userRole == 'Cashier'}">
        <a href="checks?action=new" class="btn btn-success d-flex align-items-center">
          <i class="bi bi-plus-circle me-2"></i> New check
        </a>
      </c:if>
    </div>
  </div>

  
  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="checks" method="GET" class="row g-3 align-items-end">

        <div class="col-md-4">
          <label class="form-label small text-muted">Search by check number</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control"
                   placeholder="Check number or cashier ID"
                   value="${param.searchQuery}">
          </div>
        </div>

        
        <c:if test="${sessionScope.userRole == 'Manager'}">
          <div class="col-md-3">
            <label class="form-label small text-muted">Cashier</label>
            <select name="employeeId" class="form-select">
              <option value="">All cashiers</option>
              <c:forEach var="emp" items="${employees}">
                <option value="${emp.id_employee}"
                  <c:if test="${emp.id_employee == selectedEmployee}">selected</c:if>>
                  ${emp.empl_surname} ${emp.empl_name}
                </option>
              </c:forEach>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted">Date from</label>
            <input type="date" class="form-control" name="dateFrom"
                   value="${dateFrom}">
          </div>
          <div class="col-md-2">
            <label class="form-label small text-muted">Date to</label>
            <input type="date" class="form-control" name="dateTo"
                   value="${dateTo}">
          </div>
        </c:if>

        <div class="col-md-3">
          <label class="form-label small text-muted">Sort by</label>
          <select name="sortBy" class="form-select">
            <option value="date_desc">Date (new first)</option>
            <option value="sum_desc">Total price (max first)</option>
            <option value="number">By check number</option>
          </select>
        </div>

        <div class="col-auto">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-funnel me-1"></i> Apply
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
      <table id="report-table" class="table table-striped table-hover align-middle mb-0">
        <thead class="table-dark">
          <tr>
            <th>Check №</th>
            <th>Cashier</th>
            <th>Client card</th>
            <th>Date and time</th>
            <th>VAT (20%)</th>
            <th>Total price</th>
            <th class="text-end no-print">Actions</th>
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
              <td class="text-muted">${ch.vat} UAH</td>
              <td class="fw-bold text-success">${ch.sum_total} UAH</td>
              <td class="text-end no-print">
                
                <a href="checks?action=view&id=${ch.check_number}"
                   class="btn btn-sm btn-outline-info me-1" title="View details">
                  <i class="bi bi-eye"></i>
                </a>
                
                <c:if test="${sessionScope.userRole == 'Manager'}">
                  <a href="checks?action=delete&id=${ch.check_number}"
                     class="btn btn-sm btn-outline-danger"
                     onclick="return confirm('Delete check #${ch.check_number}?')"
                     title="Delete">
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
                Checks not found
              </td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>
