package com.sunilson.firenote.presentation.views;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * @author Linus Weiss
 */

/**
 * View used as content of Add element Dialog
 */
public class AddElementView extends ElementDialogView {

    public AddElementView(final Context context, final ArrayAdapter<CharSequence> categoryAdapter) {
        super(context, categoryAdapter);

        colorAdapter.setCheckedPosition(0);
        ((ColorElementView) colorAdapter.getView(0, null, null)).setChecked(true);
        selectedColor = colorAdapter.getItem(0).getColor();

    }
}
