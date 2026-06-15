<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container-fluid">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2 class="h3 mb-0 text-gray-800">Products in stock</h2>
    <div class="d-flex gap-2 no-print">
      <button onclick="openPrintPreview('Products in stock', 'report-table')" class="btn btn-outline-secondary d-flex align-items-center">
        <i class="bi bi-printer me-2"></i> Report
      </button>
      <c:if test="${sessionScope.userRole == 'Manager'}">
        <a href="store-products?action=new" class="btn btn-success d-flex align-items-center">
          <i class="bi bi-plus-circle me-2"></i> Add
        </a>
      </c:if>
    </div>
  </div>

  <div class="card shadow-sm border-0 mb-4 no-print bg-light">
    <div class="card-body py-3">
      <form action="store-products" method="GET" class="row g-3 align-items-end">

        <div class="col-md-4">
          <label class="form-label small text-muted">Search</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
            <input type="text" name="searchQuery" class="form-control"
                   placeholder="UPC, product name..."
                   value="${param.searchQuery}">
          </div>
        </div>

        
        <div class="col-md-3">
          <label class="form-label small text-muted">Product type</label>
          <select name="promo" class="form-select">
            <option value="">All products</option>
            <option value="true"  <c:if test="${promoFilter == 'true'}">selected</c:if>>
              Discounted products
            </option>
            <option value="false" <c:if test="${promoFilter == 'false'}">selected</c:if>>
              Non-discounted products
            </option>
          </select>
        </div>

        
        <div class="col-md-3">
          <label class="form-label small text-muted">Sort by</label>
          <select name="sortBy" class="form-select">
            <option value="name_asc">Name (A-Z)</option>
            <option value="name_desc">Name (Z-A)</option>
            <option value="number_desc">Amount (max-min)</option>
            <option value="number_asc">Amount (min-max)</option>
            <option value="price_asc">Price (min first)</option>
            <option value="upc">By UPC</option>
          </select>
        </div>

        <div class="col-auto">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-funnel me-1"></i> Apply
          </button>
          <a href="store-products" class="btn btn-outline-secondary ms-1">
            <i class="bi bi-x-circle"></i>
          </a>
        </div>

      </form>

      
      <hr class="my-3">
      <form action="store-products" method="GET" class="row g-2 align-items-end">
        <input type="hidden" name="action" value="lookup">
        <div class="col-md-4">
          <label class="form-label small text-muted">Search by UPC</label>
          <div class="input-group">
            <span class="input-group-text bg-white"><i class="bi bi-upc text-muted"></i></span>
            <input type="text" name="upc" class="form-control" placeholder="Enter product UPC" required>
            <button type="submit" class="btn btn-outline-primary">Search</button>
          </div>
        </div>
      </form>
    </div>
  </div>

  <div class="card shadow-sm border-0">
    <div class="table-responsive">
      <table id="report-table" class="table table-striped table-hover align-middle mb-0">
        <thead class="table-dark">
          <tr>
            <th>UPC</th>
            <th>Product name</th>
            <th>Selling price</th>
            <th>Amount</th>
            <th>Type</th>
            <th class="text-end no-print">Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="sprod" items="${storeProducts}">
            <tr>
              <td><strong>${sprod.UPC}</strong></td>
              <td>${sprod.product_name}</td>
              <td class="fw-bold">${sprod.selling_price} UAH</td>
              <td>
                <span class="${sprod.products_number == 0 ? 'text-danger fw-bold' : ''}">
                  ${sprod.products_number}
                </span>
              </td>
              <td>
                <c:choose>
                  <c:when test="${sprod.promotional_product}">
                    <span class="badge bg-danger">
                      <i class="bi bi-tags-fill me-1"></i>Discounted
                      <c:if test="${not empty sprod.UPC_prom}"> (promo: ${sprod.UPC_prom})</c:if>
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge bg-success">Non-discounted</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td class="text-end no-print">
                <c:choose>
                  <c:when test="${sessionScope.userRole == 'Manager'}">
                    <a href="store-products?action=edit&id=${sprod.UPC}"
                       class="btn btn-sm btn-outline-warning me-1" title="Edit">
                      <i class="bi bi-pencil"></i>
                    </a>
                    <a href="store-products?action=delete&id=${sprod.UPC}"
                       class="btn btn-sm btn-outline-danger"
                       onclick="return confirm('Delete product UPC ${sprod.UPC} from stock?')"
                       title="Delete">
                      <i class="bi bi-trash"></i>
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a href="store-products?action=lookup&upc=${sprod.UPC}"
                       class="btn btn-sm btn-outline-info" title="Product details">
                      <i class="bi bi-info-circle"></i>
                    </a>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty storeProducts}">
            <tr>
              <td colspan="6" class="text-center text-muted py-4">
                <i class="bi bi-box-seam fs-3 d-block mb-2"></i>
                Products not found
              </td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>
