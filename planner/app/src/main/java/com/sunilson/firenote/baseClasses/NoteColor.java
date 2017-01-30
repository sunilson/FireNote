package com.sunilson.firenote.baseClasses;

/**
 * Created by linus_000 on 18.11.2016.
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
