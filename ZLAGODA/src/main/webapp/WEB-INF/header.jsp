<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link href="css/styles.css" rel="stylesheet">

<nav class="sticky-top custom-header py-2 mb-0">
  <div class="container-fluid px-4 d-flex align-items-center justify-content-between">

    <h2>
      ZLAGODA
    </h2>

    <ul class="nav align-items-center">
      <li class="nav-item me-2">
        <c:choose>
          <c:when test="${empty param.page || param.page == 'profile'}">
            <a href="${pageContext.request.contextPath}/index.jsp" class="nav-link header-icon-link active-profile px-2 fs-4" title="Профіль користувача (Поточна сторінка)">
              <i class="bi bi-person-circle"></i>
            </a>
          </c:when>
          <c:otherwise>
            <a href="${pageContext.request.contextPath}/index.jsp" class="nav-link header-icon-link px-2 fs-4" title="Профіль користувача">
              <i class="bi bi-person-circle"></i>
            </a>
          </c:otherwise>
        </c:choose>
      </li>

      <li class="nav-item">
        <a href="${pageContext.request.contextPath}/auth_page.jsp" class="nav-link logout-link fw-semibold px-3 py-2 d-flex align-items-center" title="Вийти з АІС">
          <i class="bi bi-box-arrow-right me-2"></i> Вихід
        </a>
      </li>
    </ul>

  </div>
</nav>