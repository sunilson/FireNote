package com.pro3.planner.views;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Checkable;
import android.widget.TextView;

import com.pro3.planner.R;

/**
 * Created by linus_000 on 18.11.2016.
 */

public class CategoryElementView extends LinearLayout implements Checkable {

    private View v;
    private TextView textView;
    private CheckBox checkBox;
    private boolean checked = false;

    public CategoryElementView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.category_list_layout, this, true);
        textView = (TextView) v.findViewById(R.id.category_element_text);
        checkBox = (CheckBox) v.findViewById(R.id.category_element_checkBox);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        if (isChecked()) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(getResources().getColor(R.color.text_crossed_out));
            checkBox.setChecked(true);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(getResources().getColor(R.color.primary_text_color));
            checkBox.setChecked(false);
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
