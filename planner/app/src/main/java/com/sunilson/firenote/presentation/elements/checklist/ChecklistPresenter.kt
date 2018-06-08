package com.sunilson.firenote.presentation.elements.checklist

import com.sunilson.firenote.presentation.shared.base.BasePresenter
import javax.inject.Inject

class ChecklistPresenter @Inject constructor(val view : ChecklistPresenterContract.View) : BasePresenter(view), ChecklistPresenterContract.Presenter{
    override fun addChecklistElement() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeChecklistElement() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeChecklistElement() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadElementData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }
}