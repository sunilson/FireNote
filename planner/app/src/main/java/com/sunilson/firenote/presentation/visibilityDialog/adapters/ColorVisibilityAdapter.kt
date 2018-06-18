package com.sunilson.firenote.presentation.visibilityDialog.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.NoteColor
import com.sunilson.firenote.presentation.adapters.CheckableArrayAdapter
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter
import com.sunilson.firenote.presentation.shared.colors
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.views.ColorElementView

class ColorVisibilityAdapter(context: Context, val localSettingsManager: LocalSettingsManager)
    : BaseArrayAdapter<NoteColor>(context, R.layout.color_list_layout), CheckableArrayAdapter {

    init {
        _data = context.colors().toMutableList()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView as ColorElementView?

        val elementHolder = if (row == null) {
            row = ColorElementView(context, true)
            val tempHolder = ElementHolder(row.findViewById(R.id.color_list_icon))
            row.tag = tempHolder
            tempHolder
        } else {
            row.tag as ElementHolder
        }

        val color = data[position]
        row.setBackgroundColor(color.color)
        row.isChecked = localSettingsManager.getColorVisibility(color.color) == -1
        row.tag = elementHolder
        return row
    }

    override fun toggleAll(checked: Boolean) {
        data.forEach {
            localSettingsManager.setColorVisibility(it.color, if (checked) -1 else 1)
            if(context is HasElementList) (context as HasElementList).adapter.checkOrderAndVisibility()
        }
        notifyDataSetChanged()
    }

    private class ElementHolder(val icon: ImageView)
}