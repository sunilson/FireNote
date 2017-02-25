package com.sunilson.firenote.Interfaces;

/**
 * @author Linus Weiss
 */

/**
 * Interface implemented by RecyclerView Adapters
 */
public interface ItemTouchHelperAdapter {

    /**
     * Item was moved inside list. Not used
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * Item was swiped in list
     *
     * @param position Position of item
     */
    void onItemDismiss(int position);

}
