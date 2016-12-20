package com.pro3.planner.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.R;

public class CategoryActivity extends BaseActivity {

    private MainActivityInterface mainActivityInterface;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivityInterface = (MainActivityInterface) ((BaseApplication) getApplicationContext()).mainContext;
        listView = (ListView) findViewById(R.id.category_list);
        listView.setAdapter(mainActivityInterface.getSettingsCategoryAdapter());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
