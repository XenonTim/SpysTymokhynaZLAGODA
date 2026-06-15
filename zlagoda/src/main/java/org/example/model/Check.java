package org.example.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Check {
    private String checkNumber;
    private String idEmployee;
    private String cardNumber;
    private Timestamp printDate;
    private BigDecimal sumTotal;
    private BigDecimal vat;
    private String employeeName;
    private String cardOwner;

    public String getCheckNumber() { return checkNumber; }
    public void setCheckNumber(String checkNumber) { this.checkNumber = checkNumber; }
    public String getIdEmployee() { return idEmployee; }
    public void setIdEmployee(String idEmployee) { this.idEmployee = idEmployee; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public Timestamp getPrintDate() { return printDate; }
    public void setPrintDate(Timestamp printDate) { this.printDate = printDate; }
    public BigDecimal getSumTotal() { return sumTotal; }
    public void setSumTotal(BigDecimal sumTotal) { this.sumTotal = sumTotal; }
    public BigDecimal getVat() { return vat; }
    public void setVat(BigDecimal vat) { this.vat = vat; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getCardOwner() { return cardOwner; }
    public void setCardOwner(String cardOwner) { this.cardOwner = cardOwner; }

    public String getCheck_number() { return getCheckNumber(); }
    public void setCheck_number(String check_number) { setCheckNumber(check_number); }
    public String getId_employee() { return getIdEmployee(); }
    public void setId_employee(String id_employee) { setIdEmployee(id_employee); }
    public String getCard_number() { return getCardNumber(); }
    public void setCard_number(String card_number) { setCardNumber(card_number); }
    public Timestamp getPrint_date() { return getPrintDate(); }
    public void setPrint_date(Timestamp print_date) { setPrintDate(print_date); }
    public BigDecimal getSum_total() { return getSumTotal(); }
    public void setSum_total(BigDecimal sum_total) { setSumTotal(sum_total); }
}
