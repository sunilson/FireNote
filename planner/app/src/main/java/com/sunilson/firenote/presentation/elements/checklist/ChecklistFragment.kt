package com.sunilson.firenote.presentation.elements.checklist

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.android.synthetic.main.alertdialog_body_checklist_add.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*
import kotlinx.android.synthetic.main.base_element_activity.*
import kotlinx.android.synthetic.main.fragment_checklist.view.*
import javax.inject.Inject

class ChecklistFragment : BaseFragment(), ChecklistPresenterContract.View {

    @Inject
    lateinit var checklistPresenter: ChecklistPresenterContract.Presenter

    @Inject
    lateinit var checklistRecyclerAdapter: ChecklistRecyclerAdapter

    val elementActivity: BaseElementPresenterContract.View?
        get() = activity as? BaseElementPresenterContract.View

    override val element: Element?
        get() = elementActivity?.element

    private lateinit var imm: InputMethodManager

    override fun toggleLoading(loading: Boolean, message: String?) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_checklist, container, false)
        view.checkListView.adapter = checklistRecyclerAdapter
        view.checkListView.itemAnimator = ScaleInAnimator(OvershootInterpolator(1f))
        view.checkListView.itemAnimator.addDuration = 300
        view.checkListView.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        activity?.fab?.setOnClickListener { openChecklistElementDialog() }

        imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    override fun titleEditToggled(active: Boolean) {}
    override fun checklistElementAdded(checklistElement: ChecklistElement) = checklistRecyclerAdapter.add(checklistElement)
    override fun checklistElementChanged(checklistElement: ChecklistElement) {}
    override fun checklistElementRemoved(checklistElement: ChecklistElement) = checklistRecyclerAdapter.remove(checklistElement)

    private fun openChecklistElementDialog() {
        val alert = AlertDialog.Builder(context)

        val titleView = layoutInflater.inflate(R.layout.alertdialog_custom_title, null)
        titleView.dialog_title.text = getString(R.string.add_checklist_item_title)
        titleView.setBackgroundColor(element?.color ?: R.color.note_color_1)
        alert.setCustomTitle(titleView)

        val content = layoutInflater.inflate(R.layout.alertdialog_body_checklist_add, null)
        alert.setView(content)

        alert.setPositiveButton(R.string.confirm_add_dialog) { _, _ ->
            checklistPresenter.addChecklistElement(ChecklistElement(text = content.checklist_add_element_title.text.toString(), finished = false))
        }
        alert.setNegativeButton(R.string.cancel_add_dialog) { _, _ -> }

        val dialog = alert.create()
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.window.attributes.windowAnimations = R.style.dialogAnimation

        content.checklist_add_element_title.setOnEditorActionListener { _, i, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE) dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
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