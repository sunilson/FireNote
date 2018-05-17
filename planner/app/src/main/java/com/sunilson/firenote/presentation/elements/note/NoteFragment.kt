package com.sunilson.firenote.presentation.elements.note

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.text.util.Linkify
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import com.sunilson.firenote.presentation.shared.base.element.ElementFragment
import com.sunilson.firenote.presentation.shared.base.element.interfaces.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.singletons.ConnectivityManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.base_element_activity.*
import kotlinx.android.synthetic.main.fragment_note.*
import javax.inject.Inject

class NoteFragment : BaseFragment(), NotePresenterContract.INoteView, ElementFragment {

    @Inject
    lateinit var notePresenter: NotePresenterContract.INotePresenter

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    private val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private lateinit var elementActivity: BaseElementPresenterContract.View
    private var editMode = false

    override val element: Element
        get() = (activity as BaseElementPresenterContract.View).element

    override val mContext = activity as Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        elementActivity = activity as BaseElementPresenterContract.View
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        notepad.setOnTouchListener(object : View.OnTouchListener {
            private val gestureDetector = GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (!editMode) toggleEditMode()
                    return super.onDoubleTap(e)
                }
            })

            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(motionEvent)
                if (editMode) imm.showSoftInput(notepad, InputMethodManager.SHOW_FORCED)
                return false
            }
        })

        fab.setImageDrawable(activity?.getDrawable(R.drawable.ic_mode_edit_black_24dp))
        fab.setOnClickListener {
            toggleEditMode()
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_note, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    fun toggleEditMode() {
        if (!editMode) {
            elementActivity.toggleTitleEdit(true)
            imm.showSoftInput(notepad, InputMethodManager.SHOW_FORCED)
            fab.visibility = View.GONE
            editMode = true
            notepad.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            notepad.setSingleLine(false)
            notepad.clearFocus()
            notepad.requestFocus()
            notepad.linksClickable = false
            notepad.autoLinkMask = 0
            notepad.setText(notepad.text.toString())
            notepad.setSelection(notepad.text.length)
        } else {
            elementActivity.toggleTitleEdit(false)
            fab.visibility = View.VISIBLE
            imm.hideSoftInputFromWindow(notepad.windowToken, 0)
            notepad.clearFocus()
            notepad.linksClickable = true
            notepad.autoLinkMask = Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES
            notepad.setText(notepad.text.toString())
            notepad.inputType = InputType.TYPE_NULL
            notepad.setSingleLine(false)
            Toast.makeText(activity, R.string.stop_edit_mode, Toast.LENGTH_SHORT).show()
            finishTextEdit()
        }
    }

    override fun finishTextEdit() {
        editMode = false
        if (!connectivityManager.isConnected()) Toast.makeText(activity, R.string.edit_no_connection, Toast.LENGTH_LONG).show()
        notePresenter.storeNoteText(notepad.text.toString())
    }

    override fun titleEditToggled(active: Boolean) = toggleEditMode()
    override fun noteTextChanged(text: String) = notepad.setText(text)
    override fun toggleLoading(loading: Boolean, message: String?) {}
    override fun showTutorial() {}
    override fun canLeave(): Boolean {
        if (editMode) {
            toggleEditMode()
            return false
        }
        return true
    }

    companion object {
        fun newInstance(): NoteFragment {
            return NoteFragment()
        }
    }
}