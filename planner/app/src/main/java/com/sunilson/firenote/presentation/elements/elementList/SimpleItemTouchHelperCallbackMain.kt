package com.sunilson.firenote.presentation.elements.elementList

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter

class SimpleItemTouchHelperCallbackMain (val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags = 0
        var swipeFlags = 0

        if (viewHolder is ElementRecyclerAdapter.ViewHolder && viewHolder.swipeable) {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }

        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = adapter.onItemDismiss(viewHolder.adapterPosition)
    override fun isLongPressDragEnabled(): Boolean = false
    override fun isItemViewSwipeEnabled(): Boolean = true
}