package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;
import com.sunilson.firenote.adapters.ChecklistRecyclerAdapter;

/**
 * Created by linus_000 on 24.11.2016.
 */

public interface ChecklistInterface {

    public DatabaseReference getElementsReference();
    public ChecklistRecyclerAdapter getCheckListRecyclerAdapter();

}
