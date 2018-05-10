package com.sunilson.firenote.presentation.shared.views

import android.content.Context
import com.sunilson.firenote.Interfaces.BundleInterface
import com.sunilson.firenote.Interfaces.ElementInterface
import com.sunilson.firenote.presentation.shared.views.ElementDialogView
import kotlinx.android.synthetic.main.alertdialog_body_add_element.view.*

class EditElementView(context: Context, type: String, id: String) : ElementDialogView(context) {
    init {
        if(context is BundleInterface && type == "bundle") {
            val element = (context as BundleInterface).elementAdapter.getElement(id)
            add_element_title.setText(element?.title)

        } else if (context is ElementInterface) {

        } else {

        }
    }
}