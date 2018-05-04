package com.sunilson.firenote.presentation.settings.adapters

import android.app.Dialog
import android.os.Bundle
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment

class ChangePasswordDialog : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        fun newInstance() : ChangePasswordDialog {
            return ChangePasswordDialog()
        }
    }
}