package com.sunilson.firenote.baseClasses;

/**
 * @author Linus Weiss
 */

public class ChecklistElement {

    private boolean finished;
    private String text;
    private String elementID;

    public ChecklistElement(String text) {
        this.text = text;
        finished = false;
    }

    public boolean isFinished() {
        return finished;
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
