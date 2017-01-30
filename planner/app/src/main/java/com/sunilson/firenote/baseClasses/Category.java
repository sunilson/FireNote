package com.sunilson.firenote.baseClasses;

/**
 * Created by linus_000 on 25.11.2016.
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
