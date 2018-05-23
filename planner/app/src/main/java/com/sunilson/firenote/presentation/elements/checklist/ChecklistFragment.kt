package com.sunilson.firenote.presentation.elements.checklist

import android.content.Context
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import javax.inject.Inject

class ChecklistFragment : BaseFragment() {

    @Inject
    lateinit var notePresenter: ChecklistPresenterContract.Presenter

    override val mContext = activity as Context

    override fun toggleLoading(loading: Boolean, message: String?) {
    }

    companion object {
        fun newInstance() : ChecklistFragment {
            return ChecklistFragment()
        }
    }
}