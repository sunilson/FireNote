package com.sunilson.firenote.presentation.homepage

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.authentication.AuthenticationActivity
import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapter
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapterFactory
import com.sunilson.firenote.presentation.homepage.adapters.SortingListArrayAdapter
import com.sunilson.firenote.presentation.homepage.adapters.SortingListArrayAdapterFactory
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.dialogs.MasterPasswordDialog
import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.VisibilityDialog
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.typeBundle
import com.sunilson.firenote.presentation.shared.typeChecklist
import com.sunilson.firenote.presentation.shared.typeNote
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sorting_list_layout.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HomepagePresenterContract.IHomepageView, HasElementList, View.OnClickListener {

    @Inject
    lateinit var presenter: HomepagePresenterContract.IHomepagePresenter

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    @Inject
    lateinit var elementRecyclerAdapterFactory: ElementRecyclerAdapterFactory

    @Inject
    lateinit var sortListingArrayAdapterFactory: SortingListArrayAdapterFactory

    //Click listeners
    private lateinit var recyclerViewClickListener: View.OnClickListener
    private lateinit var recyclerViewLongClickListener: View.OnLongClickListener
    private lateinit var sortingListArrayAdapter: SortingListArrayAdapter

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
        adapter = elementRecyclerAdapterFactory.create(recyclerViewClickListener, recyclerViewLongClickListener, { id, _ ->
            presenter.deleteElement(id)
        }, activity_main_recycler_view)
        activity_main_recycler_view.adapter = adapter
        activity_main_recycler_view.itemAnimator = ScaleInAnimator(OvershootInterpolator(1f))
        activity_main_recycler_view.itemAnimator.addDuration = 300
        activity_main_recycler_view.layoutManager = layoutManager

        //Initialize sorting list
        sortingListArrayAdapter = sortListingArrayAdapterFactory.create(this)
        sorting_methods_list.adapter = sortingListArrayAdapter
        sorting_methods_list.divider = null
        sorting_methods_list.dividerHeight = 0

        //Set sorting text
        if (localSettingsManager.getSortingMethod() != null) activity_main_sorting_bar_title.text = getString(R.string.current_sorthing_method) + " " + localSettingsManager.getSortingMethod()
        else activity_main_sorting_bar_title.text = getString(R.string.current_sorthing_method) + " " + getString(R.string.sort_ascending_name)

        handleIntent(intent)
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(intent)
        if (newIntent != null) handleIntent(newIntent)
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
            R.id.action_logOut -> presenter.signOut()
            R.id.main_element_sort -> toggleSorting()
            R.id.main_element_visibility -> VisibilityDialog.newInstance().show(supportFragmentManager, "dialog")
            R.id.action_bin -> startActivity(Intent(this, BinActivity::class.java))
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_web -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initClickListeners() {
        fab_add_note.setOnClickListener(this)
        fab_add_checklist.setOnClickListener(this)
        fab_add_bundle.setOnClickListener(this)
        activity_main_sorting_bar.setOnClickListener(this)

        recyclerViewClickListener = View.OnClickListener {
            val element = adapter.data[activity_main_recycler_view.getChildLayoutPosition(it)]
            openElement(element)
        }

        recyclerViewLongClickListener = View.OnLongClickListener {
            val element = adapter.data[activity_main_recycler_view.getChildLayoutPosition(it)]
            if (element.locked) {
            } else {
                ElementDialog.newInstance(getString(R.string.edit_element_title), element.elementID, element).show(supportFragmentManager, "dialog")
            }
            true
        }

        activity_main_swipe_refresh_layout.setOnRefreshListener {
            adapter.clear()
            presenter.loadElementData()
            activity_main_swipe_refresh_layout.isRefreshing = false
        }

        sorting_methods_list.setOnItemClickListener { _, _, position, _ ->
            toggleSorting()
            localSettingsManager.setSortingMethod(sortingListArrayAdapter.getItem(position).name)
            activity_main_sorting_bar_title.text = getString(R.string.sort_by, sortingListArrayAdapter.getItem(position).name)
            adapter.checkOrderAndVisibility()
        }
    }

    private fun toggleSorting() {
        val collapsed = activity_main_sorting_bar.layoutParams.height == resources.getDimensionPixelSize(R.dimen.sorting_layout_collapsed)

        val height = if (collapsed) resources.getDimensionPixelSize(R.dimen.sorting_layout_expanded)
        else resources.getDimensionPixelSize(R.dimen.sorting_layout_collapsed)

        val slideAnimator = ValueAnimator.ofInt(activity_main_sorting_bar.height, height).setDuration(200)
        slideAnimator.addUpdateListener {
            val value = it.animatedValue as Int
            val layoutparams = activity_main_sorting_bar.layoutParams
            layoutparams.height = value
            activity_main_sorting_bar.layoutParams = layoutparams
        }
        slideAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (collapsed) sorting_methods_list.visibility = View.VISIBLE
                else activity_main_sorting_bar_title.visibility = View.VISIBLE
            }
        })

        if (collapsed) activity_main_sorting_bar_title.visibility = View.GONE
        else sorting_methods_list.visibility = View.GONE

        slideAnimator.start()
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_CREATE_DOCUMENT) {
            when (intent.getStringExtra("noteType")) {
                typeNote, typeChecklist, typeBundle -> {
                    supportFragmentManager.fragments.forEach {
                        if (it is android.support.v4.app.DialogFragment) {
                            it.dismissAllowingStateLoss()
                        }
                    }
                    ElementDialog.newInstance(
                            getString(R.string.add_Element_Title),
                            intent.getStringExtra("noteType"))
                            .show(supportFragmentManager, "dialog")
                }
            }
        } else if (intent.action == Intent.ACTION_VIEW && intent.getStringExtra("openElement") != null) {
            val element = adapter.getElement(intent.getStringExtra("openElement"))
            if (element != null) openElement(element)
        }
    }

    private fun openElement(element: Element) {
        if (element.locked) {
            val dialog = MasterPasswordDialog.newInstance()
            dialog.listener = object : DialogListener<Boolean> {
                override fun onResult(result: Boolean?) {
                    if (result != null && result) {
                        val intent = Intent(this@MainActivity, ElementActivity::class.java)
                        intent.putExtra("elementID", element.elementID)
                        intent.putExtra("noteType", element.noteType)
                        intent.putExtra("elementColor", element.color)
                        startActivity(intent)
                    } else {
                        showError(getString(R.string.wrong_password))
                    }
                }
            }
            dialog.show(supportFragmentManager, "dialog")
        } else {
            val intent = Intent(this, ElementActivity::class.java)
            intent.putExtra("elementID", element.elementID)
            intent.putExtra("noteType", element.noteType)
            intent.putExtra("elementColor", element.color)
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_add_note -> {
                ElementDialog.newInstance(getString(R.string.add_Element_Title), typeNote).show(supportFragmentManager, "dialog")
                fab.collapse()
            }
            R.id.fab_add_checklist -> {
                ElementDialog.newInstance(getString(R.string.add_Element_Title), typeChecklist).show(supportFragmentManager, "dialog")
                fab.collapse()
            }
            R.id.fab_add_bundle -> {
                ElementDialog.newInstance(getString(R.string.add_Element_Title), typeChecklist).show(supportFragmentManager, "dialog")
                fab.collapse()
            }
            R.id.activity_main_sorting_bar -> toggleSorting()
        }
    }

    override fun loggedOut() {
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

    override fun toggleLoading(loading: Boolean, message: String?) {
        activity_main_swipe_refresh_layout.isRefreshing = loading
    }

    override fun elementAdded(element: Element) {
        adapter.add(element)
    }

    override fun elementChanged(element: Element) {
        adapter.update(element)
    }

    override fun elementRemoved(element: Element) {
        adapter.remove(element)
    }

    override fun clearAdapter() = adapter.clear()
}