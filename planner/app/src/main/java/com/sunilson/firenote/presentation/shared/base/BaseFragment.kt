package com.sunilson.firenote.presentation.shared.base

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment : Fragment(), IBaseView {

    override val mContext: Context?
        get() = activity

    override fun toggleLoading(loading: Boolean, message: String?) {
        (activity as IBaseView).toggleLoading(loading, message)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}