package com.sunilson.firenote.presentation.shared.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

abstract class BaseFragment : Fragment(), IBaseView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showTutorial()
    }

    override fun showError(message: String?) {}
    override fun showSuccess(message: String?) {}
    override fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
}