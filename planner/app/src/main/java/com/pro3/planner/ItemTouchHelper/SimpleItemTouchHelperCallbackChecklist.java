package com.pro3.planner.ItemTouchHelper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.pro3.planner.Interfaces.ItemTouchHelperAdapter;

/**
 * Created by linus_000 on 24.11.2016.
 */

public class SimpleItemTouchHelperCallbackChecklist extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public SimpleItemTouchHelperCallbackChecklist(ItemTouchHelperAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
