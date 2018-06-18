package com.sunilson.firenote.presentation.shared.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragmentSimpleText
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogWithResult
import kotlinx.android.synthetic.main.alertdialog_body_checklist_add.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

class ChecklistElementDialog : BaseDialogFragmentSimpleText(), DialogWithResult<String> {

    override var listener: DialogListener<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        titleView.dialog_title.text = context?.getString(R.string.add_checklist_item_title)
        val content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_checklist_add, null)
        builder.setView(content)

        builder.setPositiveButton(R.string.confirm_add_dialog) { _, _ -> listener?.onResult(content.checklist_add_element_title.text.toString()) }
        builder.setNegativeButton(R.string.cancel_add_dialog) { _, _ -> listener?.onResult(null) }

        val dialog = builder.create()

        content.checklist_add_element_title.requestFocus()
        content.checklist_add_element_title.setOnEditorActionListener { _, i, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
            }
            false
        }
        return dialog
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance(): ChecklistElementDialog = ChecklistElementDialog()
    }
}