package com.pro3.planner.Interfaces;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by linus_000 on 12.11.2016.
 */

public interface ElementInterface {

    DatabaseReference getElementReference();
    DatabaseReference getContentsReference();
    String getElementTitle();
    int getElementColor();
    String getElementCategoryID();
    void stopListeners();

}
