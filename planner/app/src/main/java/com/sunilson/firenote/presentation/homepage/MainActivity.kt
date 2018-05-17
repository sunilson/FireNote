package com.sunilson.firenote.presentation.homepage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.ItemTouchHelper.SimpleItemTouchHelperCallbackMain
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.addElementDialog.AddElementDialog
import com.sunilson.firenote.presentation.addElementDialog.AddElementListener
import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.dialogs.ListAlertDialog
import com.sunilson.firenote.presentation.dialogs.VisibilityDialog
import com.sunilson.firenote.presentation.elements.bundle.BundleActivity
import com.sunilson.firenote.presentation.elements.checklist.ChecklistActivity
import com.sunilson.firenote.presentation.elements.note.NoteActivity
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.adapters.elementList.ElementRecyclerAdapter
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.singletons.TutorialController
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), HomepagePresenterContract.IHomepageView, HasElementList {

    @Inject
    lateinit var presenter: HomepagePresenterContract.IHomepagePresenter

    @Inject
    lateinit var tutorialController: TutorialController

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    @Inject
    lateinit var elementRecyclerAdapterFactory: ElementRecyclerAdapter.ElementRecyclerAdapterFactory

    private val authListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        //TODO
    }

    //Click listeners
    private lateinit var recyclerViewClickListener: View.OnClickListener
    private lateinit var recyclerViewLongClickListener: View.OnLongClickListener

    override val mContext = this

    //Recyclerview stuff
    override lateinit var adapter: ElementRecyclerAdapter
    val layoutManager: LinearLayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //Initialize Recyclerview
        initClickListeners()
        activity_main_recycler_view.setHasFixedSize(true)
        adapter = elementRecyclerAdapterFactory.create(this, recyclerViewClickListener, recyclerViewLongClickListener, activity_main_recycler_view)
        val alphaAnimator = AlphaInAnimationAdapter(adapter)
        alphaAnimator.setFirstOnly(false)
        alphaAnimator.setDuration(200)
        activity_main_recycler_view.adapter = alphaAnimator
        activity_main_recycler_view.itemAnimator = ScaleInAnimator(OvershootInterpolator(1f))
        activity_main_recycler_view.itemAnimator.addDuration = 400
        activity_main_recycler_view.layoutManager = layoutManager
        val itemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallbackMain(adapter))
        itemTouchHelper.attachToRecyclerView(activity_main_recycler_view)

        //Initialize sorting list
        //TODO

        //Set sorting text
        if (localSettingsManager.getSortingMethod() != null) current_sorting_method.text = getString(R.string.current_sorthing_method) + " " + localSettingsManager.getSortingMethod()
        else current_sorting_method.text = getString(R.string.current_sorthing_method) + " " + getString(R.string.sort_ascending_name)

        if (FirebaseAuth.getInstance().currentUser != null) {
            presenter.loadData()
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    /**
     * Go to home screen on back press
     */
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logOut -> FirebaseAuth.getInstance().signOut()
            R.id.main_element_sort -> ListAlertDialog.newInstance(getString(R.string.menu_sort), "sort", null, null).show(supportFragmentManager, "dialog")
            R.id.main_element_visibility -> VisibilityDialog().show(supportFragmentManager, "dialog")
            R.id.action_bin -> startActivity(Intent(this, BinActivity::class.java))
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initClickListeners() {
        recyclerViewClickListener = View.OnClickListener {
            val element = adapter.data[activity_main_recycler_view.getChildLayoutPosition(it)]

            if (element.locked) {
                //TODO: Mit Callback
            } else {
                val intent: Intent? = when (element.noteType) {
                    "checklist" -> Intent(this, ChecklistActivity::class.java)
                    "note" -> Intent(this, NoteActivity::class.java)
                    "bundle" -> Intent(this, BundleActivity::class.java)
                    else -> null
                }
                intent?.putExtra("element", element)
            }
        }

        recyclerViewLongClickListener = View.OnLongClickListener {
            val element = adapter.data[activity_main_recycler_view.getChildLayoutPosition(it)]
            if (element.locked) {
                //TODO: Mit Callback
            } else {
                //TODO: Mit Callback
                ListAlertDialog.newInstance(getString(R.string.edit_element_title), "editElement", element.elementID, element.noteType).show(supportFragmentManager, "dialog")
            }
            true
        }

        //Toggle sorting list
        current_sorting_method.setOnClickListener {
            if (sorting_methods_list.visibility == View.GONE) sorting_methods_list.visibility = View.VISIBLE
            else if (sorting_methods_list.visibility == View.VISIBLE) sorting_methods_list.visibility = View.GONE
        }

        activity_main_swipe_refresh_layout.setOnRefreshListener { presenter.loadData() }
        fab_add_bundle.setOnClickListener { AddElementDialog.newInstance("", "bundle").show(supportFragmentManager, "dialog") }
        fab_add_checklist.setOnClickListener { AddElementDialog.newInstance("", "checklist").show(supportFragmentManager, "dialog") }
        fab_add_note.setOnClickListener { AddElementDialog.newInstance("", "note").show(supportFragmentManager, "dialog") }
        current_sorting_method.setOnClickListener {
            ListAlertDialog.newInstance(resources.getString(R.string.menu_sort), "sort", null, null).show(supportFragmentManager, "dialog")
        }
    }

    override fun showTutorial() {
        Handler().postDelayed({ tutorialController.showMainActivityTutorial(this) }, 500)
    }
    override fun toggleLoading(loading: Boolean, message: String?) {}
    override fun addElement(element: Element) = presenter.addElement(element)
    override fun elementAdded(element: Element) {}
    override fun listElements(elements: List<Element>) {}
    override fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
}