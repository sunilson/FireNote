package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pro3.planner.baseClasses.NoteColor;
import com.pro3.planner.views.ColorElementView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Linus Weiss
 */

public class ColorAddAdapter extends ArrayAdapter<NoteColor> {

    private List<NoteColor> list = new ArrayList<>();
    private int checked = -1;

    public ColorAddAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void add(NoteColor object) {
        list.add(object);
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public NoteColor getItem(int position) {
        return list.get(position);
    }

    public int getPositionWithColor(int color) {
        Iterator<NoteColor> it = list.iterator();
        int count = 0;

        while (it.hasNext()) {
            NoteColor noteColor = it.next();

            if (noteColor.getColor() == color) {
                break;
            }

            count++;
        }

        return count;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setCheckedPosition(int position) {
        checked = position;
    }

    public void uncheckAll() {
        checked = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ColorElementView row = (ColorElementView) convertView;

        ElementHolder elementHolder;

        if(row == null) {
            row = new ColorElementView(getContext());
            elementHolder = new ElementHolder();
            row.setTag(elementHolder);
        }

        NoteColor noteColor = getItem(position);
        if (noteColor != null) {
            row.setBackgroundColor(noteColor.getColor());
        }

        if (position != checked) {
            row.setChecked(false);
        } else{
            row.setChecked(true);
        }

        return row;
    }

    private static class ElementHolder {

    }
}
