package com.pro3.planner.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pro3.planner.R;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class ColorElementView extends LinearLayout implements Checkable {

    private View v;
    private boolean checked = false;
    private FrameLayout frameLayout;
    private ImageView check;

    public ColorElementView(Context context) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.color_list_layout, this, true);
        frameLayout = (FrameLayout) v.findViewById(R.id.color_list_layout);
        check = (ImageView) v.findViewById(R.id.color_list_icon);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;

        if (isChecked()) {
            int color = ContextCompat.getColor(getContext(), R.color.color_overlay);
            Drawable drawable = new ColorDrawable(color);
            frameLayout.setForeground(drawable);
            check.setVisibility(VISIBLE);
        } else {
            int[] attrs = new int[] { android.R.attr.selectableItemBackground};
            TypedArray ta = getContext().obtainStyledAttributes(attrs);
            frameLayout.setForeground(ta.getDrawable(0));
            ta.recycle();
            check.setVisibility(GONE);
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
