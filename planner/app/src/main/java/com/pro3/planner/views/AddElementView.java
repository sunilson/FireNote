package com.pro3.planner.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pro3.planner.R;
import com.pro3.planner.activities.BaseActivity;
import com.pro3.planner.adapters.ColorAddAdapter;
import com.pro3.planner.adapters.SpinnerAdapter;
import com.pro3.planner.baseClasses.Category;
import com.pro3.planner.baseClasses.NoteColor;

/**
 * Created by linus_000 on 14.11.2016.
 */

public class AddElementView extends LinearLayout implements AdapterView.OnItemSelectedListener {

    private View v;
    private EditText title, category;
    private TextView categoryTitle;
    private Spinner spinner;
    private int selectedColor;
    private ListView colorList;
    private Category selectedCategory;
    private LinearLayout linearLayout;
    private BaseActivity baseActivity;
    private InputMethodManager imm;

    public AddElementView(final Context context, final ArrayAdapter<CharSequence> categoryAdapter) {
        super(context);

        baseActivity = (BaseActivity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.alertdialog_body_add_element, this, true);

        title = (EditText) findViewById(R.id.add_element_title);
        spinner = (Spinner) findViewById(R.id.add_element_categorySpinner);
        colorList = (ListView) findViewById(R.id.add_element_colors);
        linearLayout = (LinearLayout) findViewById(R.id.add_element_layout);
        categoryTitle = (TextView) findViewById(R.id.add_element_category_title);

        title.requestFocus();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        spinner.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                title.clearFocus();
                spinner.requestFocus();
                return false;
            }
        });
        spinner.setAdapter(categoryAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setPrompt(getResources().getString(R.string.spinner_prompt));

        final ColorAddAdapter colorAddAdapter = new ColorAddAdapter(getContext(), R.layout.color_list_layout);

        colorAddAdapter.add(new NoteColor("note_color_1", ContextCompat.getColor(getContext(), R.color.note_color_1)));
        colorAddAdapter.add(new NoteColor("note_color_2", ContextCompat.getColor(getContext(), R.color.note_color_2)));
        colorAddAdapter.add(new NoteColor("note_color_3", ContextCompat.getColor(getContext(), R.color.note_color_3)));
        colorAddAdapter.add(new NoteColor("note_color_4", ContextCompat.getColor(getContext(), R.color.note_color_4)));
        colorAddAdapter.add(new NoteColor("note_color_5", ContextCompat.getColor(getContext(), R.color.note_color_5)));
        colorAddAdapter.add(new NoteColor("note_color_6", ContextCompat.getColor(getContext(), R.color.note_color_6)));
        colorAddAdapter.add(new NoteColor("note_color_7", ContextCompat.getColor(getContext(), R.color.note_color_7)));
        colorAddAdapter.add(new NoteColor("note_color_8", ContextCompat.getColor(getContext(), R.color.note_color_8)));
        colorAddAdapter.add(new NoteColor("note_color_9", ContextCompat.getColor(getContext(), R.color.note_color_9)));
        colorList.setAdapter(colorAddAdapter);

        colorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             ColorElementView colorElementView = (ColorElementView) view;
             if (!colorElementView.isChecked()) {
                 selectedColor = colorAddAdapter.getItem(position).getColor();
                 colorAddAdapter.uncheckAll();
                 colorAddAdapter.setCheckedPosition(position);
                 colorElementView.setChecked(true);
             }
         }
     }
        );

        colorList.setOnScrollListener(new AbsListView.OnScrollListener() {
              @Override
              public void onScrollStateChanged(AbsListView absListView, int i) {
                  imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
              }

              @Override
              public void onScroll(AbsListView absListView, int i, int i1, int i2) {
              }
          }
        );

        colorAddAdapter.setCheckedPosition(0);
        ((ColorElementView) colorAddAdapter.getView(0, null, null)).setChecked(true);

        selectedColor = colorAddAdapter.getItem(0).getColor();

        title.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    public String getTitle() {
        return title.getText().toString();
    }

    public Category getCategory() {
        return selectedCategory;
    }

    public int getColor() {
        return selectedColor;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = ((SpinnerAdapter) parent.getAdapter()).getCategory(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
