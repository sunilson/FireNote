package com.sunilson.firenote.presentation.elements.checklist

import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract

interface ChecklistPresenterContract {
    interface View : ElementContentPresenterContract.View {
        fun checklistElementAdded()
        fun checklistElementChanged()
        fun checklistElementRemoved()
    }

    interface Presenter : ElementContentPresenterContract.Presenter {
        fun addChecklistElement(checklistElement: ChecklistElement)
        fun removeChecklistElement()
        fun changeChecklistElement()
    }
}