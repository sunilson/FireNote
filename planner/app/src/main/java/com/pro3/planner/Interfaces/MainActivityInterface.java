package com.pro3.planner.Interfaces;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.adapters.CategoryVisibilityAdapter;
import com.pro3.planner.adapters.SpinnerAdapter;

/**
 * Created by linus_000 on 10.12.2016.
 */

public interface MainActivityInterface extends HasSortableList {
    DatabaseReference getBinReference();

    SpinnerAdapter getSpinnerCategoryAdapter();

    CategoryVisibilityAdapter getListCategoryVisibilityAdapter();

    DatabaseReference getElementsReference();

    DatabaseReference getReference();

    void refreshListeners();

    void removeListeners();
}
