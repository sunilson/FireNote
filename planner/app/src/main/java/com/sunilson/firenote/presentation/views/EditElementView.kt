package com.sunilson.firenote.presentation.views

import android.app.Activity
import android.content.Context
import com.sunilson.firenote.Interfaces.BundleInterface
import com.sunilson.firenote.Interfaces.ElementInterface
import kotlinx.android.synthetic.main.alertdialog_body_add_element.view.*
import kotlinx.android.synthetic.main.alertdialog_body_element_edit.view.*

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