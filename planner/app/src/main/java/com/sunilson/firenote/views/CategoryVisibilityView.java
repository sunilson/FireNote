package com.sunilson.firenote.views;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Checkable;
import android.widget.TextView;

import com.sunilson.firenote.R;

/**
 * @author Linus Weiss
 */

/**
 * View used for elements in a list of categories. Implements Checkable, so it can be easily selected
 */
public class CategoryVisibilityView extends LinearLayout implements Checkable {

    private View v;
    private TextView textView;
    private CheckBox checkBox;
    private boolean checked = false;

    public CategoryVisibilityView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.category_list_layout, this, true);
        textView = (TextView) v.findViewById(R.id.category_element_text);
        checkBox = (CheckBox) v.findViewById(R.id.category_element_checkBox);
    }

    /**
     * Set element to given checked state
     * @param checked True or false
     */
    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        if (isChecked()) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(getResources().getColor(R.color.text_crossed_out));
            checkBox.setChecked(false);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(getResources().getColor(R.color.primary_text_color));
            checkBox.setChecked(true);
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        checked = !checked;
        setChecked(checked);
    }
}
