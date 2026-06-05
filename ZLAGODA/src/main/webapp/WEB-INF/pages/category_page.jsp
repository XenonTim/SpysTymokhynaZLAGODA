<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Categories</h2>
    <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center no-print">
      <i class="bi bi-printer me-2"></i> Print
    </button>
  </div>

  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="categories" method="GET" class="row g-3 align-items-center">
        <div class="col-md-6">
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control" placeholder="Search by name or number">
            <button type="submit" class="btn btn-primary">Search</button>
          </div>
        </div>

        <div class="col-md-4">
          <div class="input-group">
            <label class="input-group-text bg-white" for="sortSelect"><i class="bi bi-sort-alpha-down"></i></label>
            <select name="sortBy" class="form-select" id="sortSelect" onchange="this.form.submit()">
              <option value="name_asc">Name (A-Z)</option>
              <option value="name_desc">Name (Z-A)</option>
              <option value="number">By Number</option>
            </select>
          </div>
        </div>

        <div class="col-md-2 text-md-end">
          <c:if test="${sessionScope.userRole == 'Менеджер'}">
            <a href="categories?action=new" class="btn btn-success w-100">
              <i class="bi bi-plus-circle me-1"></i> Add
            </a>
          </c:if>
        </div>
      </form>
    </div>
  </div>

  <div class="card shadow-sm border-0">
    <div class="table-responsive">
      <table class="table table-striped table-hover align-middle mb-0">
        <thead class="table-dark">
        <tr>
          <th>Category Number</th>
          <th>Category Name</th>
          <th class="text-end no-print">Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="cat" items="${categories}">
          <tr>
            <td><strong>${cat.category_number}</strong></td>
            <td>${cat.category_name}</td>
            <td class="text-end no-print">
              <c:choose>
                <c:when test="${sessionScope.userRole == 'Менеджер'}">
                  <a href="categories?action=edit&id=${cat.category_number}" class="btn btn-sm btn-outline-warning me-1" title="Edit"><i class="bi bi-pencil"></i></a>
                  <a href="categories?action=delete&id=${cat.category_number}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this category?')" title="Delete"><i class="bi bi-trash"></i></a>
                </c:when>
                <c:otherwise>
                  <button class="btn btn-sm btn-outline-secondary" disabled title="Not authorised"><i class="bi bi-lock"></i></button>
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
