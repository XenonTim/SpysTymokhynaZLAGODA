<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="h3 mb-0 text-gray-800">Employees</h2>
        <button onclick="window.print()" class="btn btn-outline-secondary d-flex align-items-center no-print">
            <i class="bi bi-printer me-2"></i> Print
        </button>
    </div>

    <div class="card shadow-sm border-0 mb-4 no-print bg-light">
        <div class="card-body py-3">
            <form action="employees" method="GET" class="row g-3 align-items-center">
                <div class="col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-white"><i class="bi bi-search text-muted"></i></span>
                        <input type="text" name="searchQuery" class="form-control" placeholder="Search by surname or ID">
                        <button type="submit" class="btn btn-primary">Search</button>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="input-group">
                        <label class="input-group-text bg-white" for="sortSelect"><i class="bi bi-sort-alpha-down"></i></label>
                        <select name="sortBy" class="form-select" id="sortSelect" onchange="this.form.submit()">
                            <option value="surname_asc">Surname (A-Z)</option>
                            <option value="salary_desc">Highest Salary</option>
                            <option value="role">By Role</option>
                        </select>
                    </div>
                </div>

                <div class="col-md-2 text-md-end">
                    <c:if test="${sessionScope.userRole == 'Менеджер'}">
                        <a href="employees?action=new" class="btn btn-success w-100">
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
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Role</th>
                    <th>Salary</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th class="text-end no-print">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="emp" items="${employees}">
                    <tr>
                        <td><strong>${emp.id_employee}</strong></td>
                        <td>${emp.empl_surname} ${emp.empl_name} ${emp.empl_patronymic}</td>
                        <td><span class="badge bg-secondary">${emp.empl_role}</span></td>
                        <td>${emp.salary} UAH</td>
                        <td>${emp.phone_number}</td>
                        <td><span class="text-muted small">${emp.city}, ${emp.street}</span></td>
                        <td class="text-end no-print">
                            <c:choose>
                                <c:when test="${sessionScope.userRole == 'Менеджер'}">
                                    <a href="employees?action=edit&id=${emp.id_employee}" class="btn btn-sm btn-outline-warning me-1" title="Edit"><i class="bi bi-pencil"></i></a>
                                    <a href="employees?action=delete&id=${emp.id_employee}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this employee?')" title="Delete"><i class="bi bi-trash"></i></a>
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
