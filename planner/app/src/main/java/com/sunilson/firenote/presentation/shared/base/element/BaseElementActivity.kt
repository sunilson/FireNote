package com.sunilson.firenote.presentation.shared.base.element

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
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.databinding.BaseElementActivityBinding
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.base_element_activity.*
import java.util.*
import javax.inject.Inject

abstract class BaseElementActivity<T> : BaseActivity(), BaseElementPresenterContract.IBaseElementView {

    @Inject
    lateinit var presenter: BaseElementPresenter<T>

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    lateinit var element: Element
    lateinit var binding: BaseElementActivityBinding
    protected lateinit var parentID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        element = Element(
                intent.getStringExtra("elementID"),
                Category("", intent.getStringExtra("categoryID")),
                intent.getStringExtra("elementType"),
                intent.getIntExtra("elementColor", 1),
                false,
                Date(),
                intent.getStringExtra("elementTitle")
        )
        parentID = intent.getStringExtra("parentID")

        binding = DataBindingUtil.setContentView(this, R.layout.base_element_activity)
        binding.element = element

        when (element.noteType) {
            "note" -> base_element_activity_container.addView(layoutInflater.inflate(R.layout.content_note, base_element_activity_container, false))
            "checklist" -> base_element_activity_container.addView(layoutInflater.inflate(R.layout.content_checklist, base_element_activity_container, false))
            "bundle" -> base_element_activity_container.addView(layoutInflater.inflate(R.layout.content_bundle, base_element_activity_container, false))
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title_edittext.isFocusable = false
        title_edittext.isFocusableInTouchMode = false
        title_edittext.setText(element.title)

        setColors()
        setFab()

        if (FirebaseAuth.getInstance().currentUser != null) {
            presenter.loadElementData(element.elementID, parentID)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {
            R.id.menu_lock -> {
                if (localSettingsManager.getMasterPassword().isEmpty()) {
                    presenter.lockElement(element.elementID, !element.locked)
                } else Toast.makeText(this, R.string.master_password_not_set, Toast.LENGTH_LONG).show()
            }
            R.id.menu_settings -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val lockButton = menu?.findItem(R.id.menu_lock)
        if (element.locked) lockButton?.icon = ContextCompat.getDrawable(this, R.drawable.element_lock_icon)
        else lockButton?.icon  = ContextCompat.getDrawable(this, R.drawable.ic_lock_open_white_24dp)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setFab() {
        when (element.noteType) {
            "note" -> {
            }
            "checklist" -> {
            }
            "bundle" -> {
            }
        }
    }

    private fun setColors() {
        val colorDrawable = ColorDrawable()
        colorDrawable.color = element.color
        supportActionBar!!.setBackgroundDrawable(colorDrawable)

        fab.backgroundTintList = ColorStateList.valueOf(element.color)

        //Darken notification bar color and set it to status bar. Only works in Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val hsv = FloatArray(3)
            Color.colorToHSV(element.color, hsv)
            hsv[2] *= 0.6f

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.HSVToColor(hsv)
        }
    }

    override fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
    override fun elementChanged(element: Element) {
        val colorChanged = this.element.color != element.color
        this.element = element
        this.element.notifyChange()
        if(colorChanged) setColors()
    }
    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        finish()
    }
    override fun elementRemoved() {
        Toast.makeText(this, R.string.element_removed, Toast.LENGTH_LONG).show()
        finish()
    }
}