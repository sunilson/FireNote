package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.Category;
import com.pro3.planner.views.CategoryVisibilityView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by linus_000 on 18.11.2016.
 */

public class CategoryVisibilityAdapter extends ArrayAdapter {

    private List<Category> list;

    public CategoryVisibilityAdapter(Context context, int resource, List<Category> entries) {
        super(context, resource);

        list = entries;
    }

    @Nullable
    @Override
    public Category getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void sort(@NonNull Comparator comparator) {
        Collections.sort(list, comparator);
    }

    public void uncheckAll() {
        MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;

        for (int i = 0; i < getCount(); i++) {
            CategoryVisibilityView categoryVisibilityView = (CategoryVisibilityView) getView(i, null, null);
            LocalSettingsManager.getInstance().setCategoryVisibility((getItem(i)).getCategoryID(), -1);
            mainActivityInterface.getElementAdapter().hideElements();
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryVisibilityView row = (CategoryVisibilityView) convertView;

        CategoryVisibilityAdapter.ElementHolder elementHolder;

        if (row == null) {
            row = new CategoryVisibilityView(getContext());
            elementHolder = new CategoryVisibilityAdapter.ElementHolder();
            elementHolder.elementText = (TextView) row.findViewById(R.id.category_element_text);
            row.setTag(elementHolder);
        } else {
            elementHolder = (CategoryVisibilityAdapter.ElementHolder) row.getTag();
        }

        Category category = getItem(position);
        elementHolder.elementText.setText(category.getCategoryName());

        if (LocalSettingsManager.getInstance().getCategoryVisibility(category.getCategoryID()) == 1) {
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
