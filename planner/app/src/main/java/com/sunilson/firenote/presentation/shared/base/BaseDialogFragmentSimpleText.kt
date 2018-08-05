package com.sunilson.firenote.presentation.shared.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

abstract class BaseDialogFragmentSimpleText : BaseDialogFragment() {

    private lateinit var imm: InputMethodManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        super.onDismiss(dialog)
    }
}