<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link href="css/styles.css" rel="stylesheet">

<div class="d-flex flex-column flex-shrink-0 p-3 custom-sidebar border-end"
     style="width: 280px; min-height: calc(100vh - 90px);">

  <ul class="nav nav-pills flex-column mb-auto">

    <%-- Товари (Products) — доступно всім --%>
    <li class="mb-2">
      <button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 fw-semibold text-dark w-100 text-start justify-content-between px-2 py-2"
              data-bs-toggle="collapse"
              data-bs-target="#product-collapse"
              aria-expanded="true">
        <span><i class="bi bi-box-seam me-2"></i>Товари</span>
        <i class="bi bi-chevron-down small text-muted"></i>
      </button>

      <div class="collapse show mt-1" id="product-collapse">
        <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small ps-4">
          <li class="mb-1">
            <a href="${pageContext.request.contextPath}/categories"
               class="nav-link link-dark rounded py-1">
              <i class="bi bi-tag me-1"></i> Категорії
            </a>
          </li>
          <li class="mb-1">
            <a href="${pageContext.request.contextPath}/products"
               class="nav-link link-dark rounded py-1">
              <i class="bi bi-archive me-1"></i> Товари
            </a>
          </li>
          <li class="mb-1">
            <a href="${pageContext.request.contextPath}/store-products"
               class="nav-link link-dark rounded py-1">
              <i class="bi bi-shop me-1"></i> Товари у магазині
            </a>
          </li>
        </ul>
      </div>
    </li>

    <%-- Чеки — доступно всім --%>
    <li class="nav-item mb-1">
      <a href="${pageContext.request.contextPath}/checks"
         class="nav-link link-dark d-flex align-items-center py-2">
        <i class="bi bi-receipt me-2"></i> Чеки
      </a>
    </li>

    <%-- Позиції продажів — ТІЛЬКИ менеджер (технічна/адмін сторінка, не для касира) --%>
    <c:if test="${sessionScope.userRole == 'Менеджер'}">
      <li class="nav-item mb-1">
        <a href="${pageContext.request.contextPath}/sales"
           class="nav-link link-dark d-flex align-items-center py-2">
          <i class="bi bi-table me-2"></i> Позиції продажів
        </a>
      </li>
    </c:if>

    <%-- Клієнти — доступно всім --%>
    <li class="nav-item mb-1">
      <a href="${pageContext.request.contextPath}/customers"
         class="nav-link link-dark d-flex align-items-center py-2">
        <i class="bi bi-people me-2"></i> Клієнти
      </a>
    </li>

    <%-- Працівники — ТІЛЬКИ менеджер --%>
    <c:if test="${sessionScope.userRole == 'Менеджер'}">
      <li class="nav-item mb-1">
        <a href="${pageContext.request.contextPath}/employees"
           class="nav-link link-dark d-flex align-items-center py-2">
          <i class="bi bi-person-badge me-2"></i> Працівники
        </a>
      </li>
    </c:if>

  </ul>

  <%-- Інформація про поточного користувача внизу сайдбару --%>
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
