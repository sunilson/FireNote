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
    private int color;
    private int icon;
    private Date creationDate;

    public Element(String noteType, String title, Context context) {
        this.noteType = noteType;

        if(title != null) {
            this.title = title;
        } else {
            this.title = "New Element";
        }

        if(noteType.equals("checklist")) {
            icon = R.drawable.element_checklist_icon;
        } else if (noteType.equals("note")) {
            icon = R.drawable.element_note_icon;
        } else {
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
