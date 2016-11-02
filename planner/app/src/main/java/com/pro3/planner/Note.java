package com.pro3.planner;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Note extends Element{

    private String text;
    private Reminder reminder;

    public Note(String noteID, String noteType) {
        super(noteID, noteType);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
