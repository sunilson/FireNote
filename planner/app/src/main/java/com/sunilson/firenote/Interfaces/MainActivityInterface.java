package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;
import com.sunilson.firenote.adapters.CategoryVisibilityAdapter;
import com.sunilson.firenote.adapters.SpinnerAdapter;

/**
 * Created by linus_000 on 10.12.2016.
 */

public interface MainActivityInterface extends HasSortableList {
    DatabaseReference getBinReference();

    SpinnerAdapter getSpinnerCategoryAdapter();

    CategoryVisibilityAdapter getListCategoryVisibilityAdapter();

    DatabaseReference getElementsReference();

    DatabaseReference getReference();

    void setDeletedElement(boolean value);

    void refreshListeners();

    void removeListeners();
}
