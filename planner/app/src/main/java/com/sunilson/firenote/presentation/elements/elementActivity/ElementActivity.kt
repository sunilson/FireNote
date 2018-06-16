package com.sunilson.firenote.presentation.elements.elementActivity

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.databinding.BaseElementActivityBinding
import com.sunilson.firenote.presentation.dialogs.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.elements.checklist.ChecklistFragment
import com.sunilson.firenote.presentation.elements.note.NoteFragment
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.base_element_activity.*
import javax.inject.Inject

class ElementActivity : BaseActivity(), BaseElementPresenterContract.View, HasSupportFragmentInjector {

    @Inject
    lateinit var presenter: BaseElementPresenterContract.Presenter

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var _element: Element? = null
    private lateinit var binding: BaseElementActivityBinding
    private var editMode: Boolean = false
    private lateinit var imm: InputMethodManager
    private var elementID: String = ""
    private var parent: String? = null
    private var lockButton: MenuItem? = null

    override val element: Element?
        get() = _element

    override val mContext: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        elementID = intent.getStringExtra("elementID")
        parent = intent.getStringExtra("parentID")
        binding = DataBindingUtil.setContentView(this, R.layout.base_element_activity)
        elementChanged(Element(elementID = elementID, noteType = intent.getStringExtra("noteType"), parent = parent, _color = intent.getIntExtra("elementColor", 123)))

        //Set element content
        when (intent.getStringExtra("noteType")) {
            "note" -> supportFragmentManager.beginTransaction().replace(R.id.base_element_activity_framelayout, NoteFragment.newInstance()).commit()
            "checklist" -> supportFragmentManager.beginTransaction().replace(R.id.base_element_activity_framelayout, ChecklistFragment.newInstance()).commit()
            "bundle" -> supportFragmentManager.beginTransaction().replace(R.id.base_element_activity_framelayout, NoteFragment.newInstance()).commit()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title_edittext.isFocusable = false
        title_edittext.isFocusableInTouchMode = false
        title_edittext.setOnClickListener { toggleTitleEdit(true) }
        title_edittext.setOnEditorActionListener { _, code, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || code == EditorInfo.IME_ACTION_DONE) {
                toggleTitleEdit(false)
                true
            } else {
                false
            }
        }

        title_done_button.setOnClickListener { toggleTitleEdit(false) }
    }

    override fun onStart() {
        super.onStart()
        presenter.loadElementData(elementID, parent)
    }

    override fun onBackPressed() {
        if (editMode) toggleTitleEdit(false)
        else if ((supportFragmentManager.fragments[0] as ElementContentPresenterContract.View).canLeave()) super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if ((supportFragmentManager.fragments[0] as ElementContentPresenterContract.View).canLeave()) finish()
            }
            R.id.menu_lock -> {
                presenter.lockElement(!_element!!.locked)
            }
            R.id.menu_settings -> {
                if(element != null) ElementDialog.newInstance("bla", element!!.elementID, element).show(supportFragmentManager, "dialog")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.element_base_menu, menu)
        lockButton = menu?.findItem(R.id.menu_lock)
        lockButton?.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_open_white_24dp)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setStatusBarColors() {
        //Darken notification bar color and set it to status bar. Only works in Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val hsv = FloatArray(3)
            Color.colorToHSV(element!!.color, hsv)
            hsv[2] *= 0.6f

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.HSVToColor(hsv)
        }
    }

    override fun toggleTitleEdit(active: Boolean) {
        if(editMode == active) return
        editMode = active
        if (editMode) {
            title_edittext.ellipsize = null
            title_edittext.isFocusableInTouchMode = true
            title_edittext.setFocusable(true)
            title_edittext.requestFocus()
            title_edittext.setSelection(title_edittext.text.length)
            title_edittext.setBackgroundColor(ContextCompat.getColor(this, R.color.tint_white))
            title_edittext.setTextColor(ContextCompat.getColor(this, R.color.title_text_color))
            title_done_button.visibility = View.VISIBLE
            imm.showSoftInput(title_edittext, InputMethodManager.SHOW_FORCED)
        } else {
            title_edittext.ellipsize = TextUtils.TruncateAt.END
            title_edittext.setSelection(0)
            title_edittext.isFocusableInTouchMode = false
            title_edittext.setFocusable(false);
            title_edittext.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            title_edittext.setTextColor(ContextCompat.getColor(this, R.color.tint_white))
            title_done_button.visibility = View.GONE
            imm.hideSoftInputFromWindow(title_edittext.windowToken, 0)
            element!!.title = title_edittext.text.toString()
            presenter.updateElement(element!!)
            (supportFragmentManager.fragments[0] as ElementContentPresenterContract.View).titleEditToggled(false)
        }
    }

    override fun elementChanged(element: Element) {

        _element = element
        element.parent = parent
        binding.element = _element

        /*if (_element == null) {
            _element = element
            element.parent = parent
            binding.element = _element
        } else {
            this.element!!.color = element.color
            this.element!!.title = element.title
            this.element!!.locked = element.locked
        }*/

        checkLockStatus()
        setStatusBarColors()
    }

    private fun checkLockStatus() {
        if (element!!.locked) lockButton?.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_outline_white_24dp)
        else lockButton?.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_open_white_24dp)
    }

    override fun elementRemoved() = finish()
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector
    override fun toggleLoading(loading: Boolean, message: String?) {}
}
