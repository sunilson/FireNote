package com.pro3.planner.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pro3.planner.Interfaces.CanAddElement;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ColorAdapter;
import com.pro3.planner.baseClasses.NoteColor;
import com.pro3.planner.views.ColorElementView;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class ColorFragment extends android.support.v4.app.Fragment {

    // Store instance variables
    private String title;

    public static ColorFragment newInstance(String title) {
        ColorFragment colorFragment = new ColorFragment();
        Bundle args = new Bundle();
        args.putString("someTitle", title);
        colorFragment.setArguments(args);
        return colorFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color, container, false);
        ListView colorListView = (ListView) view.findViewById(R.id.fragment_color_listview);
        final ColorAdapter colorAdapter = new ColorAdapter(getContext(), R.layout.color_list_layout);
        colorListView.setAdapter(colorAdapter);

        colorAdapter.add(new NoteColor("note_color_1", ContextCompat.getColor(getContext(), R.color.note_color_1)));
        colorAdapter.add(new NoteColor("note_color_2", ContextCompat.getColor(getContext(), R.color.note_color_2)));
        colorAdapter.add(new NoteColor("note_color_3", ContextCompat.getColor(getContext(), R.color.note_color_3)));
        colorAdapter.add(new NoteColor("note_color_4", ContextCompat.getColor(getContext(), R.color.note_color_4)));
        colorAdapter.add(new NoteColor("note_color_5", ContextCompat.getColor(getContext(), R.color.note_color_5)));
        colorAdapter.add(new NoteColor("note_color_6", ContextCompat.getColor(getContext(), R.color.note_color_6)));
        colorAdapter.add(new NoteColor("note_color_7", ContextCompat.getColor(getContext(), R.color.note_color_7)));
        colorAdapter.add(new NoteColor("note_color_8", ContextCompat.getColor(getContext(), R.color.note_color_8)));
        colorAdapter.add(new NoteColor("note_color_9", ContextCompat.getColor(getContext(), R.color.note_color_9)));

        final CanAddElement canAddElement = (CanAddElement) getActivity();

        colorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ColorElementView colorElementView = (ColorElementView) view;
                if (colorElementView.isChecked()) {
                    colorElementView.setChecked(false);
                    LocalSettingsManager.getInstance().setColorVisibility(colorAdapter.getItem(position).getColor(), 1);
                } else {
                    colorElementView.setChecked(true);
                    LocalSettingsManager.getInstance().setColorVisibility(colorAdapter.getItem(position).getColor(), -1);
                }

                canAddElement.getElementAdapter().sort(LocalSettingsManager.getInstance().getSortingMethod());
            }
        });

        return view;
    }
}
