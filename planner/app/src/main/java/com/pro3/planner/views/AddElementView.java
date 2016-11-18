package com.pro3.planner.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.pro3.planner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by linus_000 on 14.11.2016.
 */

public class AddElementView extends LinearLayout{

    private View v;
    private List<View> colorBlocks = new ArrayList<>();
    private TableLayout tableLayout;
    private EditText title;
    private Spinner category;
    private HashMap<String, String> categories = new HashMap<>();
    private ArrayAdapter<CharSequence> categoryAdapter;
    private int selectedColor;

    public AddElementView(Context context, ArrayAdapter<CharSequence> categoryAdapter) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.alertdialog_body_add_element, this, true);


        title = (EditText) findViewById(R.id.add_element_title);
        tableLayout = (TableLayout) findViewById(R.id.add_element_tableLayout);
        category = (Spinner) findViewById(R.id.add_element_category);
        category.setAdapter(categoryAdapter);

        int countTableRows = tableLayout.getChildCount();

        for (int i = 0; i < countTableRows; i++) {
            TableRow tablerow = (TableRow) tableLayout.getChildAt(i);
            int countColorblocks = tablerow.getChildCount();
            for (int j = 0; j < countColorblocks; j++) {
                View view = tablerow.getChildAt(j);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectItem(Integer.parseInt(v.getTag().toString()));
                    }
                });
                colorBlocks.add(view);
            }
        }

        selectItem(3);
    }

    private void selectItem(int position) {

        Iterator it = colorBlocks.iterator();

        while (it.hasNext()) {
            View view = (View) it.next();
            view.setVisibility(VISIBLE);
        }

        View view = colorBlocks.get(position);
        view.setVisibility(INVISIBLE);

        int color = ((ColorDrawable) view.getBackground()).getColor();
        selectedColor = color;
    }

    public void addCategory(String category, String key) {
        categories.put(key, category);
        categoryAdapter.add(category);
        categoryAdapter.notifyDataSetChanged();
    }

    public void removeCategory(String category, String key) {
        categories.remove(key);
        categoryAdapter.remove(category);
    }

    public String getTitle() {
        return title.getText().toString();
    }

    public String getCategory() {
        return category.getSelectedItem().toString();
    }

    public int getColor() {
        return selectedColor;
    }
}
