package com.pro3.planner.baseClasses;

import android.content.Context;

import com.pro3.planner.R;

import java.util.Date;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Element {
    private String title;
    private String noteID;
    private String noteType;
    private String category;
    private String color;
    private int icon;
    private Date creationDate;

    public Element(String noteType, Context context) {
        this.noteType = noteType;

        if(noteType.equals("checklist")) {
            this.title = "New Checklist";
            icon = R.drawable.ic_done_all_black_24dp;
        } else if (noteType.equals("note")) {
            this.title = "New Note";
            icon = R.drawable.ic_note_black_24dp;
        } else {
            this.title = "New Element";
            icon = R.drawable.ic_toc_black_24dp;
        }
        this.creationDate = new Date();
        this.category = context.getResources().getString(R.string.default_category);
    }

    public Element() {

    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
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
