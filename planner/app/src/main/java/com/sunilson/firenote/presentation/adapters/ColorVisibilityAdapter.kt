package com.sunilson.firenote.presentation.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.sunilson.firenote.Interfaces.MainActivityInterface
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.NoteColor
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.views.ColorElementView
import javax.inject.Inject

class ColorVisibilityAdapter(context: Context) : BaseArrayAdapter<NoteColor>(context, resource), CheckableArrayAdapter {

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    @Inject
    lateinit var mainActivity: MainActivityInterface

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
    }

    override fun toggleAll(checked: Boolean) {
        data.forEach {
            localSettingsManager.setColorVisibility(it.color, if (checked) -1 else 1)
            mainActivity.elementAdapter.hideElements()
        }
        notifyDataSetChanged()
    }

    private class ElementHolder (val icon : ImageView)
}