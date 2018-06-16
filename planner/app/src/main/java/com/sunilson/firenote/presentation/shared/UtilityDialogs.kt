package com.sunilson.firenote.presentation.shared

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunilson.firenote.R
import kotlinx.android.synthetic.main.alertdialog_body_authenticate.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

class UtilityDialogs {
    companion object {

        private fun createAlert(context: Context, title: String, content: View, positive: () -> Unit, negative: (() -> Unit)? = {}): AlertDialog.Builder {
            val alert = AlertDialog.Builder(context)
            val titleView = LayoutInflater.from(context).inflate(R.layout.alertdialog_custom_title, null)
            titleView.dialog_title.text = title
            alert.setCustomTitle(titleView)
            alert.setView(content)

            alert.setPositiveButton(R.string.confirm_add_dialog) { _, bla ->
                positive()
            }
            alert.setNegativeButton(R.string.cancel_add_dialog) { _, _ ->
                negative?.invoke()
            }

            return alert
        }

        private fun createSimpleTextDialog(context: Context, title: String, content: View, positive: () -> Unit, negative: (() -> Unit)? = {}, doneWithEnter: Boolean = false): Dialog {
            val alert = createAlert(context, title, content, positive, negative)
            val dialog = alert.create()
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            dialog.window.attributes.windowAnimations = R.style.dialogAnimation
            dialog.setOnDismissListener {
                (context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(dialog.window.decorView.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }

            if(doneWithEnter) {
                content.findViewById<EditText>(R.id.dialog_edit_text).setOnEditorActionListener { _, i, keyEvent ->
                    if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
                    false
                }
            }

            return dialog
        }

        fun showMultiLineTextDialog(context: Context, title: String, callback: (String?) -> Unit) {
            val content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_import_text, null)
            createSimpleTextDialog(context, title, content, {
                callback(content.findViewById<EditText>(R.id.dialog_edit_text).text.toString())
            }).show()

            content.findViewById<EditText>(R.id.dialog_edit_text).requestFocus()
        }

        fun showMasterPasswordDialog(context: Context, callback: (Boolean) -> Unit) {
            val content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_password, null)
            createSimpleTextDialog(context,
                    context.getString(R.string.enter_master_password),
                    content,
                    {
                        FirebaseDatabase.getInstance().getReference("users/${FirebaseAuth.getInstance().currentUser?.uid}/settings/masterPassword").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                                callback(false)
                            }

                            override fun onDataChange(p0: DataSnapshot?) {
                                if (p0 != null) {
                                    val passwordHash = p0.getValue(String::class.java)
                                    Log.d("Linus", content.password.text.toString().encodePassword())
                                    callback(passwordHash == content.password.text.toString().encodePassword())
                                } else callback(false)
                            }
                        })
                    }, doneWithEnter = true).show()
        }
    }
}