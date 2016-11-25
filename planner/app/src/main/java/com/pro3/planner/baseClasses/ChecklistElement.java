package com.pro3.planner.baseClasses;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class ChecklistElement {

    private boolean finished;
    private String text;
    private String elementID;
    private int position;

    public ChecklistElement(String text, String elementID, int position) {
        this.text = text;
        finished = false;
        this.elementID = elementID;
        this.position = position;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public ChecklistElement() {

    }

    public String getElementID() {
        return elementID;
    }

    public void setElementID(String elementID) {
        this.elementID = elementID;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

}
