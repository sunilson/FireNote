package com.sunilson.firenote.presentation.homepage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.SortingMethod
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.sortingMethods
import javax.inject.Inject

@ActivityScope
class SortingListArrayAdapter @Inject constructor(context: Context,
                                          val localSettingsManager: LocalSettingsManager)
    : BaseArrayAdapter<SortingMethod>(context, 0) {

    init {
        data = context.sortingMethods()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView

        val viewHolder = if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.sorting_list_row, null, false)
            val tempHolder = ViewHolder(view!!.findViewById(R.id.sorting_list_row_text), view.findViewById(R.id.sorting_list_row_icon))
            view.tag = tempHolder
            tempHolder
        } else {
            view.tag as ViewHolder
        }

        viewHolder.textView.text = context.getString(R.string.sort_by, data[position].name)
        viewHolder.icon.setImageDrawable(context.getDrawable(data[position].icon))

        return view
    }

    class ViewHolder(val textView: TextView, val icon: ImageView)
}
