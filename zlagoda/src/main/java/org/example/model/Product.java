package org.example.model;

public class Product {
    private int idProduct;
    private int categoryNumber;
    private String categoryName;
    private String productName;
    private String characteristics;

    public int getIdProduct() { return idProduct; }
    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }
    public int getCategoryNumber() { return categoryNumber; }
    public void setCategoryNumber(int categoryNumber) { this.categoryNumber = categoryNumber; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getCharacteristics() { return characteristics; }
    public void setCharacteristics(String characteristics) { this.characteristics = characteristics; }

    public int getId_product() { return getIdProduct(); }
    public void setId_product(int id_product) { setIdProduct(id_product); }
    public int getCategory_number() { return getCategoryNumber(); }
    public void setCategory_number(int category_number) { setCategoryNumber(category_number); }
    public String getCategory_name() { return getCategoryName(); }
    public void setCategory_name(String category_name) { setCategoryName(category_name); }
    public String getProduct_name() { return getProductName(); }
    public void setProduct_name(String product_name) { setProductName(product_name); }
}
