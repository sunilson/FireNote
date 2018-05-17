package com.sunilson.firenote.presentation.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.sunilson.firenote.Interfaces.MainActivityInterface
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.NoteColor
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.views.ColorElementView
import javax.inject.Inject

class ColorVisibilityAdapter(context: Context, val localSettingsManager: LocalSettingsManager) : BaseArrayAdapter<NoteColor>(context, R.layout.color_list_layout), CheckableArrayAdapter {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView as ColorElementView?

        val elementHolder =  if(row == null) {
            row = ColorElementView(context)
            val tempHolder = ColorVisibilityAdapter.ElementHolder(row.findViewById(R.id.color_list_icon))
            row.tag = tempHolder
            tempHolder
        } else {
            row.tag as ColorVisibilityAdapter.ElementHolder
        }

        row.tag = elementHolder

        return row
    }

    override fun toggleAll(checked: Boolean) {
        data.forEach {
            localSettingsManager.setColorVisibility(it.color, if (checked) -1 else 1)
            (context as HasElementList).adapter.hideElements()
        }
        notifyDataSetChanged()
    }

    private class ElementHolder (val icon : ImageView)
}