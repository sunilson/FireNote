package com.pro3.planner.baseClasses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Checklist extends Element {

    private Map<String, ChecklistElement> elements = new HashMap<>();

    public Checklist() {
        super();
    }

    public Checklist(String noteType, String title) {
        super(noteType, title);
    }

    public Map<String, ChecklistElement> getElements() {
        return elements;
    }

    public void setElements(Map<String, ChecklistElement> elements) {
        this.elements = elements;
    }
}
