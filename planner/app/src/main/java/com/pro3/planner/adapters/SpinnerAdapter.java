package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linus_000 on 25.11.2016.
 */

public class SpinnerAdapter extends ArrayAdapter {

    List<String> list = new ArrayList<>();
    private int resource;

    public SpinnerAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public void add(Object object) {
        list.add((String) object);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Object object) {
        list.remove(object);
        notifyDataSetChanged();
    }

    public void remove(String categoryName) {
        list.remove(categoryName);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    public String getCategory(int position) {
        return list.get(position);
    }


    @Override
    public int getPosition(Object item) {
        return super.getPosition(item);
    }

    public void update(String oldCategory, String newCategory) {
        list.set(list.indexOf(oldCategory), newCategory);
        notifyDataSetChanged();
    }

    static class ElementHolder {
        TextView elementText;
    }
}
