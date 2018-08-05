package com.sunilson.firenote.presentation.elements.checklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import com.sunilson.firenote.presentation.shared.dialogs.ChecklistElementDialog
import com.sunilson.firenote.presentation.shared.dialogs.ConfirmDialog
import com.sunilson.firenote.presentation.shared.dialogs.ImportTextDialog
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.android.synthetic.main.base_element_activity.*
import kotlinx.android.synthetic.main.fragment_checklist.view.*
import javax.inject.Inject





class ChecklistFragment : BaseFragment(), ChecklistPresenterContract.View {

    @Inject
    lateinit var checklistPresenter: ChecklistPresenterContract.Presenter

    @Inject
    lateinit var checklistRecyclerAdapterFactory: ChecklistRecyclerAdapterFactory

    override val elementActivity: BaseElementPresenterContract.View?
        get() = activity as? BaseElementPresenterContract.View

    private lateinit var imm: InputMethodManager
    private lateinit var checklistRecyclerAdapter: ChecklistRecyclerAdapter

    override fun toggleLoading(loading: Boolean, message: String?) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_checklist, container, false)
        checklistRecyclerAdapter = checklistRecyclerAdapterFactory.create(View.OnClickListener {
            val position = view.checkListView.getChildLayoutPosition(it)
            checklistPresenter.changeChecklistElement(checklistRecyclerAdapter.data[position].apply {
                this.finished = !this.finished
            })
        }, View.OnLongClickListener {
            true
        }, { checklistPresenter.removeChecklistElement(it) }, view.checkListView)
        view.checkListView.adapter = checklistRecyclerAdapter
        view.checkListView.itemAnimator = ScaleInAnimator(OvershootInterpolator(1f))
        view.checkListView.itemAnimator.addDuration = 300
        view.checkListView.layoutManager = LinearLayoutManager(context)

        view.swipeContainerChecklist.setOnRefreshListener {
            checklistRecyclerAdapter.clear()
            checklistPresenter.refreshChecklistElements()
            Handler().postDelayed({
                view.swipeContainerChecklist.isRefreshing = false
            }, 200)
        }

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
            R.id.menu_import -> {
                val dialog = ImportTextDialog.newInstance()
                dialog.listener = object : DialogListener<String> {
                    override fun onResult(result: String?) {
                        result?.lines()?.forEach { line ->
                            if(line.isNotEmpty()) {
                                val element = ChecklistElement(text = line.substring(1).trim())
                                when {
                                    line[0] == '☒' -> element.finished = true
                                    line[0] == '☐' -> element.finished = false
                                    else -> element.finished = false
                                }
                                checklistPresenter.addChecklistElement(element)
                            }
                        }
                    }
                }
                dialog.show(childFragmentManager, "dialog")
            }
            R.id.clean_checklist -> {
                val dialogFragment = ConfirmDialog.newInstance(getString(R.string.remove_checked_items_title), getString(R.string.remove_checked_items))
                dialogFragment.listener = object: DialogListener<Boolean> {
                    override fun onResult(result: Boolean?) {
                        checklistRecyclerAdapter.data.forEach {
                            if(it.finished) checklistPresenter.removeChecklistElement(it)
                        }
                    }
                }
                dialogFragment.show(fragmentManager, "dialog")
            }
            R.id.menu_reminder -> {
                val shareBody = "${getString(R.string.element_checklist)} \"${element?.title}\" ${getString(R.string.from_app)}: \n $checklistRecyclerAdapter"
                val calIntent = Intent(Intent.ACTION_INSERT)
                calIntent.data = CalendarContract.Events.CONTENT_URI
                calIntent.type = "vnd.android.cursor.item/event"
                calIntent.putExtra(CalendarContract.Events.TITLE, element?.title + " - " + getString(R.string.app_name))
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, shareBody)
                startActivityForResult(calIntent, 123)
            }
            R.id.check_checklist -> {
                checklistRecyclerAdapter.checkAll()
            }
            R.id.menu_share -> {
                val shareBody = checklistRecyclerAdapter.toString()
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, element?.title)
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, ""))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun titleEditToggled(active: Boolean) {}
    override fun checklistElementAdded(checklistElement: ChecklistElement) = checklistRecyclerAdapter.add(checklistElement)
    override fun checklistElementChanged(checklistElement: ChecklistElement) {
        Handler().postDelayed({ checklistRecyclerAdapter.update(checklistElement) }, 200)
    }

    override fun clearAdapter() {
        checklistRecyclerAdapter.clear()
    }

    override fun checklistElementRemoved(checklistElement: ChecklistElement) = checklistRecyclerAdapter.remove(checklistElement)

    private fun openChecklistElementDialog() {
        val dialog = ChecklistElementDialog.newInstance()
        dialog.listener = object : DialogListener<String> {
            override fun onResult(result: String?) {
                if (result != null) {
                    checklistPresenter.addChecklistElement(
                            ChecklistElement(text = result,
                                    finished = false)
                    )
                }
            }
        }
        dialog.show(childFragmentManager, "dialog")
    }

    companion object {
        fun newInstance(): ChecklistFragment {
            return ChecklistFragment()
        }
    }
}