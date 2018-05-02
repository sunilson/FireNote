package com.sunilson.firenote.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.adapters.BaseArrayAdapter

class DialogMenuAdapter(context: Context) : BaseArrayAdapter<DialogMenuAdapter.MenuItem>(context, resource) {

    fun add(text: String, iconDrawable: Int) {
        data.add(MenuItem(text, iconDrawable))
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        val elementHolder = if (v == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(resource, parent, false)
            val tempHolder = ElementHolder(v.findViewById(R.id.dialog_menu_text) , v.findViewById(R.id.dialog_menu_icon))
            v.tag = tempHolder
            tempHolder
        } else {
            convertView?.tag as ElementHolder
        }

        elementHolder.text.text = data[position].text
        elementHolder.icon.setImageResource(data[position].iconDrawable)
        return v!!
    }

    data class ElementHolder(var text: TextView, var icon: ImageView)
    data class MenuItem(var text: String, val iconDrawable: Int)
}