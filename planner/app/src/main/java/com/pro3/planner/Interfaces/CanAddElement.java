package com.pro3.planner.Interfaces;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by linus_000 on 11.11.2016.
 */

public interface CanAddElement extends HasSortableList {

    public DatabaseReference getElementsReference();

}
