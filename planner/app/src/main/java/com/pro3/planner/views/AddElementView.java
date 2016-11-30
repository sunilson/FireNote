package com.pro3.planner.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.Interfaces.CanAddDeleteElement;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ColorAddAdapter;
import com.pro3.planner.adapters.SpinnerAdapter;
import com.pro3.planner.baseClasses.Category;
import com.pro3.planner.baseClasses.NoteColor;

/**
 * Created by linus_000 on 14.11.2016.
 */

public class AddElementView extends LinearLayout implements AdapterView.OnItemSelectedListener{

    private View v;
    private ImageView addCategory, addCategoryDone;
    private EditText title, category;
    private Spinner spinner;
    private int selectedColor;
    private ListView colorList;
    private Category selectedCategory;

    public AddElementView(final Context context, final ArrayAdapter<CharSequence> categoryAdapter) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.alertdialog_body_add_element, this, true);

        title = (EditText) findViewById(R.id.add_element_title);
        spinner = (Spinner) findViewById(R.id.add_element_categorySpinner);
        category = (EditText) findViewById(R.id.add_element_category);
        addCategory = (ImageView) findViewById(R.id.add_element_addCategory);
        addCategoryDone = (ImageView) findViewById(R.id.add_element_addCategory_done);
        colorList = (ListView) findViewById(R.id.add_element_colors);

        title.requestFocus();
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        addCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory.setVisibility(GONE);
                addCategoryDone.setVisibility(VISIBLE);
                spinner.setVisibility(GONE);
                category.setVisibility(VISIBLE);
            }
        });

        addCategoryDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory.setVisibility(VISIBLE);
                addCategoryDone.setVisibility(GONE);
                spinner.setVisibility(VISIBLE);
                category.setVisibility(GONE);

                String categoryName = category.getText().toString();
                if (!categoryName.equals("")) {
                    CanAddDeleteElement canAddDeleteElement = (CanAddDeleteElement) getContext();
                    DatabaseReference dRef = canAddDeleteElement.getCategoryReference().push();
                    Category category = new Category(categoryName, dRef.getKey());
                    dRef.setValue(category);
                    Toast.makeText(getContext(), R.string.added_category, Toast.LENGTH_SHORT).show();
                }
                category.setText("");
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
                if (colorElementView.isChecked()) {

                } else {
                    selectedColor = colorAddAdapter.getItem(position).getColor();
                    colorAddAdapter.uncheckAll();
                    colorAddAdapter.setCheckedPosition(position);
                    colorElementView.setChecked(true);
                }
            }
        });

        colorAddAdapter.setCheckedPosition(0);
        ((ColorElementView)colorAddAdapter.getView(0, null, null)).setChecked(true);
        selectedColor = colorAddAdapter.getItem(0).getColor();
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
        selectedCategory = ((SpinnerAdapter)parent.getAdapter()).getCategory(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
