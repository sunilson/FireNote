package com.sunilson.firenote.presentation.authentication.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import com.sunilson.firenote.presentation.shared.interfaces.CanNavigateFragments
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_start.view.*

class StartFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        view.go_to_login.setOnClickListener { (activity as CanNavigateFragments).navigateTo(LoginFragment.newInstance()) }
        view.go_to_register.setOnClickListener { (activity as CanNavigateFragments).navigateTo(RegisterFragment.newInstance()) }
        return view
    }

    override val mContext: Context
        get() = activity as Context

    companion object {
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }
}