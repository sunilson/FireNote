package com.sunilson.firenote.presentation.shared.base.element.activities

import android.content.Context
import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.databinding.BaseElementActivityBinding
import com.sunilson.firenote.presentation.elements.note.NoteFragment
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.base.element.ElementFragment
import com.sunilson.firenote.presentation.shared.base.element.interfaces.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.base_element_activity.*
import java.util.*
import javax.inject.Inject

class ElementActivity : BaseActivity(), BaseElementPresenterContract.View {
    @Inject
    lateinit var presenter: BaseElementPresenterContract.Presenter

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    private lateinit var _element: Element
    private lateinit var binding: BaseElementActivityBinding

    override val element: Element
        get() = _element

    override val mContext: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        _element = Element(
                intent.getStringExtra("elementID"),
                Category("", intent.getStringExtra("categoryID")),
                intent.getStringExtra("elementType"),
                intent.getIntExtra("elementColor", 1),
                false,
                Date(),
                intent.getStringExtra("elementTitle"),
                intent.getStringExtra("parentID")
        )

        binding = DataBindingUtil.setContentView(this, R.layout.base_element_activity)
        binding.element = _element

        //Set element content
        when (_element.noteType) {
            "note" -> supportFragmentManager.beginTransaction().replace(R.id.base_element_activity_framelayout, NoteFragment.newInstance()).commit()
            "checklist" -> supportFragmentManager.beginTransaction().replace(R.id.base_element_activity_framelayout, NoteFragment.newInstance()).commit()
            "bundle" -> supportFragmentManager.beginTransaction().replace(R.id.base_element_activity_framelayout, NoteFragment.newInstance()).commit()
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title_edittext.isFocusable = false
        title_edittext.isFocusableInTouchMode = false
        title_edittext.setText(_element.title)
        setColors()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if((supportFragmentManager.fragments[0] as ElementFragment).canLeave()) finish()
            }
            R.id.menu_lock -> {
                if (localSettingsManager.getMasterPassword().isEmpty()) presenter.lockElement(!_element.locked)
                else Toast.makeText(this, R.string.master_password_not_set, Toast.LENGTH_LONG).show()
            }
            R.id.menu_settings -> { }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val lockButton = menu?.findItem(R.id.menu_lock)
        if (_element.locked) lockButton?.icon = ContextCompat.getDrawable(this, R.drawable.element_lock_icon)
        else lockButton?.icon = ContextCompat.getDrawable(this, R.drawable.ic_lock_open_white_24dp)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setColors() {
        val colorDrawable = ColorDrawable()
        colorDrawable.color = _element.color
        supportActionBar!!.setBackgroundDrawable(colorDrawable)

        fab.backgroundTintList = ColorStateList.valueOf(_element.color)

        //Darken notification bar color and set it to status bar. Only works in Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val hsv = FloatArray(3)
            Color.colorToHSV(_element.color, hsv)
            hsv[2] *= 0.6f

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.HSVToColor(hsv)
        }
    }

    override fun toggleTitleEdit(active: Boolean) {}

    override fun elementChanged(element: Element) {
        val colorChanged = this._element.color != element.color
        this._element = element
        this._element.notifyChange()
        if (colorChanged) setColors()
    }

    override fun elementRemoved() {
        Toast.makeText(this, R.string.element_removed, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun showError(message: String?) {}
    override fun showTutorial() {}
    override fun showSuccess(message: String?) {}
    override fun toggleLoading(loading: Boolean, message: String?) {}
}
