package com.sunilson.firenote.presentation.visibilityDialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseFragment
import com.sunilson.firenote.presentation.shared.colors
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import com.sunilson.firenote.presentation.shared.views.ColorElementView
import com.sunilson.firenote.presentation.visibilityDialog.adapters.ColorVisibilityAdapter
import kotlinx.android.synthetic.main.fragment_color.view.*
import javax.inject.Inject


class ColorFragment : BaseFragment() {

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    private var title: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_color, container, false)
        val colorVisibilityAdapter = ColorVisibilityAdapter(context!!, localSettingsManager)

        view.fragment_color_listview.adapter = colorVisibilityAdapter

        view.fragment_color_listview.setOnItemClickListener { _, view, position, _ ->
            val colorElementView = view as ColorElementView
            colorElementView.isChecked = !colorElementView.isChecked
            if (!colorElementView.isChecked) localSettingsManager.setColorVisibility(colorVisibilityAdapter.getItem(position).color, 1)
            else localSettingsManager.setColorVisibility(colorVisibilityAdapter.getItem(position).color, 1)
            (activity as HasElementList).adapter.hideElements()
        }

        view.checkAll.setOnClickListener { colorVisibilityAdapter.toggleAll(true) }
        view.uncheckAll.setOnClickListener { colorVisibilityAdapter.toggleAll(false) }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString("someTitle")
    }

    companion object {
        fun newInstance(): ColorFragment {
            return ColorFragment()
        }
    }
}