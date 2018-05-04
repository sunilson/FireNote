package com.sunilson.firenote.adapters

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.Interfaces.ItemTouchHelperAdapter
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.data.models.ElementComparators
import com.sunilson.firenote.presentation.shared.base.adapters.BaseRecyclerAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import javax.inject.Inject

@ActivityScope
class ElementRecyclerAdapter @Inject constructor(context: AppCompatActivity,
                             val onClickListener: View.OnClickListener,
                             val onLongClickListener: View.OnLongClickListener,
                             val recyclerView: RecyclerView) : BaseRecyclerAdapter<Element>(context), ItemTouchHelperAdapter {

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

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
        val element = recyclerData[position]
        return if (element.locked) 0
        else 1
    }

    fun getElement(id: String): Element? {
        recyclerData.forEach { if (it.elementID == id) return it }
        return null
    }

    /*

    fun add(element: Element): Int {
        if (context is MainActivity) {
            var position = 0
            if (localSettingsManager.getCategoryVisibility(element.category.id) != -1 && localSettingsManager.getColorVisibility(element.color) != -1) {
                list.add(element)
                val sort = LocalSettingsManager.getInstance().sortingMethod
                if (sort != null) {
                    sort(LocalSettingsManager.getInstance().sortingMethod)
                } else {
                    sort(context.getString(R.string.sort_ascending_name))
                }
                position = list.indexOf(element)
                notifyItemInserted(position)
            }
            allItems.add(element)
            return position
        } else {
            return 2
        }
    }

    fun remove(id: String): Int {
        if (context is MainActivity) removeFromAllItems(id)

        val iterator = list.iterator()
        for ((index, value) in iterator.withIndex()) {
            if (value.elementID == id) {
                iterator.remove()
                if (index == list.size) notifyDataSetChanged()
                else notifyItemRemoved(index)
                return index
            }
        }
    }

    fun update(element: Element) {

    }

    fun removeFromAllItems(id: String) {
        val iterator = allItems.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().elementID == id) {
                iterator.remove()
                return
            }
        }
    }

      */

    fun sort(sortMethod: String) {
        when (sortMethod) {
            context.resources.getString(R.string.sort_descending_date) -> recyclerData.sortWith(ElementComparators.sortByName(true))
            context.resources.getString(R.string.sort_ascending_date) -> recyclerData.sortWith(ElementComparators.sortByDate(false))
            context.resources.getString(R.string.sort_descending_name) -> recyclerData.sortWith(ElementComparators.sortByName(true))
            context.resources.getString(R.string.sort_ascending_name) -> recyclerData.sortWith(ElementComparators.sortByName(false))
            context.resources.getString(R.string.sort_category_name) -> recyclerData.sortWith(ElementComparators.sortByCategory())
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
    }

    override fun onItemDismiss(position: Int) {
    }
}