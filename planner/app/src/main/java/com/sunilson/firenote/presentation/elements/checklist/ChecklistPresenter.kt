package com.sunilson.firenote.presentation.elements.checklist

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import javax.inject.Inject

class ChecklistPresenter @Inject constructor(val view : ChecklistPresenterContract.View, private val repository: IRepository)
    : BasePresenter(view), ChecklistPresenterContract.Presenter{


    override fun refreshChecklistElements() {
        disposable.dispose()
        loadElementData()
    }

    override fun loadElementData() {
        disposable.add(repository.loadChecklistElements(FirebaseAuth.getInstance().currentUser!!.uid, view.element!!.elementID).subscribe {
            when(it.first) {
                ChangeType.ADDED -> view.checklistElementAdded(it.second)
                ChangeType.CHANGED -> view.checklistElementChanged(it.second)
                ChangeType.REMOVED -> view.checklistElementRemoved(it.second)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        loadElementData()
    }

    override fun onStop() {
        super.onStop()
        view.clearAdapter()
        disposable.dispose()
    }

    override fun addChecklistElement(checklistElement: ChecklistElement) {
        repository.addChecklistElement(FirebaseAuth.getInstance().currentUser!!.uid, view.element!!.elementID, checklistElement)
    }

    override fun removeChecklistElement(checklistElement: ChecklistElement) {
        repository.removeChecklistElement(FirebaseAuth.getInstance().currentUser!!.uid, view.element!!.elementID, checklistElement)
    }

    override fun changeChecklistElement(checklistElement: ChecklistElement) {
        repository.updateChecklistElement(FirebaseAuth.getInstance().currentUser!!.uid, view.element!!.elementID, checklistElement)
    }
}