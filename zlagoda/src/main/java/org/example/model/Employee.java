package org.example.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Employee {
    private String idEmployee;
    private String emplSurname;
    private String emplName;
    private String patronymic;
    private String emplRole;
    private BigDecimal salary;
    private Date dateOfBirth;
    private Date dateOfStart;
    private String phoneNumber;
    private String city;
    private String street;
    private String zipCode;
    private String passwordHash;

    public String getIdEmployee() { return idEmployee; }
    public void setIdEmployee(String idEmployee) { this.idEmployee = idEmployee; }
    public String getEmplSurname() { return emplSurname; }
    public void setEmplSurname(String emplSurname) { this.emplSurname = emplSurname; }
    public String getEmplName() { return emplName; }
    public void setEmplName(String emplName) { this.emplName = emplName; }
    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }
    public String getEmplRole() { return emplRole; }
    public void setEmplRole(String emplRole) { this.emplRole = emplRole; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Date getDateOfStart() { return dateOfStart; }
    public void setDateOfStart(Date dateOfStart) { this.dateOfStart = dateOfStart; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getId_employee() { return getIdEmployee(); }
    public void setId_employee(String id_employee) { setIdEmployee(id_employee); }
    public String getEmpl_surname() { return getEmplSurname(); }
    public void setEmpl_surname(String empl_surname) { setEmplSurname(empl_surname); }
    public String getEmpl_name() { return getEmplName(); }
    public void setEmpl_name(String empl_name) { setEmplName(empl_name); }
    public String getEmpl_patronymic() { return getPatronymic(); }
    public void setEmpl_patronymic(String empl_patronymic) { setPatronymic(empl_patronymic); }
    public String getEmpl_role() { return getEmplRole(); }
    public void setEmpl_role(String empl_role) { setEmplRole(empl_role); }
    public BigDecimal getSalary_amount() { return getSalary(); }
    public void setSalary_amount(BigDecimal salary_amount) { setSalary(salary_amount); }
    public Date getDate_of_birth() { return getDateOfBirth(); }
    public void setDate_of_birth(Date date_of_birth) { setDateOfBirth(date_of_birth); }
    public Date getDate_of_start() { return getDateOfStart(); }
    public void setDate_of_start(Date date_of_start) { setDateOfStart(date_of_start); }
    public String getPhone_number() { return getPhoneNumber(); }
    public void setPhone_number(String phone_number) { setPhoneNumber(phone_number); }
    public String getCity_name() { return getCity(); }
    public void setCity_name(String city_name) { setCity(city_name); }
    public String getStreet_name() { return getStreet(); }
    public void setStreet_name(String street_name) { setStreet(street_name); }
    public String getZip_code() { return getZipCode(); }
    public void setZip_code(String zip_code) { setZipCode(zip_code); }
    public String getRole() { return getEmplRole(); }
    public void setRole(String role) { setEmplRole(role); }
}
