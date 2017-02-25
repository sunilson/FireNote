package com.sunilson.firenote.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.LocalSettingsManager;
import com.sunilson.firenote.R;
import com.sunilson.firenote.baseClasses.Category;
import com.sunilson.firenote.views.CategoryVisibilityView;

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

    /**
     * Sort adapter with given Comparator
     *
     * @param comparator Comparator for sorting ArrayList
     */
    @Override
    public void sort(@NonNull Comparator comparator) {
        Collections.sort(list, comparator);
    }

    /**
     * Set all elements of adapter to unchecked
     */
    public void uncheckAll() {
        MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;

        for (int i = 0; i < getCount(); i++) {
            Category category = getItem(i);
            if (category != null) {
                LocalSettingsManager.getInstance().setCategoryVisibility(category.getCategoryID(), -1);
                mainActivityInterface.getElementAdapter().hideElements();
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Set all elements of adapter to checked
     */
    public void checkAll() {
        MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;

        for (int i = 0; i < getCount(); i++) {
            Category category = getItem(i);
            if (category != null) {
                LocalSettingsManager.getInstance().setCategoryVisibility(category.getCategoryID(), 1);
                mainActivityInterface.getElementAdapter().hideElements();
            }
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
        if (category != null) {
            elementHolder.elementText.setText(category.getCategoryName());

            if (LocalSettingsManager.getInstance().getCategoryVisibility(category.getCategoryID()) == 1) {
                row.setChecked(false);
            } else {
                row.setChecked(true);
            }
        }

        return row;
    }

    private static class ElementHolder {
        TextView elementText;
    }
}
