package com.sunilson.firenote.presentation.shared.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogWithResult
import kotlinx.android.synthetic.main.alertdialog_body_confirm.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

class ConfirmDialog : BaseDialogFragment(), DialogWithResult<Boolean> {

    override var listener: DialogListener<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        titleView.dialog_title.text = arguments?.getString("title")
        val content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_confirm, null)
        builder.setView(content)
        builder.setPositiveButton(R.string.confirm_add_dialog) { _, _ ->
            listener?.onResult(true)
        }
        if (arguments?.getBoolean("showNegative") != null && arguments!!.getBoolean("showNegative")) {
            builder.setNegativeButton(R.string.cancel) { _, _ ->
                listener?.onResult(false)
            }
        }

        content.dialog_confirm_text.text = Html.fromHtml(arguments?.getString("text"))
        content.dialog_confirm_text.movementMethod = LinkMovementMethod.getInstance()
        return builder.create()
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance(title: String, text: String, showNegativeButton: Boolean = true): ConfirmDialog {
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("text", text)
            bundle.putBoolean("showNegative", showNegativeButton)
            val dialog = ConfirmDialog()
            dialog.arguments = bundle
            return dialog
        }
    }
}