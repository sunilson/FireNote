package com.sunilson.firenote.presentation.views;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunilson.firenote.R;

/**
 * @author LInus Weiss
 */

/**
 * View for a checklist element in a Checklist. Can be checked.
 */
public class ChecklistView extends LinearLayout implements Checkable {

    public View v;
    private TextView textView;
    private CheckBox checkBox;
    private boolean checked = false;
    private LayoutInflater inflater;

    public ChecklistView(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.checklist_list_layout, this, false);
        textView = (TextView) v.findViewById(R.id.checkList_element_text);
        checkBox = (CheckBox) v.findViewById(R.id.checkList_element_checkBox);
    }

    public TextView getTextView() {
        return textView;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        if (isChecked()) {
            //Strike through text and show disabled color
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(getResources().getColor(R.color.text_crossed_out));
            checkBox.setChecked(true);
        } else {
            //Display text normally
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
    }
}
