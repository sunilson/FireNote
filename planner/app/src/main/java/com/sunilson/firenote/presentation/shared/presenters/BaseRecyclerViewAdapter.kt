package com.sunilson.firenote.presentation.shared.presenters

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(protected val context: Context) : RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder>() {
    val data = mutableListOf<T>()

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: T) {
            binding.setVariable(BR.obj, obj)
            binding.executePendingBindings()
        }
    }
}