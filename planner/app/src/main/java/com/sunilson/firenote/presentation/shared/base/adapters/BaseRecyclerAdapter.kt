package com.sunilson.firenote.presentation.shared.base.adapters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class BaseRecyclerAdapter<T>(protected val context: Context) : RecyclerView.Adapter<BaseRecyclerAdapter<T>.ViewHolder>() {
    protected var recyclerData = mutableListOf<T>()

    fun setData(newData: List<T>) {
        this.recyclerData = newData.toMutableList()
    }

    override fun getItemCount(): Int = recyclerData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(recyclerData[position])

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: T) {
            binding.setVariable(BR.obj, obj)
            binding.executePendingBindings()
        }
    }
}