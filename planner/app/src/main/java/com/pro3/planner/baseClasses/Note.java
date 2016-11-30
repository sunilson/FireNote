package com.pro3.planner.baseClasses;

import android.content.Context;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Note extends Element{

    private String text;
    private Reminder reminder;

    public Note(String noteType, String title, Context context) {
        super(noteType, title, context);
        this.text = "";
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
