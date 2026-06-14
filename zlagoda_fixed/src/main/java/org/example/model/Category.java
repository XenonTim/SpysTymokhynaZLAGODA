package org.example.model;

public class Category {
    private int categoryNumber;
    private String categoryName;

    public int getCategoryNumber() { return categoryNumber; }
    public void setCategoryNumber(int categoryNumber) { this.categoryNumber = categoryNumber; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getCategory_number() { return getCategoryNumber(); }
    public void setCategory_number(int category_number) { setCategoryNumber(category_number); }
    public String getCategory_name() { return getCategoryName(); }
    public void setCategory_name(String category_name) { setCategoryName(category_name); }
}
