package com.sunilson.firenote.presentation.elements.checklist

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.presentation.shared.base.adapters.BaseRecyclerAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import javax.inject.Inject

@FragmentScope
class ChecklistRecyclerAdapter @Inject constructor(context: Context)
    : BaseRecyclerAdapter<ChecklistElement>(context) {

    override fun toString(): String {
        var result = ""

        data.forEach {
            val checkbox =
                    if (it.finished) "☒"
                    else "☐"
            result += checkbox + " " + it.text + "\n"
        }

        return result
    }

    fun remove(checklistElement: ChecklistElement) {
        var removedIndex = -1
        _data = _data.filterIndexed { index, value ->
            if(checklistElement.id == value.id) removedIndex = index
            checklistElement.id != value.id
        }.toMutableList()
        if(removedIndex >= 0) notifyItemRemoved(removedIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.checklist_list_layout,
                parent,
                false
        )

        //binding.root.setOnClickListener(onClickListener)
        //binding.root.setOnLongClickListener(onLongClickListener)
        return ViewHolder(binding)
    }
}