package com.pro3.planner.views;

import android.app.Activity;
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

import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.BundleInterface;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ColorAddAdapter;
import com.pro3.planner.adapters.SpinnerAdapter;
import com.pro3.planner.baseClasses.Category;
import com.pro3.planner.baseClasses.NoteColor;

/**
 * Created by linus_000 on 24.12.2016.
 */

public class EditElementView extends LinearLayout implements AdapterView.OnItemSelectedListener{

    private View v;
    private EditText title;
    private Spinner categorySpinner;
    private Category selectedCategory;
    private int selectedColor;
    private ListView colorList;
    private String type, id;
    private InputMethodManager imm;

    public EditElementView(Context context, final ArrayAdapter<CharSequence> categoryAdapter, String type, String id) {
        super(context);

        this.type = type;
        this.id = id;

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.alertdialog_body_element_edit, this, true);

        title = (EditText) findViewById(R.id.edit_element_title);
        title.requestFocus();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        categorySpinner.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                title.clearFocus();
                categorySpinner.requestFocus();
                return false;
            }
        });
        categorySpinner = (Spinner) findViewById(R.id.edit_element_categorySpinner);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);
        categorySpinner.setPrompt(getResources().getString(R.string.spinner_prompt));

        Activity activity = (Activity) getContext();

        if (activity instanceof BundleInterface && !type.equals("bundle")) {
            BundleInterface bundleInterface = (BundleInterface) getContext();
            title.setText(bundleInterface.getElementAdapter().getElement(id).getTitle());
        } else if (activity instanceof ElementInterface) {
            ElementInterface elementInterface = (ElementInterface) getContext();
            title.setText(elementInterface.getElementTitle());
        } else if (activity instanceof MainActivityInterface) {
            MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
            title.setText(mainActivityInterface.getElementAdapter().getElement(id).getTitle());
        }

        title.setSelection(title.getText().length());

        colorList = (ListView) findViewById(R.id.edit_element_colors);
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

    public EditText getTitleEditText() {
        return title;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        selectedCategory = ((SpinnerAdapter) parent.getAdapter()).getCategory(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
