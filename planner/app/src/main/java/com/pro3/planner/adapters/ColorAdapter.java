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
import java.util.List;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class ColorAdapter extends ArrayAdapter<NoteColor> {

    private List<NoteColor> list = new ArrayList<>();

    public ColorAdapter(Context context, int resource) {
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

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        ColorAdapter.ElementHolder elementHolder;

        if(row == null) {
            row = new ColorElementView(getContext());
            elementHolder = new ColorAdapter.ElementHolder();
            row.setTag(elementHolder);
        }

        NoteColor noteColor = getItem(position);
        row.setBackgroundColor(noteColor.getColor());

        return row;
    }

    static class ElementHolder {

    }
}
