package com.pro3.planner.Interfaces;

import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by linus_000 on 11.11.2016.
 */

public interface CanAddElement extends HasSortableList {

    DatabaseReference getElementsReference();

    ArrayAdapter<CharSequence> getSpinnerCategoryAdapter();

    ArrayAdapter<String> getListCategoryAdapter();

}
