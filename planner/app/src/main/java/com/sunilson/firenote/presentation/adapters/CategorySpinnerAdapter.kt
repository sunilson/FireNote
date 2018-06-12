package com.sunilson.firenote.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.presentation.shared.base.adapters.BaseSpinnerArrayAdapter
import com.sunilson.firenote.presentation.shared.categories
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope

@DialogFragmentScope
class CategorySpinnerAdapter constructor(context: Context)
    : BaseSpinnerArrayAdapter<Category>(context) {

    init {
        data = context.categories()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView

        val viewHolder = if (row == null) {
            row = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false)
            val tempViewholder = ViewHolder(row.findViewById(android.R.id.text1))
            row.tag = tempViewholder
            tempViewholder
        } else {
            row.tag as ViewHolder
        }
        viewHolder.textView.text = data[position].name

        row!!.setPadding(0, row.paddingTop, row.paddingRight, row.paddingBottom)
        return row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView

        val viewHolder = if (row == null) {
            row = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
            val tempViewholder = ViewHolder(row.findViewById(android.R.id.text1))
            row.tag = tempViewholder
            tempViewholder
        } else {
            row.tag as ViewHolder
        }
        viewHolder.textView.text = data[position].name
        return row!!
    }

    fun setCheckedCategory(id: String) {

    }

    fun getPositionWithId(id: String): Int {
        for ((index, category) in data.withIndex()) {
            if (category.id == id) return index
        }
        return 0
    }
}