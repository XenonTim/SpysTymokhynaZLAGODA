<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/print-report.js"></script>

<nav class="sticky-top custom-header py-2 mb-0">
  <div class="container-fluid px-4 d-flex align-items-center justify-content-between">
    <a href="${pageContext.request.contextPath}/index.jsp" class="text-decoration-none">
      <h2 class="mb-0 text-white">ZLAGODA</h2>
    </a>
    <ul class="nav align-items-center">
      <li class="nav-item me-2">
        <a href="${pageContext.request.contextPath}/index.jsp"
           class="nav-link header-icon-link px-2 fs-4" title="Profile">
          <i class="bi bi-person-circle"></i>
        </a>
      </li>
      <li class="nav-item">
        <a href="${pageContext.request.contextPath}/logout"
           class="nav-link logout-link fw-semibold px-3 py-2 d-flex align-items-center" title="Log-out">
          <i class="bi bi-box-arrow-right me-2"></i> Log-out
        </a>
      </li>
    </ul>
  </div>
</nav>
