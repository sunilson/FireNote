package com.pro3.planner.Interfaces;

import android.widget.ArrayAdapter;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by linus_000 on 10.12.2016.
 */

public interface MainActivityInterface extends HasSortableList {
    DatabaseReference getBinReference();

    ArrayAdapter<CharSequence> getSpinnerCategoryAdapter();

    ArrayAdapter<String> getListCategoryVisibilityAdapter();

    ArrayAdapter<String> getSettingsCategoryAdapter();

    DatabaseReference getCategoryReference();

    DatabaseReference getElementsReference();
}
