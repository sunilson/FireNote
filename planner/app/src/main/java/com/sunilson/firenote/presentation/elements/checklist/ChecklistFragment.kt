package com.sunilson.firenote.presentation.elements.checklist

import android.os.Bundle
import android.view.*
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.base.BaseFragment
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

    companion object {
        fun newInstance(): ChecklistFragment {
            return ChecklistFragment()
        }
    }
}