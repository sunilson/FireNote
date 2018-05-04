package com.sunilson.firenote.presentation.shared.base.adapters

import android.content.Context
import android.widget.ArrayAdapter
import java.util.Comparator

open class BaseArrayAdapter<T>(context: Context, resource: Int, val data : MutableList<T> = mutableListOf()) : ArrayAdapter<T>(context, resource) {
    override fun getCount(): Int = data.size
    override fun getItem(position: Int): T = data[position]
    override fun add(`object`: T) {
        data.add(`object`)
        notifyDataSetChanged()
    }
    override fun sort(comparator: Comparator<in T>?) {
        data.sortedWith(comparator!!)
    }
}