package com.sunilson.firenote.presentation.shared.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.element.ElementActivity
import dagger.android.support.AndroidSupportInjection

abstract class BaseDialogFragment : DialogFragment() {

    protected lateinit var titleView: View
    protected lateinit var builder: AlertDialog.Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        builder = AlertDialog.Builder(activity)
        titleView = activity.layoutInflater.inflate(R.layout.alertdialog_custom_title, null)
        if (activity is ElementActivity) titleView.dialog_title_container.setBackgroundColor((activity as ElementActivity).elementColor)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(dialog.window != null) dialog.window.attributes.windowAnimations = R.style.dialogAnimation
    }
}