package com.pro3.planner.Interfaces;

import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.adapters.SpinnerAdapter;
import com.pro3.planner.baseClasses.Category;

/**
 * Created by linus_000 on 10.12.2016.
 */

public interface MainActivityInterface extends HasSortableList {
    DatabaseReference getBinReference();

    SpinnerAdapter getSpinnerCategoryAdapter();

    ArrayAdapter<Category> getListCategoryVisibilityAdapter();

    DatabaseReference getElementsReference();

    DatabaseReference getReference();

    void removeListeners();
}
