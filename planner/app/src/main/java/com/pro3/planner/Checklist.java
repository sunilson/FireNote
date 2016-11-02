package com.pro3.planner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Checklist extends Element{

    private List checklistElements;
    private Reminder reminder;

    public Checklist(String noteID, String noteType) {
        super(noteID, noteType);
        checklistElements = new ArrayList();
    }

    public void addChecklistElement(String text) {
        if(text != null) {
            checklistElements.add(new ChecklistElement(text));
        }
    }
}
