package com.sunilson.firenote.presentation.shared.dialogs.authenticationDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IAuthentication
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogWithResult
import com.sunilson.firenote.presentation.shared.googleSignInRequestCode
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.alertdialog_body_authenticate.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*
import javax.inject.Inject

class AuthenticationDialog() : BaseDialogFragment(), DialogWithResult<Boolean> {

    @Inject
    lateinit var authService: IAuthentication

    private lateinit var content: View

    override var listener: DialogListener<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        titleView.dialog_title.text = getString(R.string.reauth_title)
        content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_authenticate, null)
        builder.setView(content)

        builder.setPositiveButton(R.string.login_button, { _, _ -> })
        builder.setNegativeButton(R.string.cancel, { _, _ -> })

        content.google_sign_in.setOnClickListener {
            authService.startGoogleSignIn(fragment = this)
        }

        val dialog = builder.create()
        return dialog
    }

    override fun onStart() {
        super.onStart()

        val d = dialog as AlertDialog
        d.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
            disposable.add(authService.emailSignIn(content.loginEmail.text.toString(), content.password.text.toString(), true).subscribe({
                listener?.onResult(true)
                dismiss()
            }, {
                Log.e("AuthenticationDialog", it.message, it)
                showError(it.message)
            }))
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googleSignInRequestCode && data != null) {
            disposable.add(authService.handleGoogleSignIn(data, true).subscribe({
                listener?.onResult(true)
                dismiss()
            }, {
                Log.e("AuthenticationDialog", it.message, it)
                showError(it.message)
            }))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener?.onResult(false)
        super.onDismiss(dialog)
    }

    companion object {
        fun newInstance(): AuthenticationDialog {
            return AuthenticationDialog()
        }
    }
}