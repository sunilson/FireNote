package com.sunilson.firenote.presentation.authentication

import android.content.Context
import android.os.Bundle
import com.sunilson.firenote.presentation.shared.base.BaseActivity

class AuthenticationActivity : BaseActivity() {

    override val mContext: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}
    override fun showTutorial() {}
}