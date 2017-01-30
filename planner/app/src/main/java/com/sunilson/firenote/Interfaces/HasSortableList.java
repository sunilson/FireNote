package com.sunilson.firenote.Interfaces;

import android.widget.TextView;

import com.sunilson.firenote.adapters.ElementRecyclerAdapter;

/**
 * Created by linus_000 on 11.11.2016.
 */

public interface HasSortableList {

    ElementRecyclerAdapter getElementAdapter();

    TextView getSortTextView();

}
