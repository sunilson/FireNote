package com.pro3.planner;

import android.graphics.Color;
import java.util.Date;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Element {
    private String noteID;
    private String noteType;
    private String category;
    private int color;
    private Date creationDate;

    public Element(String noteID, String noteType) {
        this.noteID = noteID;
        this.noteType = noteType;
        this.creationDate = new Date();
        this.category = "Default";
        this.color = Color.YELLOW;
    }

    public String getNoteID() {
        return noteID;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
