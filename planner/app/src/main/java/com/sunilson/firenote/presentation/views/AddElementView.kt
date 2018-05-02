package com.sunilson.firenote.presentation.views

import android.content.Context

class AddElementView(context: Context) : ElementDialogView(context) {
    init {
        colorAdapter.setCheckedPosition(0)
        (colorAdapter.getView(0, null, null) as ColorElementView).isChecked = true
        selectedColor = colorAdapter.getItem(0)!!.color
    }
}