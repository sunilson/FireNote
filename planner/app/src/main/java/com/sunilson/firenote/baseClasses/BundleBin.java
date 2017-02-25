package com.sunilson.firenote.baseClasses;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Linus Weiss
 */

public class BundleBin {

    private Map<String, Checklist> checklists = new HashMap<>();
    private Map<String, Note> notes = new HashMap<>();

    public BundleBin() {

    }

    public Map<String, Checklist> getChecklists() {
        return checklists;
    }

    public void setChecklists(Map<String, Checklist> checklistElements) {
        this.checklists = checklistElements;
    }

    public Map<String, Note> getNotes() {
        return notes;
    }

    public void setNotes(Map<String, Note> noteElements) {
        this.notes = noteElements;
    }
}
