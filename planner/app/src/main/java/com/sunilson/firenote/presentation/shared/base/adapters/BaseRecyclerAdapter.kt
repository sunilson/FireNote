package com.sunilson.firenote.presentation.shared.base.adapters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import com.sunilson.firenote.BR

abstract class BaseRecyclerAdapter<T>(protected val context: Context) : RecyclerView.Adapter<BaseRecyclerAdapter<T>.ViewHolder>() {

    protected var _data = mutableListOf<T>()
    var data : List<T>
        get() = _data.toList()
        set(value) {
            _data = value.toMutableList()
            notifyDataSetChanged()
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