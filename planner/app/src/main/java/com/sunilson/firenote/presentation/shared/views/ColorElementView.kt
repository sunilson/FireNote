package com.sunilson.firenote.presentation.shared.views

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.LinearLayout
import com.sunilson.firenote.R
import kotlinx.android.synthetic.main.color_list_layout.view.*

class ColorElementView(context: Context) : LinearLayout(context), Checkable {

    private var checked = false

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.color_list_layout, this, true)
    }

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        checked != checked
        setChecked(checked)
    }

    override fun setChecked(checked: Boolean) {
        this.checked = checked
        if (checked) {
            val drawable = ColorDrawable(ContextCompat.getColor(context, R.color.color_overlay))
            color_list_layout.foreground = drawable
            color_list_icon.visibility = View.VISIBLE
        } else {
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            val ta = context.obtainStyledAttributes(attrs)
            color_list_layout.foreground = ta.getDrawable(0)
            ta.recycle()
            color_list_icon.visibility = View.GONE
        }
    }
}