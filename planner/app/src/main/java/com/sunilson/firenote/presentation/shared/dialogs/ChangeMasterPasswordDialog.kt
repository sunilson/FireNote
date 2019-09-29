package com.sunilson.firenote.presentation.shared.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.shared.encodePassword
import kotlinx.android.synthetic.main.alertdialog_body_master_password.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

class ChangeMasterPasswordDialog : BaseDialogFragment() {

    private lateinit var content: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        titleView.dialog_title.text = context?.getString(R.string.set_master_password)
        content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_master_password, null)

        if (!arguments!!.getBoolean("firstTime")) {
            content.master_password_old.visibility = View.GONE
        }

        builder.setView(content)

        builder.setPositiveButton(R.string.confirm_add_dialog) { _, _ -> }
        builder.setNegativeButton(R.string.cancel) { _, _ ->

        }

        val dialog = builder.create()
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val d = dialog as AlertDialog
        d.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
            val newPassword = content.master_password_new.text.toString()
            val newPassword2 = content.master_password_new2.text.toString()
            val oldPassword = content.master_password_old.text.toString()
            if (newPassword.isNotEmpty() && newPassword == newPassword2) {
                val ref = FirebaseDatabase.getInstance()
                        .getReference("users/${FirebaseAuth.getInstance().currentUser?.uid}/settings/masterPassword")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) = showError(context?.getString(R.string.error_change_master_password))
                    override fun onDataChange(p0: DataSnapshot) {
                        val password = p0.getValue(String::class.java)
                        if (content.master_password_old.visibility == View.GONE || password == oldPassword.encodePassword()) {
                            ref.setValue(newPassword.encodePassword())
                                    .addOnSuccessListener {
                                        showSuccess(getString(R.string.password_changed))
                                        dismiss()
                                    }
                                    .addOnFailureListener { showError(getString(R.string.error_change_master_password)) }
                        } else showError(context?.getString(R.string.old_password_error))
                    }
                })
            } else showError(context?.getString(R.string.error_password_match))
        }
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance(firstTime: Boolean = false): ChangeMasterPasswordDialog {
            val dialog = ChangeMasterPasswordDialog()
            val bundle = Bundle()
            bundle.putBoolean("firstTime", firstTime)
            dialog.arguments = bundle
            return dialog
        }
    }
}