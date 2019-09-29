package com.sunilson.firenote.presentation.elements.elementList

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
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.adapters.AdapterElement
import com.sunilson.firenote.presentation.shared.base.adapters.BaseRecyclerAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.sortingMethods
import javax.inject.Inject

class ElementRecyclerAdapter constructor(
        context: Context,
        private val onClickListener: View.OnClickListener,
        private val onLongClickListener: View.OnLongClickListener,
        private val onSwipeListener: (String, String?) -> Unit,
        private val recyclerView: RecyclerView,
        val localSettingsManager: LocalSettingsManager) : BaseRecyclerAdapter<Element>(context), ItemTouchHelperAdapter {

    private var allItems = mutableListOf<Element>()
    var swipeable: Boolean = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        //TODO Use Dagger
        ItemTouchHelper(SimpleItemTouchHelperCallbackMain(this)).attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerAdapter<Element>.ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.element_list_layout,
                parent,
                false
        )

        binding.root.setOnClickListener(onClickListener)
        binding.root.setOnLongClickListener(onLongClickListener)

        return if (viewType == 0) ViewHolder(binding) else ViewHolder(binding, true)
    }

    override fun getItemViewType(position: Int): Int {
        if (!swipeable) return 0

        val element = _data[position]
        return if (element.locked) 0
        else 1
    }

    fun getElement(id: String): Element? {
        _data.forEach { if (it.elementID == id) return it }
        return null
    }

    override fun clear() {
        _data.clear()
        allItems.clear()
        notifyDataSetChanged()
    }

    override fun add(element: Element) {
        val existAllElement = allItems.find { it.elementID == element.elementID }
        if (existAllElement == null) allItems.add(element)
        else allItems[allItems.indexOf(existAllElement)] = element

        if (localSettingsManager.getCategoryVisibility(element.category.id) != -1 && localSettingsManager.getColorVisibility(element.color) != -1) {
            if (existAllElement == null) {
                _data.add(element)
                sort(localSettingsManager.getSortingMethod())
                notifyItemInserted(data.indexOf(element))
            } else {
                _data[_data.indexOf(existAllElement)] = element
                notifyDataSetChanged()
            }
        }
    }

    override fun update(element: AdapterElement) {
        val iterator = allItems.listIterator()
        for ((_, value) in iterator.withIndex()) {
            if (value.compareByString == element.compareByString) {
                iterator.set(element as Element)
            }
        }

        super.update(element)
    }

    fun checkOrderAndVisibility() {
        _data.clear()
        allItems.forEach {
            if (localSettingsManager.getCategoryVisibility(it.category.id) != -1 && localSettingsManager.getColorVisibility(it.color) != -1) {
                _data.add(it)
            }
        }
        sort(localSettingsManager.getSortingMethod())
        notifyDataSetChanged()
    }

    override fun remove(element: AdapterElement) {
        allItems = allItems.filter { it.elementID != element.compareByString }.toMutableList()
        super.remove(element)
    }

    fun sort(sortMethod: String) = _data.sortWith(context.sortingMethods().find { it.name == sortMethod }!!.comparator)
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean = false

    override fun onItemDismiss(position: Int) {
        val element = _data[position]
        _data.removeAt(position)
        allItems = allItems.filter { it.elementID != element.elementID }.toMutableList()
        notifyItemRemoved(position)
        onSwipeListener(element.elementID, element.parent)
    }

    inner class ViewHolder(binding: ViewDataBinding, val swipeable: Boolean = false) : BaseRecyclerAdapter<Element>.ViewHolder(binding)
}

@ActivityScope
class ElementRecyclerAdapterFactory @Inject constructor(private val localSettingsManager: LocalSettingsManager, private val context: Context) {
    fun create(onClickListener: View.OnClickListener,
               onLongClickListener: View.OnLongClickListener,
               onSwipeListener: (String, String?) -> Unit,
               recyclerView: RecyclerView): ElementRecyclerAdapter {
        return ElementRecyclerAdapter(context, onClickListener, onLongClickListener, onSwipeListener, recyclerView, localSettingsManager)
    }
}