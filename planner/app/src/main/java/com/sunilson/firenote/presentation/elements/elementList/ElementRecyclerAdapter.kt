package com.sunilson.firenote.presentation.elements.elementList

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.adapters.BaseRecyclerAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.sortingMethods
import javax.inject.Inject

class ElementRecyclerAdapter constructor(
        context: Context,
        val onClickListener: View.OnClickListener,
        val onLongClickListener: View.OnLongClickListener,
        val recyclerView: RecyclerView,
        val localSettingsManager: LocalSettingsManager) : BaseRecyclerAdapter<Element>(context), ItemTouchHelperAdapter {

    private var allItems = mutableListOf<Element>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.element_list_layout,
                parent,
                false
        )
        return ViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        val element = _data[position]
        return if (element.locked) 0
        else 1
    }

    fun getElement(id: String): Element? {
        _data.forEach { if (it.elementID == id) return it }
        return null
    }

    fun clear() {
        _data.clear()
        allItems.clear()
        notifyDataSetChanged()
    }

    fun add(element: Element): Int {
        allItems.add(element)
        var position = 0
        _data.add(element)
        notifyItemInserted(data.indexOf(element))
        if (localSettingsManager.getCategoryVisibility(element.category.id) != -1 && localSettingsManager.getColorVisibility(element.color) != -1) {
            _data.add(element)
            sort(localSettingsManager.getSortingMethod())
            position = data.indexOf(element)
            notifyItemInserted(position)
        }
        return position
    }

    fun hideElements() {
        _data.clear()
        allItems.forEach {
            if (localSettingsManager.getCategoryVisibility(it.category.id) != -1 && localSettingsManager.getColorVisibility(it.color) != -1) {
                _data.add(it)
            }
        }
        sort(localSettingsManager.getSortingMethod())
        notifyDataSetChanged()
    }

    fun remove(id: String) {
        allItems = allItems.filter { it.elementID != id }.toMutableList()
        val iterator = _data.listIterator()
        for ((index, value) in iterator.withIndex()) {
            if (value.elementID == id) {
                iterator.remove()
                notifyItemRemoved(index)
            }
        }
    }

    fun update(element: Element) {
        val iterator = _data.listIterator()
        for ((index, value) in iterator.withIndex()) {
            if (value.elementID == element.elementID) {
                iterator.set(element)
                notifyItemChanged(index)
            }
        }
    }

    fun sort(sortMethod: String) = _data.sortWith(context.sortingMethods().find { it.name == sortMethod }!!.comparator)

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return false
    }

    override fun onItemDismiss(position: Int) {
    }

    inner class ViewHolder(binding: ViewDataBinding, val swipeable: Boolean = false) : BaseRecyclerAdapter<Element>.ViewHolder(binding)
}

@ActivityScope
class ElementRecyclerAdapterFactory @Inject constructor(private val localSettingsManager: LocalSettingsManager) {
    fun create(context: Context,
               onClickListener: View.OnClickListener,
               onLongClickListener: View.OnLongClickListener,
               recyclerView: RecyclerView): ElementRecyclerAdapter {
        return ElementRecyclerAdapter(context, onClickListener, onLongClickListener, recyclerView, localSettingsManager)
    }
}