<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link href="css/styles.css" rel="stylesheet">

<div class="d-flex flex-column flex-shrink-0 p-3 custom-sidebar border-end"
     style="width: 280px; height: calc(100vh - 90px); position: fixed; left: 0; top: 50px; overflow-y: auto; z-index: 1020;">

  <ul class="nav nav-pills flex-column mb-auto">

    <li class="mb-2">
      <button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 fw-semibold text-dark w-100 text-start justify-content-between px-2 py-2"
              data-bs-toggle="collapse"
              data-bs-target="#product-collapse"
              aria-expanded="true">
        <span><i class="bi bi-box-seam me-2"></i>Products</span>
        <i class="bi bi-chevron-down small text-muted"></i>
      </button>

      <div class="collapse show mt-1" id="product-collapse">
        <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small ps-4">
          <li class="mb-1">
            <a href="${pageContext.request.contextPath}/categories"
               class="nav-link link-dark rounded py-1">
              <i class="bi bi-tag me-1"></i> Categories
            </a>
          </li>
          <li class="mb-1">
            <a href="${pageContext.request.contextPath}/products"
               class="nav-link link-dark rounded py-1">
              <i class="bi bi-archive me-1"></i> Products
            </a>
          </li>
          <li class="mb-1">
            <a href="${pageContext.request.contextPath}/store-products"
               class="nav-link link-dark rounded py-1">
              <i class="bi bi-shop me-1"></i> Products in stock
            </a>
          </li>
        </ul>
      </div>
    </li>

    <li class="nav-item mb-1">
      <a href="${pageContext.request.contextPath}/checks"
         class="nav-link link-dark d-flex align-items-center py-2">
        <i class="bi bi-receipt me-2"></i> Checks
      </a>
    </li>

    <c:if test="${sessionScope.userRole == 'Manager'}">
      <li class="nav-item mb-1">
        <a href="${pageContext.request.contextPath}/sales"
           class="nav-link link-dark d-flex align-items-center py-2">
          <i class="bi bi-table me-2"></i> Sales
        </a>
      </li>
    </c:if>

    <li class="nav-item mb-1">
      <a href="${pageContext.request.contextPath}/customers"
         class="nav-link link-dark d-flex align-items-center py-2">
        <i class="bi bi-people me-2"></i> Clients
      </a>
    </li>

    <c:if test="${sessionScope.userRole == 'Manager'}">
      <li class="nav-item mb-1">
        <a href="${pageContext.request.contextPath}/employees"
           class="nav-link link-dark d-flex align-items-center py-2">
          <i class="bi bi-person-badge me-2"></i> Employees
        </a>
      </li>
    </c:if>

  </ul>

  <div class="mt-auto pt-3 border-top">
    <div class="d-flex align-items-center gap-2">
      <i class="bi bi-person-circle fs-4 text-muted"></i>
      <div class="small">
        <div class="fw-semibold">${sessionScope.user.empl_surname} ${sessionScope.user.empl_name}</div>
        <div class="text-muted">${sessionScope.userRole}</div>
      </div>
    </div>
  </div>
</div>
