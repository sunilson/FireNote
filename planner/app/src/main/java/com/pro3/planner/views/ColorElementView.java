package com.pro3.planner.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.pro3.planner.R;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class ColorElementView extends LinearLayout implements Checkable {

    private View v;
    private CheckBox checkBox;
    private boolean checked = false;

    public ColorElementView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.color_list_layout, this, true);
        checkBox = (CheckBox) v.findViewById(R.id.color_element_checkBox);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        if (isChecked()) {
            checkBox.setChecked(true);
        } else {
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
