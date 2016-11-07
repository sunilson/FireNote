package com.pro3.planner;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by linus_000 on 07.11.2016.
 */

public class ChecklistView extends LinearLayout implements Checkable {

    private View v;
    private TextView textView;
    private CheckBox checkBox;
    private boolean checked = false;

    public ChecklistView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.checklist_list_layout, this, true);
        textView = (TextView) v.findViewById(R.id.checkList_element_text);
        checkBox = (CheckBox) v.findViewById(R.id.checkList_element_checkBox);
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
            textView.setTextColor(getResources().getColor(R.color.text_color));
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
    }
}
