package com.sunilson.firenote.Interfaces;

import android.widget.TextView;

import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapter;

/**
 * @author Linus Weiss
 */

/**
 * Implemented by Activities that have a list that can be sorted
 */
public interface HasSortableList {

    ElementRecyclerAdapter getElementAdapter();

    TextView getSortTextView();

}
