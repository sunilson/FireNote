package com.sunilson.firenote.data.models;

/**
 * @author Linus Weiss
 */

public class NoteColor {

    private String colorName;
    private int color;

    public NoteColor(String colorName, int color) {
        this.color = color;
        this.colorName = colorName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
