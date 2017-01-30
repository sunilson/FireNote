package com.sunilson.firenote.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.sunilson.firenote.baseClasses.Category;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Linus Weiss
 */

public class SpinnerAdapter extends ArrayAdapter {

    private List<Category> list;

    public SpinnerAdapter(Context context, int resource, List<Category> entries) {
        super(context, resource);
        list = entries;
    }

    @Override
    public void sort(Comparator comparator) {
        Collections.sort(list, comparator);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return list.get(position).getCategoryName();
    }

    public Category getCategory(int position) {
        return list.get(position);
    }

    public int getPositionWithID(String categoryID) {
        Iterator<Category> it = list.iterator();
        int count = 0;

        while (it.hasNext()) {
            Category category = it.next();
            if (category.getCategoryID().equals(categoryID)) {
                break;
            }

            if (count == list.size() - 1) {
                break;
            }

            count++;
        }

        return count;
    }

    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }
}
