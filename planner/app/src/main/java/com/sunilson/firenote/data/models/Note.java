package com.sunilson.firenote.data.models;

/**
 * @author Linus Weiss
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
