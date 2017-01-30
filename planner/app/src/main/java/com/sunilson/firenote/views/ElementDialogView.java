package com.sunilson.firenote.views;

import android.content.Context;
import android.content.res.Configuration;
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

import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.R;
import com.sunilson.firenote.adapters.ColorAddAdapter;
import com.sunilson.firenote.adapters.SpinnerAdapter;
import com.sunilson.firenote.baseClasses.Category;
import com.sunilson.firenote.baseClasses.NoteColor;

/**
 * Created by linus_000 on 05.01.2017.
 */

public class ElementDialogView extends LinearLayout implements AdapterView.OnItemSelectedListener {

    protected View v;
    protected EditText title, category;
    protected TextView categoryTitle;
    protected Spinner categorySpinner;
    protected int selectedColor;
    protected ListView colorList;
    protected Category selectedCategory;
    protected LinearLayout linearLayout;
    protected InputMethodManager imm;
    protected MainActivityInterface mainActivityInterface;
    protected ColorAddAdapter colorAdapter;
    protected LayoutInflater inflater;

    public ElementDialogView(Context context, final ArrayAdapter<CharSequence> categoryAdapter) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            v = inflater.inflate(R.layout.alertdialog_body_add_element, this, true);
        } else {
            v = inflater.inflate(R.layout.alertdialog_body_add_element_horizontal, this, true);
        }

        mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
        colorList = (ListView) v.findViewById(R.id.colorlist);
        title = (EditText) findViewById(R.id.add_element_title);
        categorySpinner = (Spinner) findViewById(R.id.add_element_categorySpinner);
        linearLayout = (LinearLayout) findViewById(R.id.add_element_layout);
        categoryTitle = (TextView) findViewById(R.id.add_element_category_title);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        colorAdapter = new ColorAddAdapter(getContext(), R.layout.color_list_layout);
        colorAdapter.add(new NoteColor("note_color_1", ContextCompat.getColor(getContext(), R.color.note_color_1)));
        colorAdapter.add(new NoteColor("note_color_2", ContextCompat.getColor(getContext(), R.color.note_color_2)));
        colorAdapter.add(new NoteColor("note_color_3", ContextCompat.getColor(getContext(), R.color.note_color_3)));
        colorAdapter.add(new NoteColor("note_color_4", ContextCompat.getColor(getContext(), R.color.note_color_4)));
        colorAdapter.add(new NoteColor("note_color_5", ContextCompat.getColor(getContext(), R.color.note_color_5)));
        colorAdapter.add(new NoteColor("note_color_6", ContextCompat.getColor(getContext(), R.color.note_color_6)));
        colorAdapter.add(new NoteColor("note_color_7", ContextCompat.getColor(getContext(), R.color.note_color_7)));
        colorAdapter.add(new NoteColor("note_color_8", ContextCompat.getColor(getContext(), R.color.note_color_8)));
        colorAdapter.add(new NoteColor("note_color_9", ContextCompat.getColor(getContext(), R.color.note_color_9)));
        colorList.setAdapter(colorAdapter);

        colorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                 ColorElementView colorElementView = (ColorElementView) view;
                                                 if (!colorElementView.isChecked()) {
                                                     selectedColor = colorAdapter.getItem(position).getColor();
                                                     colorAdapter.uncheckAll();
                                                     colorAdapter.setCheckedPosition(position);
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

        categorySpinner.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                title.clearFocus();
                categorySpinner.requestFocus();
                return false;
            }
        });

        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);
        categorySpinner.setPrompt(getResources().getString(R.string.spinner_prompt));

        title.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        selectedCategory = ((SpinnerAdapter) parent.getAdapter()).getCategory(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setCategory(String categoryID) {
        int position = mainActivityInterface.getSpinnerCategoryAdapter().getPositionWithID(categoryID);
        categorySpinner.setSelection(position);
    }

    public void setColor(int color) {
        int position = colorAdapter.getPositionWithColor(color);
        selectColor(position);
    }

    protected void selectColor(int position) {
        colorAdapter.setCheckedPosition(position);
        ((ColorElementView) colorAdapter.getView(position, null, null)).setChecked(true);
        if (colorAdapter.getItem(position) != null) {
            selectedColor = colorAdapter.getItem(position).getColor();
        }
    }
}
