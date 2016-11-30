package com.pro3.planner.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.NoteColor;
import com.pro3.planner.views.ColorElementView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class ColorVisibilityAdapter extends ArrayAdapter<NoteColor> {

    private List<NoteColor> list = new ArrayList<>();

    public ColorVisibilityAdapter(Context context, int resource) {
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

        ColorElementView row = (ColorElementView) convertView;

        ColorVisibilityAdapter.ElementHolder elementHolder;

        if(row == null) {
            row = new ColorElementView(getContext());
            elementHolder = new ColorVisibilityAdapter.ElementHolder();
            elementHolder.icon = (ImageView) row.findViewById(R.id.color_list_icon);
            row.setTag(elementHolder);
        } else {
            elementHolder = (ElementHolder) row.getTag();
        }

        NoteColor noteColor = getItem(position);
        row.setBackgroundColor(noteColor.getColor());

        elementHolder.icon.setImageResource(R.drawable.color_list_negative_icon);

        if (LocalSettingsManager.getInstance().getColorVisibility(noteColor.getColor()) == -1) {
            row.setChecked(true);
        } else {
            row.setChecked(false);
        }

        return row;
    }

    static class ElementHolder {
        public ImageView icon;
    }
}
