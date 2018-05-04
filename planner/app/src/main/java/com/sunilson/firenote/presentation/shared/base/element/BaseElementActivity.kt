package com.sunilson.firenote.presentation.shared.base.element

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.base.BaseContract

abstract class BaseElementActivity : BaseActivity(), BaseElementPresenterContract.IBaseElementView {

    lateinit var element: Element
    protected lateinit var titleEditText: EditText
    protected lateinit var titleDoneButton: ImageView
    protected lateinit var parentID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        element = Element(
                intent.getStringExtra("elementID"),
                Category("", intent.getStringExtra("categoryID")),
                intent.getStringExtra("elementType"),
                intent.getIntExtra("elementColor", 1),
                false,
                title = intent.getStringExtra("elementTitle")
        )
        parentID = intent.getStringExtra("parentID")

        when (element.noteType) {
            "note" -> setContentView(R.layout.activity_note)
            "checklist" -> setContentView(R.layout.activity_checklist)
            "bundle" -> setContentView(R.layout.activity_bundle)
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleEditText = findViewById(R.id.title_edittext)
        titleEditText.isFocusable = false
        titleEditText.isFocusableInTouchMode = false
        titleEditText.setText(element.title)
        titleDoneButton = findViewById(R.id.title_done_button)

        if (FirebaseAuth.getInstance().currentUser != null) {
            initialize()
        }
    }

    private fun setColors() {
        val colorDrawable = ColorDrawable()
        colorDrawable.color = element.color
        supportActionBar!!.setBackgroundDrawable(colorDrawable)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
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

    abstract fun showTutorial()
    abstract fun initialize()

    override fun addObserver(presenter: BaseContract.IBasePresenter) = lifecycle.addObserver(presenter)
    override fun elementChanged() {}
    override fun elementRemoved() {}
}