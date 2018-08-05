package com.sunilson.firenote.presentation.shared.base.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.TextView

open class BaseSpinnerArrayAdapter<T>(context: Context, var data: List<T> = listOf()) : ArrayAdapter<T>(context, android.R.layout.simple_list_item_1) {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): T = data[position]

    class ViewHolder (val textView: TextView)
}