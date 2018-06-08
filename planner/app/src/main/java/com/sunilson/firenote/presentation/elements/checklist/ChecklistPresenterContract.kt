package com.sunilson.firenote.presentation.elements.checklist

import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract

interface ChecklistPresenterContract {
    interface View : ElementContentPresenterContract.View {
        fun checklistElementAdded()
        fun checklistElementChanged()
        fun checklistElementRemoved()
    }

    interface Presenter : ElementContentPresenterContract.Presenter {
        fun addChecklistElement()
        fun removeChecklistElement()
        fun changeChecklistElement()
    }
}