package com.sunilson.firenote.presentation.shared.base.adapters

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.sunilson.firenote.BR

abstract class BaseRecyclerViewAdapter<T>(protected val context: Context) : RecyclerView.Adapter<BaseRecyclerViewAdapter<T>.ViewHolder>() {
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