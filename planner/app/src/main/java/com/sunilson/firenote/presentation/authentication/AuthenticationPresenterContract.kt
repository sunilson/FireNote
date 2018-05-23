package com.sunilson.firenote.presentation.authentication

import android.content.Intent
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface AuthenticationPresenterContract {
    interface View : IBaseView{
        fun loggedIn()
        val presenter: Presenter
    }

    interface Presenter {
        fun signIn(email: String, password: String)
        fun startSocialSignIn()
        fun handleSOcialSignIn(data: Intent)
        fun register(email: String, password: String, repeatedPassword: String)
        fun resetPassword(email: String)
    }
}