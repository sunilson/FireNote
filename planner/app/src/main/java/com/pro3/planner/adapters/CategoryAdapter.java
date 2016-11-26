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
import com.pro3.planner.baseClasses.Category;
import com.pro3.planner.views.CategoryElementView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by linus_000 on 18.11.2016.
 */

public class CategoryAdapter extends ArrayAdapter {

    private List<Category> list = new ArrayList<>();

    public CategoryAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void remove(Object object) {
        list.remove(object);
        notifyDataSetChanged();
    }

    @Override
    public void add(Object object) {
        list.add((Category) object);
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public Category getItem(int position) {
        return list.get(position);
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

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryElementView row = (CategoryElementView) convertView;

        CategoryAdapter.ElementHolder elementHolder;

        if(row == null) {
            row = new CategoryElementView(getContext());
            elementHolder = new CategoryAdapter.ElementHolder();
            elementHolder.elementText = (TextView) row.findViewById(R.id.category_element_text);
            row.setTag(elementHolder);
        } else {
            elementHolder = (CategoryAdapter.ElementHolder) row.getTag();
        }

        java.lang.String string = getItem(position).getCategoryName();
        elementHolder.elementText.setText(string);

        if (LocalSettingsManager.getInstance().getCategoryVisibility(string) == -1) {
            row.setChecked(true);
        } else {
            row.setChecked(false);
        }

        return row;
    }

    static class ElementHolder {
        TextView elementText;
    }
}
