package org.example.model;

import java.math.BigDecimal;

public class Sale {
    private String upc;
    private String checkNumber;
    private int productNumber;
    private BigDecimal sellingPrice;
    private String productName;

    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    public String getCheckNumber() { return checkNumber; }
    public void setCheckNumber(String checkNumber) { this.checkNumber = checkNumber; }
    public int getProductNumber() { return productNumber; }
    public void setProductNumber(int productNumber) { this.productNumber = productNumber; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUPC() { return getUpc(); }
    public void setUPC(String UPC) { setUpc(UPC); }
    public String getCheck_number() { return getCheckNumber(); }
    public void setCheck_number(String check_number) { setCheckNumber(check_number); }
    public int getProduct_number() { return getProductNumber(); }
    public void setProduct_number(int product_number) { setProductNumber(product_number); }
    public BigDecimal getSelling_price() { return getSellingPrice(); }
    public void setSelling_price(BigDecimal selling_price) { setSellingPrice(selling_price); }
}
