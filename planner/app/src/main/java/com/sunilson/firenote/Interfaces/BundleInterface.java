package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by linus_000 on 11.11.2016.
 */

public interface BundleInterface extends HasSortableList {

    DatabaseReference getElementsReference();

    void setDeletedElement(boolean value);

}
