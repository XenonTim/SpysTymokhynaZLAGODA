<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Checks</h2>
    <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center no-print">
      <i class="bi bi-printer me-2"></i> Print
    </button>
  </div>

  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="checks" method="GET" class="row g-3 align-items-center">
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control" placeholder="Search by check number or employee ID">
            <button type="submit" class="btn btn-primary">Search</button>
          </div>
        </div>

        <div class="col-md-4">
          <div class="input-group">
            <label class="input-group-text bg-white" for="sortSelect"><i class="bi bi-sort-numeric-down"></i></label>
            <select name="sortBy" class="form-select" id="sortSelect" onchange="this.form.submit()">
              <option value="date_desc">Date (Newest First)</option>
              <option value="sum_desc">Total Sum (Highest)</option>
              <option value="number">By Check Number</option>
            </select>
          </div>
        </div>

        <div class="col-md-2 text-md-end">
          <a href="checks?action=new" class="btn btn-success w-100">
            <i class="bi bi-plus-circle me-1"></i> New Check
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
          <th>Check №</th>
          <th>Employee ID</th>
          <th>Card Number</th>
          <th>Print Date</th>
          <th>VAT (20%)</th>
          <th>Total Sum</th>
          <th class="text-end no-print">Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="ch" items="${checks}">
          <tr>
            <td><strong>${ch.check_number}</strong></td>
            <td>Employee #${ch.id_employee}</td>
            <td>
              <c:choose>
                <c:when test="${ch.card_number != null}">${ch.card_number}</c:when>
                <c:otherwise><span class="text-muted small">None</span></c:otherwise>
              </c:choose>
            </td>
            <td>${ch.print_date}</td>
            <td>${ch.vat} UAH</td>
            <td class="fw-bold text-success">${ch.sum_total} UAH</td>
            <td class="text-end no-print">
              <c:choose>
                <c:when test="${sessionScope.userRole == 'Менеджер'}">
                  <a href="checks?action=edit&id=${ch.check_number}" class="btn btn-sm btn-outline-warning me-1" title="Edit"><i class="bi bi-pencil"></i></a>
                  <a href="checks?action=delete&id=${ch.check_number}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this check record?')" title="Delete"><i class="bi bi-trash"></i></a>
                </c:when>
                <c:otherwise>
                  <a href="checks?action=view&id=${ch.check_number}" class="btn btn-sm btn-outline-info" title="View Details"><i class="bi bi-eye"></i></a>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>
