package com.sunilson.firenote.data.models;

/**
 * @author Linus Weiss
 */

public class Category {

    private String categoryID;
    private String categoryName;

    public Category(String categoryName, String categoryID) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public Category() {

    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
