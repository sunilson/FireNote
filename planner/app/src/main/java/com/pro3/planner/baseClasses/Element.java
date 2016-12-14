package com.pro3.planner.baseClasses;

import java.util.Date;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Element {
    private String title;
    private String noteID;
    private String noteType;
    private String bundleID;
    private Category category;
    private int color;
    private Date creationDate;
    private String text;
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

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getBundleID() {
        return bundleID;
    }

    public void setBundleID(String bundleID) {
        this.bundleID = bundleID;
    }
}
