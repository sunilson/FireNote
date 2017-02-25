package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;

/**
 * @author Linus Weiss
 */

/**
 * Implemented by BundleActivity
 */
public interface BundleInterface extends HasSortableList {
    DatabaseReference getElementsReference();
    void setDeletedElement(boolean value);
}
