package com.sunilson.firenote.presentation.authentication.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.authentication.AuthenticationPresenterContract
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import com.sunilson.firenote.presentation.shared.interfaces.CanNavigateFragments
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.forgotPasswordLink.setOnClickListener(this)
        view.loginButton.setOnClickListener(this)
        view.google_sign_in.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.forgotPasswordLink -> (activity as CanNavigateFragments).navigateTo(ResetPasswordFragment.newInstance())
            R.id.loginButton -> (activity as AuthenticationPresenterContract.View).presenter.signIn(loginEmail.text.toString(), loginPassword.text.toString())
            R.id.google_sign_in -> (activity as AuthenticationPresenterContract.View).presenter.startSocialSignIn()
        }
    }

    override fun toggleLoading(loading: Boolean, message: String?) {
        loginButton.isEnabled = !loading
        google_sign_in.isEnabled = !loading

        if(loading) {
            fragment_login_progress?.visibility = View.VISIBLE
        } else{
            fragment_login_progress?.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance() : LoginFragment {
            return LoginFragment()
        }
    }
}