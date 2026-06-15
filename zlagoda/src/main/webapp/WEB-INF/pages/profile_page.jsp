<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container-fluid">
  <div class="row">
    <div class="col-md-8 mx-auto">
      <div class="card shadow-sm border-0 mt-3">
        <div class="card-header custom-header text-black py-3">
          <h4 class="mb-0 d-flex align-items-center">
            <i class="bi bi-person-lines-fill me-2"></i> Employee profile
          </h4>
        </div>
        <div class="card-body p-4">
          <div class="row align-items-center">
            <div class="col-md-3 text-center mb-3 mb-md-0">
              <i class="bi bi-person-square text-secondary" style="font-size: 6rem;"></i>
            </div>
            <div class="col-md-9">
              <h2 class="fw-bold text-dark">${user.empl_surname} ${user.empl_name} ${user.empl_patronymic}</h2>
              <p class="badge bg-success fs-6 mb-3">${user.role}</p>

              <hr>

              <div class="row g-3">
                <div class="col-sm-6">
                  <span class="text-muted d-block">Employee ID:</span>
                  <strong>${user.id_employee}</strong>
                </div>
                <div class="col-sm-6">
                  <span class="text-muted d-block">Salary:</span>
                  <strong>${user.salary} UAH</strong>
                </div>
                <div class="col-sm-6">
                  <span class="text-muted d-block">Phone number:</span>
                  <strong>${user.phone_number}</strong>
                </div>
                <div class="col-sm-6">
                  <span class="text-muted d-block">Address:</span>
                  <strong>${user.city}, ${user.street}, ${user.zip_code}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
