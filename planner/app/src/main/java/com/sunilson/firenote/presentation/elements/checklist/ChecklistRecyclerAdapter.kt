package com.sunilson.firenote.presentation.elements.checklist

import android.view.ViewGroup
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import com.sunilson.firenote.presentation.shared.base.adapters.BaseRecyclerAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import javax.inject.Inject

@FragmentScope
class ChecklistRecyclerAdapter @Inject constructor(activity: ElementActivity)
    : BaseRecyclerAdapter<ChecklistElement>(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}