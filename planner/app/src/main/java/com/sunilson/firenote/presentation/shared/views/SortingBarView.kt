package com.sunilson.firenote.presentation.shared.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.homepage.adapters.SortingListArrayAdapter
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import kotlinx.android.synthetic.main.sorting_list_layout.view.*

class SortingBarView : LinearLayout {

    var sortingListArrayAdapter: SortingListArrayAdapter? = null
        set(value) {
            field = value
            //Initialize sorting list
            sorting_methods_list.adapter = value
            sorting_methods_list.divider = null
            sorting_methods_list.dividerHeight = 0

            //Set sorting text
            if (localSettingsManager?.getSortingMethod() != null) sorting_bar_title.text = context.getString(R.string.current_sorthing_method) + " " + localSettingsManager?.getSortingMethod()
            else sorting_bar_title.text = context.getString(R.string.current_sorthing_method) + " " + context.getString(R.string.sort_ascending_name)
        }

    var localSettingsManager: LocalSettingsManager? = null
    var sortingMethodChangedListener: (() -> Unit)? = null

    var title: String
        set(value) {
            field = value
            sorting_bar_title.text = value
            invalidate()
            requestLayout()
        }

    var toggled: Boolean
        set(value) {
            field = value
            val height = if (value) resources.getDimensionPixelSize(R.dimen.sorting_layout_expanded)
            else resources.getDimensionPixelSize(R.dimen.sorting_layout_collapsed)

            val slideAnimator = ValueAnimator.ofInt(sorting_bar_container.height, height).setDuration(200)
            slideAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutparams = sorting_bar_container.layoutParams
                layoutparams.height = value
                sorting_bar_container.layoutParams = layoutparams
            }

            slideAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    if (value) sorting_methods_list.visibility = View.VISIBLE
                    else sorting_bar_title.visibility = View.VISIBLE
                }
            })

            if (value) sorting_bar_title.visibility = View.GONE
            else sorting_methods_list.visibility = View.GONE

            slideAnimator.start()
        }

    fun toggle() {
        toggled = !toggled
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.sorting_list_layout, this, true)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SortingBarView, 0, 0)
        title = a.getString(R.styleable.SortingBarView_title)
        toggled = a.getBoolean(R.styleable.SortingBarView_toggled, false)
        a.recycle()

        sorting_methods_list.setOnItemClickListener { _, _, position, _ ->
            toggled = !toggled
            if (sortingListArrayAdapter != null) {
                localSettingsManager?.setSortingMethod(sortingListArrayAdapter!!.getItem(position).name)
                sorting_bar_title.text = context.getString(R.string.sort_by, sortingListArrayAdapter!!.getItem(position).name)
                sortingMethodChangedListener?.invoke()
            }
        }

        sorting_bar_container.setOnClickListener {
            toggle()
        }
    }
}