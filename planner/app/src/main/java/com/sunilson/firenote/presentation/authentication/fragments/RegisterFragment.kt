package com.sunilson.firenote.presentation.authentication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.authentication.AuthenticationPresenterContract
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*

class RegisterFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        view.registerSubmitButton.setOnClickListener {
            (activity as AuthenticationPresenterContract.View).presenter.register(
                    registerEmail.text.toString(),
                    registerPassword.text.toString(),
                    registerPasswordAgain.text.toString())
        }
        return view
    }

    override fun toggleLoading(loading: Boolean, message: String?) {
        registerSubmitButton.isEnabled = !loading

        if (loading) fragment_register_progress.visibility = View.VISIBLE
        else fragment_register_progress.visibility = View.GONE
    }

    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}