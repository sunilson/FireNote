package com.sunilson.firenote.presentation.bin

import android.os.Bundle
import android.sax.Element
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.sunilson.firenote.Interfaces.MainActivityInterface
import com.sunilson.firenote.ItemTouchHelper.SimpleItemTouchHelperCallbackMain
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.adapters.BinRecyclerAdapter
import com.sunilson.firenote.presentation.shared.base.BaseActivity
import com.sunilson.firenote.presentation.shared.base.BaseContract
import kotlinx.android.synthetic.main.content_bin.*
import javax.inject.Inject

class BinActivity : BaseActivity(), BinPresenterContract.View {
    @Inject
    lateinit var mainActivity: MainActivityInterface

    @Inject
    lateinit var binPresenter: BinPresenter

    private lateinit var elementID : String
    private lateinit var elementName: String
    private val adapter = BinRecyclerAdapter(this, {

    }, {
        true
    })

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
        binList.adapter = adapter
        ItemTouchHelper(SimpleItemTouchHelperCallbackMain(adapter)).attachToRecyclerView(binList)

        binPresenter.setView(this)
    }

    override fun elementAdded(element: Element) {}

    override fun elementChanged(element: Element) {}
    override fun elementRemoved(element: Element) {}

    override fun addObserver(presenter: BaseContract.IBasePresenter) = lifecycle.addObserver(presenter)
}