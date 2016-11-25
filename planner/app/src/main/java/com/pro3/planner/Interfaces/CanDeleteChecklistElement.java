package com.pro3.planner.Interfaces;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.adapters.ChecklistRecyclerAdapter;

/**
 * Created by linus_000 on 24.11.2016.
 */

public interface CanDeleteChecklistElement {

    public DatabaseReference getElementsReference();

    public ChecklistRecyclerAdapter getCheckListRecyclerAdapter();

}
