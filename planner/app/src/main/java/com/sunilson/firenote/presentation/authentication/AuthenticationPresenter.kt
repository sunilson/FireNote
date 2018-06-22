package com.sunilson.firenote.presentation.authentication

import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IAuthentication
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class AuthenticationPresenter @Inject constructor(val view: AuthenticationPresenterContract.View, private val authService : IAuthentication)
    : AuthenticationPresenterContract.Presenter, BasePresenter(view), GoogleApiClient.OnConnectionFailedListener {

    private val authListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser != null) {
            if(it.currentUser?.isEmailVerified == false) {
                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                view.showSuccess(view.mContext!!.getString(R.string.register_success))
            }
            view.loggedIn()
        }
    }

    override fun onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        super.onStart()
    }

    override fun onStop() {
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        super.onStop()
    }

    override fun signIn(email: String, password: String) {
        view.toggleLoading(true)
        disposable.add(authService.emailSignIn(email, password).subscribe({
            view.showSuccess(view.mContext!!.getString(R.string.logged_in))
        }, {
            view.toggleLoading(false)
            if(it is IllegalArgumentException) view.showError(it.message)
            else view.showError("Sign in Failed!")
        }))
    }

    override fun startSocialSignIn() {
        authService.startGoogleSignIn(view.mContext!! as Activity)
        view.toggleLoading(true)
    }

    override fun handleSocialSignIn(data: Intent) {
        disposable.add(authService.handleGoogleSignIn(data).subscribe( {
            view.showSuccess(view.mContext!!.getString(R.string.logged_in))
        }, {
            view.toggleLoading(false)
            view.showError(it.message)
        }))
    }

    override fun register(email: String, password: String, repeatedPassword: String) {
        view.toggleLoading(true)
        disposable.add(authService.register(email, password, repeatedPassword).subscribe({
            view.toggleLoading(false)
        }, {
            when (it) {
                is FirebaseAuthWeakPasswordException -> view.showError(view.mContext!!.getString(R.string.error_register_password_weak))
                is FirebaseAuthInvalidCredentialsException -> view.showError(view.mContext!!.getString(R.string.error_register_invalid_email))
                is FirebaseAuthUserCollisionException -> view.showError(view.mContext!!.getString(R.string.error_register_user_already_exists))
                else -> view.showError(it.message)
            }
        }))
    }

    override fun resetPassword(email: String) {
        view.toggleLoading(true)
        disposable.add(authService.passwordReset(email).subscribe({
            view.showSuccess(view.mContext!!.getString(R.string.reset_success))
        }, {
            view.toggleLoading(false)
            if(it is IllegalArgumentException) view.showError(it.message)
            else view.showError(view.mContext!!.getString(R.string.reset_failed))
        }))
    }

    override fun onConnectionFailed(p0: ConnectionResult) = view.showError(view.mContext!!.getString(R.string.login_error))
}