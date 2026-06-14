package org.example.model;

import java.math.BigDecimal;

public class StoreProduct {
    private String upc;
    private String upcProm;
    private int idProduct;
    private String productName;
    private BigDecimal sellingPrice;
    private int productsNumber;
    private boolean promotionalProduct;

    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    public String getUpcProm() { return upcProm; }
    public void setUpcProm(String upcProm) { this.upcProm = upcProm; }
    public int getIdProduct() { return idProduct; }
    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getProductsNumber() { return productsNumber; }
    public void setProductsNumber(int productsNumber) { this.productsNumber = productsNumber; }
    public boolean isPromotionalProduct() { return promotionalProduct; }
    public void setPromotionalProduct(boolean promotionalProduct) { this.promotionalProduct = promotionalProduct; }

    public String getUPC() { return getUpc(); }
    public void setUPC(String UPC) { setUpc(UPC); }
    public String getUPC_prom() { return getUpcProm(); }
    public void setUPC_prom(String UPC_prom) { setUpcProm(UPC_prom); }
    public int getId_product() { return getIdProduct(); }
    public void setId_product(int id_product) { setIdProduct(id_product); }
    public String getProduct_name() { return getProductName(); }
    public void setProduct_name(String product_name) { setProductName(product_name); }
    public BigDecimal getSelling_price() { return getSellingPrice(); }
    public void setSelling_price(BigDecimal selling_price) { setSellingPrice(selling_price); }
    public int getProducts_number() { return getProductsNumber(); }
    public void setProducts_number(int products_number) { setProductsNumber(products_number); }
    public boolean getPromotional_product() { return isPromotionalProduct(); }
    public void setPromotional_product(boolean promotional_product) { setPromotionalProduct(promotional_product); }
}
