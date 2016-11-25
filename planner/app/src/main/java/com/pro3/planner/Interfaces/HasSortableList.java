package com.pro3.planner.Interfaces;

import android.content.SharedPreferences;

import com.pro3.planner.adapters.ElementRecyclerAdapter;

/**
 * Created by linus_000 on 11.11.2016.
 */

public interface HasSortableList {

    public ElementRecyclerAdapter getElementAdapter();

    public SharedPreferences getSharedPrefs();

}
