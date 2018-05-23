package com.sunilson.firenote.presentation.shared.base

import android.content.Context
import android.support.v4.app.Fragment
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment : Fragment(), IBaseView {

    override val mContext: Context
        get() = activity as Context

    override fun toggleLoading(loading: Boolean, message: String?) {
        (activity as IBaseView).toggleLoading(loading, message)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}