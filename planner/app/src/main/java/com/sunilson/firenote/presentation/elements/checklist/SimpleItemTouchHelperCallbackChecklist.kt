package com.sunilson.firenote.presentation.elements.checklist

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter

class SimpleItemTouchHelperCallbackChecklist(val adapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean = false
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int)  = adapter.onItemDismiss(viewHolder!!.adapterPosition)
    override fun isLongPressDragEnabled(): Boolean = false
    override fun isItemViewSwipeEnabled(): Boolean = true

}