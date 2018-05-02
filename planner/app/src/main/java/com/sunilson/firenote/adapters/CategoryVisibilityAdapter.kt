package com.sunilson.firenote.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.sunilson.firenote.Interfaces.MainActivityInterface
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.views.CategoryVisibilityView
import javax.inject.Inject

class CategoryVisibilityAdapter(context: Context, resource: Int) : BaseArrayAdapter<Category>(context, resource), CheckableArrayAdapter {

    @Inject
    lateinit var mainActivity: MainActivityInterface

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager


    override fun toggleAll(checked: Boolean) {
        data.forEach {
            localSettingsManager.setCategoryVisiblity(it.id, if (checked) 1 else -1)
            mainActivity.elementAdapter.hideElements()
        }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView as CategoryVisibilityView?

        val elementHolder = if (row == null) {
            row = CategoryVisibilityView(context)
            val tempHolder = CategoryVisibilityAdapter.ElementHolder(row.findViewById(R.id.category_element_text))
            row.tag = tempHolder
            tempHolder
        } else {
            row.tag as ElementHolder
        }

        val category = data[position]
        elementHolder.elementText.text = category.name
        row.isChecked = localSettingsManager.getCategoryVisibility(category.id) != 1

        return row
    }

    private class ElementHolder(val elementText: TextView)
}