package com.pro3.planner;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class ChecklistElement {

    private boolean finished;
    private String text;

    public ChecklistElement(String text) {
        this.text = text;
        finished = false;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

}
