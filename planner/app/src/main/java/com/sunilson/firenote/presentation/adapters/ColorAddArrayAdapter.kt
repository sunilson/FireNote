package com.sunilson.firenote.presentation.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.NoteColor
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter
import com.sunilson.firenote.presentation.shared.views.ColorElementView

class ColorAddArrayAdapter(context: Context) : BaseArrayAdapter<NoteColor>(context, R.layout.color_list_layout) {

    var checked = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView as ColorElementView?
        val viewHolder = if (row == null) {
            row = ColorElementView(context)
            val tempHolder = ViewHolder()
            row.tag = tempHolder
            tempHolder
        } else {
            row.tag
        }
        row.tag = viewHolder
        row.setBackgroundColor(data[position].color)
        row.isChecked = (checked == position)
        return row
    }

    fun getPositionWithColor(color: Int): Int {
        for ((index, value) in data.withIndex()) {
            if (value.color == color) return index
        }

        return 0
    }

    fun setCheckedPosition(position: Int) {
        checked = position
    }

    fun uncheckAll() {
        checked = -1
        notifyDataSetChanged()
    }

    class ViewHolder
}