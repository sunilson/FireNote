package com.sunilson.firenote.presentation.elements.note

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import com.sunilson.firenote.presentation.shared.singletons.ConnectivityManager
import kotlinx.android.synthetic.main.base_element_activity.*
import kotlinx.android.synthetic.main.fragment_note.*
import javax.inject.Inject



class NoteFragment : BaseFragment(), NotePresenterContract.INoteView {

    @Inject
    lateinit var notePresenter: NotePresenterContract.INotePresenter

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    private lateinit var imm: InputMethodManager
    private var editMode = false

    val elementActivity: BaseElementPresenterContract.View?
        get() = activity as? BaseElementPresenterContract.View

    override val element: Element?
        get() = elementActivity?.element

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        activity?.fab?.setOnClickListener { if(editMode) stopEditMode() else startEditMode() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gestureDetector = GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (!editMode) startEditMode()
                return super.onDoubleTap(e)
            }
        })

        notepad_disabled.setOnTouchListener { _, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
            if (editMode) imm.showSoftInput(notepad_disabled, InputMethodManager.SHOW_FORCED)
            false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_note, container, false)
        return view
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

    fun startEditMode() {
        if(editMode) return
        elementActivity?.toggleTitleEdit(true)
        imm.showSoftInput(notepad, InputMethodManager.SHOW_FORCED)
        activity?.fab?.visibility = View.GONE
        editMode = true
        notepad_disabled.visibility = View.INVISIBLE
        notepad.visibility = View.VISIBLE
        notepad.requestFocus()
        /*
        notepad.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        notepad.setSingleLine(false)
        notepad.clearFocus()
        notepad.requestFocus()
        notepad.linksClickable = false
        notepad.autoLinkMask = 0
        notepad.setText(notepad.text.toString())
        notepad.setSelection(notepad.text.length)
        */
    }

    override fun stopEditMode(saveNote: Boolean) {
        if(!editMode) return
        elementActivity?.toggleTitleEdit(false)
        activity?.fab?.visibility = View.VISIBLE
        imm.hideSoftInputFromWindow(notepad.windowToken, 0)
        editMode = false
        notepad_disabled.visibility = View.VISIBLE
        notepad.clearFocus()
        notepad.visibility = View.INVISIBLE
        if(saveNote) {
            if (!connectivityManager.isConnected()) Toast.makeText(activity, R.string.edit_no_connection, Toast.LENGTH_LONG).show()
            notePresenter.storeNoteText(notepad.text.toString())
            notepad_disabled.text = notepad.text.toString()
        }
    }

    override fun titleEditToggled(active: Boolean) {
        if(active) startEditMode()
        else stopEditMode()
    }
    override fun noteTextChanged(text: String) {
        notepad.setText(text)
        notepad_disabled.text = text
    }
    override fun toggleLoading(loading: Boolean, message: String?) {}
    override fun canLeave(): Boolean {
        if (editMode) {
            stopEditMode()
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