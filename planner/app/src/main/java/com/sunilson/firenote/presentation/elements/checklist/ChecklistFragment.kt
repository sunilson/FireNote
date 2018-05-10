package com.sunilson.firenote.presentation.elements.checklist

class ChecklistFragment : BaseElementFragment() {



    override fun showError(message: String?) {
    }

    override fun showSuccess(message: String?) {
    }

    override fun toggleLoading(loading: Boolean, message: String?) {
    }

    companion object {
        fun newInstance() : ChecklistFragment {
            return ChecklistFragment()
        }
    }
}