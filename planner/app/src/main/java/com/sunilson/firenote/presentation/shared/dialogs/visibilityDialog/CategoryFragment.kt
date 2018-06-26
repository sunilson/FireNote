package com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.adapters.CategoryVisibilityAdapter
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.adapters.CategoryVisibilityView
import kotlinx.android.synthetic.main.fragment_category.view.*
import javax.inject.Inject


class CategoryFragment : BaseFragment() {

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        val categoryVisibilityAdapter = CategoryVisibilityAdapter(context!!, localSettingsManager)
        view.fragment_category_listview.adapter = categoryVisibilityAdapter

        view.fragment_category_listview.setOnItemClickListener { _, v, position, _ ->
            val categoryVisibilityView = v as CategoryVisibilityView
            categoryVisibilityView.isChecked = !categoryVisibilityView.isChecked
            if (categoryVisibilityView.isChecked) localSettingsManager.setCategoryVisiblity(categoryVisibilityAdapter.getItem(position).id, -1)
            else localSettingsManager.setCategoryVisiblity(categoryVisibilityAdapter.getItem(position).id, 1)

            (activity as HasElementList).adapter.checkOrderAndVisibility()
        }

        view.checkAll.setOnClickListener { categoryVisibilityAdapter.toggleAll(true) }
        view.uncheckAll.setOnClickListener { categoryVisibilityAdapter.toggleAll(false) }

        return view
    }

    companion object {
        fun newInstance(): CategoryFragment {
            return CategoryFragment()
        }
    }
}