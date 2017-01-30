package com.sunilson.firenote.baseClasses;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Note extends Element{

    private String text;

    public Note(String noteType, String title) {
        super(noteType, title);
        this.text = "";
    }

    public Note() {
        super();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
