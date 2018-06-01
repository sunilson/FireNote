package com.sunilson.firenote.presentation.visibilityDialog.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.presentation.adapters.CheckableArrayAdapter
import com.sunilson.firenote.presentation.shared.base.adapters.BaseArrayAdapter
import com.sunilson.firenote.presentation.shared.categories
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager

class CategoryVisibilityAdapter constructor(
        context: Context,
        var localSettingsManager: LocalSettingsManager) : BaseArrayAdapter<Category>(context, R.layout.category_list_layout), CheckableArrayAdapter {

    init {
        _data = context.categories().toMutableList()
    }

    override fun toggleAll(checked: Boolean) {
        data.forEach {
            localSettingsManager.setCategoryVisiblity(it.id, if (checked) 1 else -1)
            if(context is HasElementList) (context as HasElementList).adapter.checkOrderAndVisibility()
        }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView as CategoryVisibilityView?

        val elementHolder = if (row == null) {
            row = CategoryVisibilityView(context)
            val tempHolder = ElementHolder(row.findViewById(R.id.category_element_text))
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

    /*
    @ActivityScope
    class CategoryVisibilityAdapterFactory @Inject constructor(private val localSettingsManager: LocalSettingsManager) {
        fun create(context: Context): CategoryVisibilityAdapter {
            return CategoryVisibilityAdapter(context, localSettingsManager)
        }
    }
    */

    private class ElementHolder(val elementText: TextView)
}


