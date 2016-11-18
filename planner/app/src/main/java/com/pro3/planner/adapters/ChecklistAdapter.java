package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pro3.planner.R;
import com.pro3.planner.baseClasses.ChecklistElement;
import com.pro3.planner.views.ChecklistView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by linus_000 on 07.11.2016.
 */

public class ChecklistAdapter extends ArrayAdapter {

    List<ChecklistElement> list = new ArrayList<>();

    @Override
    public void clear() {
        super.clear();
        list.clear();
    }

    public ChecklistAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(ChecklistElement element) {
        list.add(element);
        notifyDataSetChanged();
    }

    public void update(int position, ChecklistElement element) {
        list.set(position, element);
        notifyDataSetChanged();
    }

    public int getPosition(String elementID) {
        ListIterator<ChecklistElement> it = list.listIterator();

        while (it.hasNext()) {
            ChecklistElement nextElement = it.next();
            if (nextElement.getElementID().equals(elementID)) {
                return list.indexOf(nextElement);
            }
        }

        return -1;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public void remove(Object object) {
        super.remove(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ElementHolder elementHolder;
        if(row == null) {
            row = new ChecklistView(getContext());
            elementHolder = new ElementHolder();
            elementHolder.elementText = (TextView) row.findViewById(R.id.checkList_element_text);
            row.setTag(elementHolder);
        } else {
            elementHolder = (ElementHolder) row.getTag();
        }

        ChecklistElement element = (ChecklistElement) getItem(position);
        elementHolder.elementText.setText(element.getText());

        return row;
    }

    static class ElementHolder {
        TextView elementText;
    }
}
