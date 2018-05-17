package com.sunilson.firenote.presentation.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter

class CategorySpinnerAdapter(context: Context, data : List<Category> = listOf()) : BaseArrayAdapter<Category>(context, 1, data.toMutableList()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //TODO use category name to display
        return super.getView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //TODO display category name
        return super.getDropDownView(position, convertView, parent)
    }

    fun getPositionWithId(id: String) : Int{
        for((index, category) in data.withIndex()) {
            if (category.id == id) return index
        }
        return 0
    }

    class ViewHolder(val textView: TextView)

}