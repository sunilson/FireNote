package com.pro3.planner.Interfaces;

import android.widget.TextView;

import com.pro3.planner.adapters.ElementRecyclerAdapter;

/**
 * Created by linus_000 on 11.11.2016.
 */

public interface HasSortableList {

    ElementRecyclerAdapter getElementAdapter();

    TextView getSortTextView();

}
