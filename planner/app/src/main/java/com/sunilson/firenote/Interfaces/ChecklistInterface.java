package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;
import com.sunilson.firenote.adapters.ChecklistRecyclerAdapter;

/**
 * @author Linus Weiss
 */

/**
 * Implemented by Checklist Activity
 */
public interface ChecklistInterface {
    public DatabaseReference getElementsReference();
    public ChecklistRecyclerAdapter getCheckListRecyclerAdapter();

}
