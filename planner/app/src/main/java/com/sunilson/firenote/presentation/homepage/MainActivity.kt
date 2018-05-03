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
import com.sunilson.firenote.adapters.ElementRecyclerAdapter
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.dialogs.ListAlertDialog
import com.sunilson.firenote.presentation.dialogs.VisibilityDialog
import com.sunilson.firenote.presentation.elements.bundle.BundleActivity
import com.sunilson.firenote.presentation.elements.checklist.ChecklistActivity
import com.sunilson.firenote.presentation.elements.note.NoteActivity
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.activities.BaseActivity
import com.sunilson.firenote.presentation.shared.presenters.BaseContract
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.singletons.TutorialController
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import javax.inject.Inject

class MainActivity : BaseActivity(), HomepagePresenterContract.View {

    @Inject
    lateinit var presenter: HomepagePresenterContract.Presenter

    @Inject
    lateinit var tutorialController: TutorialController

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    //Click listeners
    private lateinit var recyclerViewClickListener: View.OnClickListener
    private lateinit var recyclerViewLongClickListener: View.OnLongClickListener

    //Recyclerview stuff
    lateinit var adapter: ElementRecyclerAdapter
    val layoutManager: LinearLayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //Initialize Recyclerview
        initClickListeners()
        activity_main_recycler_view.setHasFixedSize(true)
        adapter = ElementRecyclerAdapter(this, recyclerViewClickListener, recyclerViewLongClickListener, activity_main_recycler_view)
        val alphaAnimator = AlphaInAnimationAdapter(adapter)
        alphaAnimator.setFirstOnly(false)
        alphaAnimator.setDuration(200)
        activity_main_recycler_view.adapter = alphaAnimator
        activity_main_recycler_view.itemAnimator = ScaleInAnimator(OvershootInterpolator(1f))
        activity_main_recycler_view.itemAnimator.addDuration = 400
        activity_main_recycler_view.layoutManager = layoutManager
        val itemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallbackMain(adapter))
        itemTouchHelper.attachToRecyclerView(activity_main_recycler_view)

        //Set sorting text
        if (localSettingsManager.getSortingMethod() != null) current_sorting_method.text = getString(R.string.current_sorthing_method) + " " + localSettingsManager.getSortingMethod()
        else current_sorting_method.text = getString(R.string.current_sorthing_method) + " " + getString(R.string.sort_ascending_name)

        Handler().postDelayed({ tutorialController.showMainActivityTutorial(this) }, 500)

        if (FirebaseAuth.getInstance().currentUser != null) {
            presenter.setView(this)
            presenter.loadData()
        }
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
            R.id.main_element_sort -> ListAlertDialog.newInstance(getString(R.string.menu_sort), "sort", null, null).show(fragmentManager, "dialog")
            R.id.main_element_visibility -> VisibilityDialog().show(fragmentManager, "dialog")
            R.id.action_bin -> startActivity(Intent(this, BinActivity::class.java))
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initClickListeners() {
        recyclerViewClickListener = View.OnClickListener {
            val element = adapter.list[activity_main_recycler_view.getChildLayoutPosition(it)]

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
            val element = adapter.list[activity_main_recycler_view.getChildLayoutPosition(it)]
            if (element.locked) {
                //TODO: Mit Callback
            } else {
                //TODO: Mit Callback
                ListAlertDialog.newInstance(getString(R.string.edit_element_title), "editElement", element.elementID, element.noteType).show(fragmentManager, "dialog")
            }
            true
        }

        current_sorting_method.setOnClickListener {
            //TODO: Mit Callback
            ListAlertDialog.newInstance(resources.getString(R.string.menu_sort), "sort", null, null).show(fragmentManager, "dialog")
        }

        activity_main_swipe_refresh_layout.setOnRefreshListener { presenter.loadData() }

        fab.setOnClickListener {
            //TODO: Mit Callback
            ListAlertDialog.newInstance(resources.getString(R.string.add_Element_Title), "addElement", null, null).show(fragmentManager, "dialog")
        }

        current_sorting_method.setOnClickListener { ListAlertDialog.newInstance(resources.getString(R.string.menu_sort), "sort", null, null).show(fragmentManager, "dialog") }
    }

    override fun listElements(elements: List<Element>) {

    }

    override fun addObserver(presenter: BaseContract.IBasePresenter) = lifecycle.addObserver(presenter)
}