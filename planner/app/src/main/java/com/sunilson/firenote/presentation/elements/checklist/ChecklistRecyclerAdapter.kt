package com.sunilson.firenote.presentation.elements.checklist

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.ChecklistElement
import com.sunilson.firenote.presentation.shared.base.adapters.BaseRecyclerAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import javax.inject.Inject

class ChecklistRecyclerAdapter constructor(
        context: Context,
        private val onClickListener: View.OnClickListener,
        private val onLongClickListener: View.OnLongClickListener,
        private val onSwipeListener: (ChecklistElement) -> Unit,
        private val recyclerView: RecyclerView)
    : BaseRecyclerAdapter<ChecklistElement>(context), ItemTouchHelperAdapter {

    init {
        //TODO Use Dagger
        ItemTouchHelper(SimpleItemTouchHelperCallbackChecklist(this)).attachToRecyclerView(recyclerView)
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.checklist_list_layout,
                parent,
                false
        )

        binding.root.setOnClickListener(onClickListener)
        binding.root.setOnLongClickListener(onLongClickListener)

        return ViewHolder(binding)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean = false
    override fun onItemDismiss(position: Int) {
        onSwipeListener(data[position])
        remove(data[position])
    }

    fun checkAll() {
        data.forEach {
            it.finished = true
        }

        notifyDataSetChanged()
    }
}

@FragmentScope
class ChecklistRecyclerAdapterFactory @Inject constructor(private val context: Context) {
    fun create(onClickListener: View.OnClickListener,
               onLongClickListener: View.OnLongClickListener,
               onSwipeListener: (ChecklistElement) -> Unit,
               recyclerView: RecyclerView): ChecklistRecyclerAdapter {
        return ChecklistRecyclerAdapter(context, onClickListener, onLongClickListener, onSwipeListener, recyclerView)
    }
}