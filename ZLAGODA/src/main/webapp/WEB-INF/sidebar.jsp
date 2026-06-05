<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<link href="css/styles.css" rel="stylesheet">

<div class="d-flex flex-column flex-shrink-0 p-3 custom-sidebar border-end" style="width: 280px; min-height: calc(100vh - 90px);">

    <ul class="nav nav-pills flex-column mb-auto">

        <li class="mb-2">
            <button class="btn btn-toggle d-inline-flex align-items-center rounded border-0 fw-semibold text-dark w-100 text-start justify-content-between px-2 py-2"
                    data-bs-toggle="collapse"
                    data-bs-target="#product-collapse"
                    aria-expanded="true">
                <span>Products</span>
                <i class="bi bi-chevron-down small text-muted"></i>
            </button>

            <div class="collapse show mt-1" id="product-collapse">
                <ul class="btn-toggle-nav list-unstyled fw-normal pb-1 small ps-4">
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/index.jsp?page=categories" class="nav-link link-dark rounded py-1">Category</a>
                    </li>
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/index.jsp?page=products" class="nav-link link-dark rounded py-1">Product</a>
                    </li>
                    <li class="mb-1">
                        <a href="${pageContext.request.contextPath}/index.jsp?page=products_in_stock" class="nav-link link-dark rounded py-1">Product in stock</a>
                    </li>
                </ul>
            </div>
        </li>

        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/index.jsp?page=checks" class="nav-link link-dark d-flex align-items-center py-2">
                Checks
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/index.jsp?page=sales" class="nav-link link-dark d-flex align-items-center py-2">
                Sales
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/index.jsp?page=clients" class="nav-link link-dark d-flex align-items-center py-2">
                Clients
            </a>
        </li>
        <li class="nav-item mb-1">
            <a href="${pageContext.request.contextPath}/index.jsp?page=employees" class="nav-link link-dark d-flex align-items-center py-2">
                Employees
            </a>
        </li>
    </ul>
</div>