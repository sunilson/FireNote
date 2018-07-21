package com.sunilson.firenote.presentation.homepage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.authentication.AuthenticationActivity
import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapter
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapterFactory
import com.sunilson.firenote.presentation.homepage.adapters.SortingListArrayAdapter
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.VisibilityDialog
import com.sunilson.firenote.presentation.shared.showSnackbar
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.typeBundle
import com.sunilson.firenote.presentation.shared.typeChecklist
import com.sunilson.firenote.presentation.shared.typeNote
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HomepagePresenterContract.IHomepageView, View.OnClickListener {

    @Inject
    lateinit var presenter: HomepagePresenterContract.IHomepagePresenter

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    @Inject
    lateinit var elementRecyclerAdapterFactory: ElementRecyclerAdapterFactory

    @Inject
    lateinit var sortListingArrayAdapter: SortingListArrayAdapter

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
        adapter = elementRecyclerAdapterFactory.create(recyclerViewClickListener, recyclerViewLongClickListener, { id, _ ->
            presenter.deleteElement(id)
            Handler().postDelayed({
                activity_main.showSnackbar(getString(R.string.moved_to_bin), true, getString(R.string.undo)) {
                    presenter.restoreElement(id)
                }
            }, 1500)
        }, activity_main_recycler_view)
        activity_main_recycler_view.adapter = adapter
        activity_main_recycler_view.itemAnimator = ScaleInAnimator(OvershootInterpolator(1f))
        activity_main_recycler_view.itemAnimator.addDuration = 300
        activity_main_recycler_view.layoutManager = layoutManager

        sorting_bar.sortingListArrayAdapter = sortListingArrayAdapter
        sorting_bar.localSettingsManager = localSettingsManager
        sorting_bar.sortingMethodChangedListener = {
            adapter.checkOrderAndVisibility()
        }

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
            R.id.main_element_sort -> sorting_bar.toggle()
            R.id.main_element_visibility -> VisibilityDialog.newInstance().show(supportFragmentManager, "dialog")
            R.id.action_bin -> {
                val intent = Intent(this, BinActivity::class.java)
                startActivity(intent)
            }
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.action_web -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initClickListeners() {
        fab_add_note.setOnClickListener(this)
        fab_add_checklist.setOnClickListener(this)
        fab_add_bundle.setOnClickListener(this)
        fab_add_gallery.setOnClickListener(this)

        recyclerViewClickListener = View.OnClickListener {
            val element = adapter.data[activity_main_recycler_view.getChildLayoutPosition(it)]
            openElement(element, this)
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
            if (element != null) openElement(element, this)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_add_note -> {
                openElementDialog(typeNote)
            }
            R.id.fab_add_checklist -> {
                openElementDialog(typeChecklist)
            }
            R.id.fab_add_bundle -> {
                openElementDialog(typeBundle)
            }
            R.id.fab_add_gallery -> {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    private fun openElementDialog(elementType: String) {
        ElementDialog.newInstance(getString(R.string.add_Element_Title), elementType).show(supportFragmentManager, "dialog")
        fab.collapse()
    }

    override fun loggedOut() {
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

    override fun toggleLoading(loading: Boolean, message: String?) {
        activity_main_swipe_refresh_layout.isRefreshing = loading
    }

    override fun clearAdapter() = adapter.clear()
}