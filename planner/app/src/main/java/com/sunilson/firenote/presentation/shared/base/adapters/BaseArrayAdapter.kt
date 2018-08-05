package com.sunilson.firenote.presentation.shared.base.adapters

import android.content.Context
import android.widget.ArrayAdapter
import java.util.Comparator

open class BaseArrayAdapter<T>(context: Context, resource: Int) : ArrayAdapter<T>(context, resource) {

    protected var _data : MutableList<T> = mutableListOf()
    var data : List<T>
        get() = _data.toList()
        set(value) {
            _data = value.toMutableList()
            notifyDataSetChanged()
        }

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): T = data[position]
    override fun add(`object`: T) {
        _data.add(`object`)
        notifyDataSetChanged()
    }
    override fun sort(comparator: Comparator<in T>?) {
        data.sortedWith(comparator!!)
    }
}