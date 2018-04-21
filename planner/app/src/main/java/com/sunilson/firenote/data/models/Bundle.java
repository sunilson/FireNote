package com.sunilson.firenote.data.models;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Linus Weiss
 */

public class Bundle extends Element {

    private Map<String, Checklist> checklists = new HashMap<>();
    private Map<String, Note> notes = new HashMap<>();
    private BundleBin bin;

    public Bundle() {
        super();
    }

    public Bundle(String noteType, String title) {
        super(noteType, title);
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

    public BundleBin getBin() {
        return bin;
    }

    public void setBin(BundleBin bin) {
        this.bin = bin;
    }
}

