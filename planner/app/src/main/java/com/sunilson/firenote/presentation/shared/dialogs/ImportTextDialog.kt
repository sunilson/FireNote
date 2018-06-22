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
import kotlinx.android.synthetic.main.alertdialog_body_import_text.view.*

class ImportTextDialog : BaseDialogFragmentSimpleText(), DialogWithResult<String> {

    override var listener: DialogListener<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_import_text, null)
        builder.setView(content)

        builder.setPositiveButton(R.string.confirm_add_dialog) { _, _ -> listener?.onResult(content.import_dialog_edittext.text.toString()) }
        builder.setNegativeButton(R.string.cancel) { _, _ -> listener?.onResult(null) }

        val dialog = builder.create()

        content.import_dialog_edittext.requestFocus()
        content.import_dialog_edittext.setOnEditorActionListener { _, i, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
            }
            false
        }
        return dialog
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance(): ImportTextDialog = ImportTextDialog()
    }
}