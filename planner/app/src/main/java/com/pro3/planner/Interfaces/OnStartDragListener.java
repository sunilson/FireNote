package com.pro3.planner.Interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by linus_000 on 24.11.2016.
 */

public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
