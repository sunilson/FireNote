package com.sunilson.firenote.presentation.shared.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IAuthentication
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.alertdialog_body_change_password.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*
import javax.inject.Inject

class ChangeLoginPasswordDialog : BaseDialogFragment() {

    @Inject
    lateinit var authService: IAuthentication

    lateinit var content: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_change_password, null)
        builder.setView(content)

        titleView.dialog_title.text = context?.getString(R.string.change_password)
        builder.setPositiveButton(R.string.confirm_add_dialog) { _, _ -> }
        builder.setNegativeButton(R.string.cancel) { _, _ ->}


        val dialog = builder.create()
        return dialog
    }

    override fun onStart() {
        super.onStart()

        val d = dialog as AlertDialog
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            disposable.add(authService.changePassword(content.new_password.text.toString(), content.new_password_again.text.toString()).subscribe({
                showSuccess(context?.getString(R.string.password_changed))
                dismiss()
            }, {
                if(it is IllegalArgumentException) showError(it.message)
                else showError("Password could not be changed!")
            }))
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance() : ChangeLoginPasswordDialog {
            return ChangeLoginPasswordDialog()
        }
    }

}