package com.pro3.planner.fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ColorVisibilityAdapter;
import com.pro3.planner.baseClasses.NoteColor;
import com.pro3.planner.views.ColorElementView;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class ColorFragment extends android.support.v4.app.Fragment {

    // Store instance variables
    private String title;
    private ImageButton uncheckAll, checkAll;

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

        final MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication)getContext().getApplicationContext()).mainContext;
        final ColorVisibilityAdapter colorVisibilityAdapter = new ColorVisibilityAdapter(getContext(), R.layout.color_list_layout);

        colorVisibilityAdapter.add(new NoteColor("note_color_1", ContextCompat.getColor(getContext(), R.color.note_color_1)));
        colorVisibilityAdapter.add(new NoteColor("note_color_2", ContextCompat.getColor(getContext(), R.color.note_color_2)));
        colorVisibilityAdapter.add(new NoteColor("note_color_3", ContextCompat.getColor(getContext(), R.color.note_color_3)));
        colorVisibilityAdapter.add(new NoteColor("note_color_4", ContextCompat.getColor(getContext(), R.color.note_color_4)));
        colorVisibilityAdapter.add(new NoteColor("note_color_5", ContextCompat.getColor(getContext(), R.color.note_color_5)));
        colorVisibilityAdapter.add(new NoteColor("note_color_6", ContextCompat.getColor(getContext(), R.color.note_color_6)));
        colorVisibilityAdapter.add(new NoteColor("note_color_7", ContextCompat.getColor(getContext(), R.color.note_color_7)));
        colorVisibilityAdapter.add(new NoteColor("note_color_8", ContextCompat.getColor(getContext(), R.color.note_color_8)));
        colorVisibilityAdapter.add(new NoteColor("note_color_9", ContextCompat.getColor(getContext(), R.color.note_color_9)));
        colorListView.setAdapter(colorVisibilityAdapter);

        colorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ColorElementView colorElementView = (ColorElementView) view;
                if (colorElementView.isChecked()) {
                    colorElementView.setChecked(false);
                    LocalSettingsManager.getInstance().setColorVisibility(colorVisibilityAdapter.getItem(position).getColor(), 1);
                } else {
                    colorElementView.setChecked(true);
                    LocalSettingsManager.getInstance().setColorVisibility(colorVisibilityAdapter.getItem(position).getColor(), -1);
                }

                mainActivityInterface.getElementAdapter().hideElements();
            }
        });

        uncheckAll = (ImageButton) view.findViewById(R.id.uncheckAll);
        checkAll = (ImageButton) view.findViewById(R.id.checkAll);

        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorVisibilityAdapter.checkAll();
            }
        });
        uncheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorVisibilityAdapter.uncheckAll();
            }
        });

        return view;
    }
}
