package com.sunilson.firenote.presentation.homepage

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.adapters.ElementRecyclerAdapter
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.bundle.BundleActivity
import com.sunilson.firenote.presentation.checklist.ChecklistActivity
import com.sunilson.firenote.presentation.dialogs.ListAlertDialog
import com.sunilson.firenote.presentation.dialogs.VisibilityDialog
import com.sunilson.firenote.presentation.note.NoteActivity
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.BaseActivity
import com.sunilson.firenote.presentation.shared.BaseContract
import com.sunilson.firenote.presentation.shared.TutorialController
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), HomepagePresenterContract.HomepageView {

    @Inject
    lateinit var presenter: HomepagePresenterContract.HomepagePresenter

    @Inject
    lateinit var tutorialController: TutorialController

    //Click listeners
    lateinit var recyclerViewClickListener: View.OnClickListener
    lateinit var recyclerViewLongClickListener: View.OnLongClickListener

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
        activity_main_recycler_view.adapter = adapter
        activity_main_recycler_view.layoutManager = layoutManager

        Handler().postDelayed({
            tutorialController.showMainActivityTutorial(this)
        }, 500)

        presenter.setView(this)
        presenter.loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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

    fun initClickListeners() {
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
                ListAlertDialog.newInstance(getString(R.string.edit_element_title), "editElement", element.elementID, element.noteType)
                        .show(supportFragmentManager, "dialog")
            }
            true
        }

        current_sorting_method.setOnClickListener {
            //TODO: Mit Callback
            ListAlertDialog.newInstance(resources.getString(R.string.menu_sort), "sort", null, null)
                    .show(supportFragmentManager, "dialog")
        }

        activity_main_swipe_refresh_layout.setOnRefreshListener {
            //TODO: Refresh data
        }

        fab.setOnClickListener {
            //TODO: Mit Callback
            ListAlertDialog.newInstance(resources.getString(R.string.add_Element_Title), "addElement", null, null)
                    .show(supportFragmentManager, "dialog")
        }
    }

    override fun displayList(elements: ArrayList<Element>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun elementAdded(element: Element) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun elementChanged(element: Element) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun elementRemoved(element: Element) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addObserver(presenter: BaseContract.IBasePresenter) = lifecycle.addObserver(presenter)
}