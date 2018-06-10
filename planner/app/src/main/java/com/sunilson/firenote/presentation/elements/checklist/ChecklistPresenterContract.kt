package com.sunilson.firenote.presentation.elements.checklist

import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract

interface ChecklistPresenterContract {
    interface View : ElementContentPresenterContract.View {
        fun checklistElementAdded(checklistElement: ChecklistElement)
        fun checklistElementChanged(checklistElement: ChecklistElement)
        fun checklistElementRemoved(checklistElement: ChecklistElement)
    }

    interface Presenter : ElementContentPresenterContract.Presenter {
        fun addChecklistElement(checklistElement: ChecklistElement)
        fun removeChecklistElement(checklistElement: ChecklistElement)
        fun changeChecklistElement(checklistElement: ChecklistElement)
    }
}