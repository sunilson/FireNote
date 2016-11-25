package com.pro3.planner.Interfaces;

/**
 * Created by linus_000 on 24.11.2016.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
