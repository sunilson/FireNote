package com.sunilson.firenote.presentation.bin

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapter
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapterFactory
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.dialogs.ConfirmDialog
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import kotlinx.android.synthetic.main.activity_bin.*
import javax.inject.Inject

class BinActivity : BaseActivity(), BinPresenterContract.View, HasElementList {

    @Inject
    lateinit var binPresenter: BinPresenter

    @Inject
    lateinit var elementRecyclerAdapterFactory: ElementRecyclerAdapterFactory

    private lateinit var elementID: String
    private lateinit var elementName: String
    override lateinit var adapter: ElementRecyclerAdapter

    override val mContext: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bin)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        elementID = intent.getStringExtra("elementID")
        elementName = intent.getStringExtra("elementName")

        //Initialize list
        binList.setHasFixedSize(true)
        binList.layoutManager = LinearLayoutManager(this)
        adapter = elementRecyclerAdapterFactory.create(View.OnClickListener { _ ->
            val dialog = ConfirmDialog.newInstance("", "")
            dialog.listener = object: DialogListener<Boolean> {
                override fun onResult(result: Boolean?) {
                    if(result == true) {

                    }
                }
            }
            dialog.show(supportFragmentManager, "dialog")
        }, View.OnLongClickListener { _ ->  true}, { _, _ -> }, binList)
        binList.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.bin_delete_all -> {
                val dialog = ConfirmDialog.newInstance("", "")
                dialog.listener = object: DialogListener<Boolean> {
                    override fun onResult(result: Boolean?) {
                        if(result == true) {

                        }
                    }
                }
                dialog.show(supportFragmentManager, "dialog")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}
}