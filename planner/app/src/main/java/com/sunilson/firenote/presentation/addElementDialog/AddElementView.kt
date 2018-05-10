package com.sunilson.firenote.presentation.addElementDialog

import android.content.Context
import com.sunilson.firenote.presentation.shared.views.ColorElementView
import com.sunilson.firenote.presentation.shared.views.ElementDialogView

class AddElementView(context: Context) : ElementDialogView(context) {
    init {
        colorAdapter.setCheckedPosition(0)
        (colorAdapter.getView(0, null, null) as ColorElementView).isChecked = true
        selectedColor = colorAdapter.getItem(0)!!.color
    }
}