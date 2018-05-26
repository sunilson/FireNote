//package com.sunilson.firenote.Interfaces;
//
//import com.google.firebase.database.DatabaseReference;
//import com.sunilson.firenote.presentation.visibilityDialog.adapters.CategoryVisibilityAdapter;
//
///**
// * @author Linus Weiss
// */
//
///**
// * Interface implemented by the MainActivity
// */
//public interface MainActivityInterface extends HasSortableList {
//    DatabaseReference getBinReference();
//
//    CategorySpinnerAdapter getSpinnerCategoryAdapter();
//
//    CategoryVisibilityAdapter getListCategoryVisibilityAdapter();
//
//    /**
//     * Get Reference to elements
//     *
//     * @return Database Reference to list with all elements
//     */
//    DatabaseReference getElementsReference();
//
//    DatabaseReference getReference();
//
//    /**
//     * If an element has been deleted recently
//     *
//     * @param value Deleted/Not deleted
//     */
//    void setDeletedElement(boolean value);
//
//    /**
//     * Detach and attach all Listeners, so new recyclerData is loaded
//     */
//    void refreshListeners();
//
//    /**
//     * Remove all Listeners
//     */
//    void removeListeners();
//}
