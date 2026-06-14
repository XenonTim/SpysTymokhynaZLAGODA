package org.example.model;

public class CustomerCard {
    private String cardNumber;
    private String custSurname;
    private String custName;
    private String custPatronymic;
    private String phoneNumber;
    private String city;
    private String street;
    private String zipCode;
    private int percent;

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCustSurname() { return custSurname; }
    public void setCustSurname(String custSurname) { this.custSurname = custSurname; }
    public String getCustName() { return custName; }
    public void setCustName(String custName) { this.custName = custName; }
    public String getCustPatronymic() { return custPatronymic; }
    public void setCustPatronymic(String custPatronymic) { this.custPatronymic = custPatronymic; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public int getPercent() { return percent; }
    public void setPercent(int percent) { this.percent = percent; }

    public String getCard_number() { return getCardNumber(); }
    public void setCard_number(String card_number) { setCardNumber(card_number); }
    public String getCust_surname() { return getCustSurname(); }
    public void setCust_surname(String cust_surname) { setCustSurname(cust_surname); }
    public String getCust_name() { return getCustName(); }
    public void setCust_name(String cust_name) { setCustName(cust_name); }
    public String getCust_patronymic() { return getCustPatronymic(); }
    public void setCust_patronymic(String cust_patronymic) { setCustPatronymic(cust_patronymic); }
    public String getPhone_number() { return getPhoneNumber(); }
    public void setPhone_number(String phone_number) { setPhoneNumber(phone_number); }
    public String getCity_name() { return getCity(); }
    public void setCity_name(String city_name) { setCity(city_name); }
    public String getStreet_name() { return getStreet(); }
    public void setStreet_name(String street_name) { setStreet(street_name); }
    public String getZip_code() { return getZipCode(); }
    public void setZip_code(String zip_code) { setZipCode(zip_code); }
}
