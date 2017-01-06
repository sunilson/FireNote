package com.pro3.planner.views;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by linus_000 on 14.11.2016.
 */

public class AddElementView extends ElementDialogView {

    public AddElementView(final Context context, final ArrayAdapter<CharSequence> categoryAdapter) {
        super(context, categoryAdapter);

        colorAdapter.setCheckedPosition(0);
        ((ColorElementView) colorAdapter.getView(0, null, null)).setChecked(true);
        selectedColor = colorAdapter.getItem(0).getColor();

    }
}
