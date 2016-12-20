package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.views.CategoryVisibilityView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linus_000 on 18.11.2016.
 */

public class categoryVisibilityAdapter extends ArrayAdapter {

    private List<String> list = new ArrayList<>();

    public categoryVisibilityAdapter(Context context, int resource) {
        super(context, resource);
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
    public void add(Object object) {
        list.add((String) object);
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return list.get(position);
    }


    public void update(String newCategory) {
        list.set(list.indexOf(newCategory), newCategory);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryVisibilityView row = (CategoryVisibilityView) convertView;

        categoryVisibilityAdapter.ElementHolder elementHolder;

        if(row == null) {
            row = new CategoryVisibilityView(getContext());
            elementHolder = new categoryVisibilityAdapter.ElementHolder();
            elementHolder.elementText = (TextView) row.findViewById(R.id.category_element_text);
            row.setTag(elementHolder);
        } else {
            elementHolder = (categoryVisibilityAdapter.ElementHolder) row.getTag();
        }

        String category = getItem(position);
        elementHolder.elementText.setText(category);

        if (LocalSettingsManager.getInstance().getCategoryVisibility(category) == 1) {
            row.setChecked(false);
        } else {
            row.setChecked(true);
        }

        return row;
    }

    static class ElementHolder {
        TextView elementText;
    }
}
