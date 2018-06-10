package com.sunilson.firenote.baseClasses;

import java.util.Date;

/**
 * @author Linus Weiss
 */

public class Element {
    private String title;
    private String noteID;
    private String noteType;
    private String categoryID;
    private String categoryName;
    private int color;
    private Date creationDate;
    private boolean locked;

    public Element(String noteType, String title) {
        this.noteType = noteType;

        if (title != null) {
            this.title = title;
        } else {
            this.title = "New Element";
        }

        this.creationDate = new Date();
    }

    public Element() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getElementID() {
        return noteID;
    }

    public void setElementID(String noteID) {
        this.noteID = noteID;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getNoteType() {
        return noteType;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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