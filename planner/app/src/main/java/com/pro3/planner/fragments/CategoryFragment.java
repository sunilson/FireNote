package com.pro3.planner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.views.CategoryVisibilityView;

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

        final MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication)getContext().getApplicationContext()).mainContext;

        categoryListView.setAdapter(mainActivityInterface.getListCategoryVisibilityAdapter());

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryVisibilityView categoryVisibilityView = (CategoryVisibilityView) view;
                if (categoryVisibilityView.isChecked()) {
                    Log.i("Linus", "Checked false");
                    categoryVisibilityView.setChecked(false);
                    LocalSettingsManager.getInstance().setCategoryVisibility((mainActivityInterface.getListCategoryVisibilityAdapter().getItem(position)).getCategoryID(), 1);
                } else {
                    Log.i("Linus", "Checked true");
                    categoryVisibilityView.setChecked(true);
                    LocalSettingsManager.getInstance().setCategoryVisibility(mainActivityInterface.getListCategoryVisibilityAdapter().getItem(position).getCategoryID(), -1);
                }

                mainActivityInterface.getElementAdapter().hideElements();
            }
        });

        return view;
    }
}
