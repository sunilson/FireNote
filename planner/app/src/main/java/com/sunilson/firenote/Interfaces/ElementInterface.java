package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;

/**
 * @author Linus Weiss
 */

/**
 * Implemented by all Activities that represent an Element Type
 */
public interface ElementInterface {
    DatabaseReference getElementReference();
    DatabaseReference getContentsReference();
    String getElementTitle();
    int getElementColor();
    String getElementCategoryID();
    void stopListeners();

}
