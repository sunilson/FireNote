package com.sunilson.firenote.presentation.authentication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_reset_password.*

class ResetPasswordFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reset_password, container, false)
        return view
    }

    override fun toggleLoading(loading: Boolean, message: String?) {
        if(loading) fragment_reset_progress.visibility = View.VISIBLE
        else fragment_reset_progress.visibility = View.GONE
    }

    companion object {
        fun newInstance() : ResetPasswordFragment {
            return ResetPasswordFragment()
        }
    }
}