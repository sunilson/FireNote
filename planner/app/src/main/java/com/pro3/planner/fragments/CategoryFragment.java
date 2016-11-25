package com.pro3.planner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pro3.planner.Interfaces.CanAddElement;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.views.CategoryElementView;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class CategoryFragment extends android.support.v4.app.Fragment {

    // Store instance variables
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static CategoryFragment newInstance(int page, String title) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        categoryFragment.setArguments(args);
        return categoryFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ListView categoryListView = (ListView) view.findViewById(R.id.fragment_category_listview);

        final CanAddElement canAddElement = (CanAddElement) getActivity();
        categoryListView.setAdapter(canAddElement.getListCategoryAdapter());

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryElementView categoryElementView = (CategoryElementView) view;
                if (categoryElementView.isChecked()) {
                    categoryElementView.setChecked(false);
                    LocalSettingsManager.getInstance().setCategoryVisibility(canAddElement.getListCategoryAdapter().getItem(position), 1);
                } else {
                    categoryElementView.setChecked(true);
                    LocalSettingsManager.getInstance().setCategoryVisibility(canAddElement.getListCategoryAdapter().getItem(position), -1);
                }

                canAddElement.getElementAdapter().hideElements();
            }
        });

        return view;
    }
}
