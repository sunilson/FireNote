package com.sunilson.firenote.presentation.shared.dialogs.authenticationDialog

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
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.alertdialog_body_authenticate.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*
import javax.inject.Inject

class AuthenticationDialog : BaseDialogFragment() {

    @Inject
    lateinit var  authService: IAuthentication

    private val disposable = CompositeDisposable()
    private lateinit var content: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        titleView.dialog_title.text = getString(R.string.reauth_title)
        content = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_authenticate, null)
        builder.setView(content)

        builder.setPositiveButton(R.string.done, {_, _ -> })
        builder.setNegativeButton(R.string.cancel, { _, _ ->})

        content.google_sign_in.setOnClickListener {
            authService.startGoogleSignIn(activity!!)
        }

        val dialog = builder.create()
        return dialog
    }

    override fun onStart() {
        super.onStart()

        val d = dialog as AlertDialog
        d.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
            disposable.add(authService.emailSignIn(content.loginEmail.text.toString(), content.password.text.toString(), true).subscribe({
                //TODO Callback
                dismiss()
            }, {
                //TODO Callback
                showError(it.message)
            }))
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance() : AuthenticationDialog {
            return AuthenticationDialog()
        }
    }
}