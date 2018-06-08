package com.sunilson.firenote.presentation.elements.checklist

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import kotlinx.android.synthetic.main.alertdialog_body_checklist_add.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*
import kotlinx.android.synthetic.main.base_element_activity.*
import javax.inject.Inject

class ChecklistFragment : BaseFragment(), ChecklistPresenterContract.View {

    @Inject
    lateinit var checklistPresenter: ChecklistPresenterContract.Presenter

    val elementActivity: BaseElementPresenterContract.View?
        get() = activity as? BaseElementPresenterContract.View

    override val element: Element?
        get() = elementActivity?.element

    override fun toggleLoading(loading: Boolean, message: String?) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_checklist, container, false)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        activity?.fab?.setImageDrawable(activity?.getDrawable(R.drawable.ic_add_white_24dp))
        activity?.fab?.setOnClickListener { openChecklistElementDialog() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_checklist, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    override fun titleEditToggled(active: Boolean) {
    }

    override fun checklistElementAdded() {
    }

    override fun checklistElementChanged() {
    }

    override fun checklistElementRemoved() {
    }

    private fun openChecklistElementDialog() {
        val alert = AlertDialog.Builder(context)

        val titleView = layoutInflater.inflate(R.layout.alertdialog_custom_title, null)
        titleView.dialog_title.text = getString(R.string.add_checklist_item_title)
        titleView.setBackgroundColor(element?.color ?: R.color.note_color_1)
        alert.setCustomTitle(titleView)

        val content = layoutInflater.inflate(R.layout.alertdialog_body_checklist_add, null)
        alert.setView(content)

        alert.setPositiveButton(R.string.confirm_add_dialog) { _, _ ->
            checklistPresenter.addChecklistElement(ChecklistElement(content.checklist_add_element_title.text.toString(), false))
        }
        alert.setNegativeButton(R.string.cancel_add_dialog) {_, _ ->}

        val dialog = alert.create()
        dialog.window.attributes.windowAnimations = R.style.dialogAnimation

        content.checklist_add_element_title.setOnEditorActionListener { _, i, keyEvent ->
            if(keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
            false
        }

        dialog.show()
    }

    companion object {
        fun newInstance(): ChecklistFragment {
            return ChecklistFragment()
        }
    }
}