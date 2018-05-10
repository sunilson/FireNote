package com.sunilson.firenote.presentation.homepage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import javax.inject.Inject

@ActivityScope
class SortingListArrayAdapter @Inject constructor(
        context: MainActivity,
        val localSettingsManager: LocalSettingsManager) : BaseArrayAdapter<String>(context, 123) {

    init {
        //TODO Sorting methods laden
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView

        val viewHolder = if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.sorting_list_row, null, false)
            val tempHolder = ViewHolder(view!!.findViewById(), view.findViewById())
            view.tag = tempHolder
            tempHolder
        } else {
            view.tag as ViewHolder
        }
        viewHolder.textView.text = ""
        return view
    }

    class ViewHolder(val textView: TextView, val icon: ImageView)
}

