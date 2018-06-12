package com.sunilson.firenote.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import com.sunilson.firenote.R
import kotlinx.android.synthetic.main.alertdialog_body_password.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

class UtilityDialogs {
    companion object {
        fun showPasswordDialog(context: Context, callback: (Boolean) -> Unit){
                val alert = AlertDialog.Builder(context)

                val titleView = LayoutInflater.from(context).inflate(R.layout.alertdialog_custom_title, null)
                titleView.dialog_title.text = context.getString(R.string.enter_master_password)
                alert.setCustomTitle(titleView)

                val content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_checklist_add, null)
                alert.setView(content)

                alert.setPositiveButton(R.string.confirm_add_dialog) { _, _ ->
                    callback(true)
                }
                alert.setNegativeButton(R.string.cancel_add_dialog) { _, _ -> }

                val dialog = alert.create()
                dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                dialog.window.attributes.windowAnimations = R.style.dialogAnimation

                content.password.setOnEditorActionListener { _, i, keyEvent ->
                    if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
                    false
                }

                dialog.show()
        }
    }
}