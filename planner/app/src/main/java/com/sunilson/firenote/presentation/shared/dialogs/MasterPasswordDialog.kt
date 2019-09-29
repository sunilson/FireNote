package com.sunilson.firenote.presentation.shared.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragmentSimpleText
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogWithResult
import com.sunilson.firenote.presentation.shared.encodePassword
import kotlinx.android.synthetic.main.alertdialog_body_password.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

class MasterPasswordDialog : BaseDialogFragmentSimpleText(), DialogWithResult<Boolean> {

    override var listener: DialogListener<Boolean>? = null
    private lateinit var imm: InputMethodManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        titleView.dialog_title.text = context?.getString(R.string.locked)
        val content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_password, null)
        builder.setView(content)

        builder.setPositiveButton(R.string.confirm_add_dialog) { _, _ ->
            FirebaseDatabase
                    .getInstance()
                    .getReference("users/${FirebaseAuth.getInstance().currentUser?.uid}/settings/masterPassword")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            listener?.onResult(false)
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val passwordHash = p0.getValue(String::class.java)
                            listener?.onResult(passwordHash == content.password_dialog_password.text.toString().encodePassword())
                        }
                    })
        }
        builder.setNegativeButton(R.string.cancel) { _, _ -> listener?.onResult(null) }
        val dialog = builder.create()

        content.password_dialog_password.requestFocus()
        content.password_dialog_password.setOnEditorActionListener { _, i, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
            }
            false
        }

        return dialog
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance(): MasterPasswordDialog = MasterPasswordDialog()
    }
}