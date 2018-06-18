package com.sunilson.firenote.presentation.shared.base.adapters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.sunilson.firenote.BR

abstract class BaseRecyclerAdapter<T : AdapterElement>(protected val context: Context) : RecyclerView.Adapter<BaseRecyclerAdapter<T>.ViewHolder>() {

    protected var _data = mutableListOf<T>()
    var data: List<T>
        get() = _data.toList()
        set(value) {
            _data = value.toMutableList()
            notifyDataSetChanged()
        }

    open fun clear() {
        _data.clear()
        notifyDataSetChanged()
    }

    open fun add(element: T) {
        _data.add(element)
        notifyItemInserted(_data.indexOf(element))
    }

    open fun remove(element: AdapterElement) {
        val iterator = _data.listIterator()
        for ((index, value) in iterator.withIndex()) {
            if (value.compareByString == element.compareByString) {
                iterator.remove()
                notifyItemRemoved(index)
            }
        }
    }

    open fun update(element: AdapterElement) {
        val iterator = _data.listIterator()
        for ((index, value) in iterator.withIndex()) {
            if (value.compareByString == element.compareByString) {
                iterator.set(element as T)
                //notifyItemChanged(index)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    open inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: T) {
            binding.setVariable(BR.obj, obj)
            binding.executePendingBindings()
        }
    }
}