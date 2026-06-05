<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="h3 mb-0 text-gray-800">Client Cards</h2>
        <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center no-print">
            <i class="bi bi-printer me-2"></i> Print
        </button>
    </div>

    <div class="card shadow-sm border-0 mb-4 no-print bg-light">
        <div class="card-body py-3">
            <form action="customers" method="GET" class="row g-3 align-items-center">
                <div class="col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
                        <input type="text" name="searchQuery" class="form-control" placeholder="Search by surname or card number">
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="input-group">
                        <label class="input-group-text bg-white" for="sortSelect"><i class="bi bi-sort-numeric-down"></i></label>
                        <select name="sortBy" class="form-select" id="sortSelect" onchange="this.form.submit()">
                            <option value="surname_asc">Surname (A-Z)</option>
                            <option value="percent_desc">Highest Percent</option>
                            <option value="card_number">By Card Number</option>
                        </select>
                    </div>
                </div>

                <div class="col-md-2 text-md-end">
                    <c:if test="${sessionScope.userRole == 'Менеджер'}">
                        <a href="customers?action=new" class="btn btn-success w-100">
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
                    <th>Card Number</th>
                    <th>Full Name</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Percent</th>
                    <th class="text-end no-print">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="cust" items="${customers}">
                    <tr>
                        <td><strong>${cust.card_number}</strong></td>
                        <td>${cust.cust_surname} ${cust.cust_name} ${cust.cust_patronymic}</td>
                        <td>${cust.phone_number}</td>
                        <td><span class="text-muted small">${cust.city}, ${cust.street}, ${cust.zip_code}</span></td>
                        <td><span class="badge bg-info text-dark">${cust.percent}%</span></td>
                        <td class="text-end no-print">
                            <c:choose>
                                <c:when test="${sessionScope.userRole == 'Менеджер'}">
                                    <a href="customers?action=edit&id=${cust.card_number}" class="btn btn-sm btn-outline-warning me-1" title="Edit"><i class="bi bi-pencil"></i></a>
                                    <a href="customers?action=delete&id=${cust.card_number}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this card?')" title="Delete"><i class="bi bi-trash"></i></a>
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