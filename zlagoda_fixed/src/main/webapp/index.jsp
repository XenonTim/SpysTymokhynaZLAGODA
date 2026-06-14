<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>AIS ZLAGODA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body class="d-flex flex-column h-100">

<%@ include file="/WEB-INF/header.jsp" %>

<div class="d-flex flex-grow-1 align-items-stretch">

    <%@ include file="/WEB-INF/sidebar.jsp" %>

    <main class="flex-grow-1 p-4 bg-white">
        <c:choose>
            <c:when test="${param.page == 'categories'}">
                <jsp:include page="/WEB-INF/pages/category_page.jsp" />
            </c:when>
            <c:when test="${param.page == 'products'}">
                <jsp:include page="/WEB-INF/pages/product_page.jsp" />
            </c:when>
            <c:when test="${param.page == 'products_in_stock'}">
                <jsp:include page="/WEB-INF/pages/product_in_stock_page.jsp" />
            </c:when>
            <c:when test="${param.page == 'checks'}">
                <jsp:include page="/WEB-INF/pages/check_page.jsp" />
            </c:when>
            <c:when test="${param.page == 'sales'}">
                <jsp:include page="/WEB-INF/pages/sale_page.jsp" />
            </c:when>
            <c:when test="${param.page == 'clients'}">
                <jsp:include page="/WEB-INF/pages/client_page.jsp" />
            </c:when>
            <c:when test="${param.page == 'employees'}">
                <jsp:include page="/WEB-INF/pages/employee_page.jsp" />
            </c:when>
            <c:otherwise>
                <jsp:include page="/WEB-INF/pages/profile_page.jsp" />
            </c:otherwise>
        </c:choose>
    </main>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
