package com.sunilson.firenote.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.View
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.activities.BaseElementActivity
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

abstract class SuperDialog : DialogFragment() {

    protected lateinit var titleView: View
    protected lateinit var builder: AlertDialog.Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        builder = AlertDialog.Builder(activity)
        titleView = activity.layoutInflater.inflate(R.layout.alertdialog_custom_title, null)
        if (activity is BaseElementActivity) titleView.dialog_title_container.setBackgroundColor((activity as BaseElementActivity).elementColor)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(dialog.window != null) dialog.window.attributes.windowAnimations = R.style.dialogAnimation
    }
}