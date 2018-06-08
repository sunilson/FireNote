package com.sunilson.firenote.presentation.homepage

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.authentication.AuthenticationActivity
import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapter
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapterFactory
import com.sunilson.firenote.presentation.homepage.adapters.SortingListArrayAdapter
import com.sunilson.firenote.presentation.homepage.adapters.SortingListArrayAdapterFactory
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.visibilityDialog.VisibilityDialog
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.sorting_list_layout.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HasSupportFragmentInjector, HomepagePresenterContract.IHomepageView, HasElementList, View.OnClickListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

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
        val alphaAnimator = AlphaInAnimationAdapter(adapter)
        alphaAnimator.setFirstOnly(false)
        alphaAnimator.setDuration(200)
        activity_main_recycler_view.adapter = alphaAnimator
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
            R.id.main_element_sort -> toggleSorting()
            R.id.main_element_visibility -> VisibilityDialog.newInstance().show(supportFragmentManager, "dialog")
            R.id.action_bin -> startActivity(Intent(this, BinActivity::class.java))
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
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

            if (element.locked) {
                //TODO: Mit Callback
            } else {
                val intent = Intent(this, ElementActivity::class.java)
                intent.putExtra("elementID", element.elementID)
                intent.putExtra("noteType", element.noteType)
                intent.putExtra("elementColor", element.color)
                startActivity(intent)
            }
        }

        recyclerViewLongClickListener = View.OnLongClickListener {
            val element = adapter.data[activity_main_recycler_view.getChildLayoutPosition(it)]
            if (element.locked) {
            } else {
            }
            true
        }

        activity_main_swipe_refresh_layout.setOnRefreshListener {
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_add_note -> {
                ElementDialog.newInstance(getString(R.string.add_Element_Title), "note").show(supportFragmentManager, "dialog")
                fab.collapse()
            }
            R.id.fab_add_checklist -> {
                ElementDialog.newInstance(getString(R.string.add_Element_Title), "checklist").show(supportFragmentManager, "dialog")
                fab.collapse()
            }
            R.id.fab_add_bundle -> {
                ElementDialog.newInstance(getString(R.string.add_Element_Title), "bundle").show(supportFragmentManager, "dialog")
                fab.collapse()
            }
            R.id.activity_main_sorting_bar -> toggleSorting()
        }
    }
    override fun loggedOut() {
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }
    override fun toggleLoading(loading: Boolean, message: String?) { activity_main_swipe_refresh_layout.isRefreshing = loading}
    override fun elementAdded(element: Element) { adapter.add(element) }
    override fun elementChanged(element: Element) { adapter.update(element) }
    override fun elementRemoved(element: Element) { adapter.remove(element.elementID) }
    override fun clearAdapter() = adapter.clear()
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector
}