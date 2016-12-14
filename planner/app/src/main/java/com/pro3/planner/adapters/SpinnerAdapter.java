package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pro3.planner.baseClasses.Category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by linus_000 on 25.11.2016.
 */

public class SpinnerAdapter extends ArrayAdapter {

    List<Category> list = new ArrayList<>();
    private int resource;

    public SpinnerAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public void add(Object object) {
        list.add((Category) object);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Object object) {
        list.remove(object);
        notifyDataSetChanged();
    }

    public void remove(String categoryID) {
        Iterator it = list.iterator();

        while (it.hasNext()) {
            Category category = (Category) it.next();
            if (category.getCategoryID().equals(categoryID)) {
                it.remove();
                return;
            }
        }
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


    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }

    public void update(Category newCategory) {
        Iterator<Category> it = list.iterator();

        while (it.hasNext()) {
            Category category = it.next();
            if (category.getCategoryID().equals(newCategory.getCategoryID())) {
                category.setCategoryName(newCategory.getCategoryName());
            }
        }
        notifyDataSetChanged();
    }

    static class ElementHolder {
        TextView elementText;
    }
}
