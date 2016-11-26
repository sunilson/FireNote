package com.pro3.planner.Interfaces;

import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.baseClasses.Category;

/**
 * Created by linus_000 on 11.11.2016.
 */

public interface CanAddElement extends HasSortableList {

    DatabaseReference getElementsReference();

    ArrayAdapter<CharSequence> getSpinnerCategoryAdapter();

    ArrayAdapter<Category> getListCategoryAdapter();

    DatabaseReference getCategoryReference();

}
