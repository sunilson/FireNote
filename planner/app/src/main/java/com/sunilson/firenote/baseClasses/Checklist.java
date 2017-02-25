package com.sunilson.firenote.baseClasses;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Linus Weiss
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
