package com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.adapters

import android.content.Context
import android.graphics.Paint
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.LinearLayout
import com.sunilson.firenote.R
import kotlinx.android.synthetic.main.category_list_layout.view.*

class CategoryVisibilityView(context: Context) : LinearLayout(context), Checkable {

    val v: View
    private var mChecked: Boolean = false

    init {
        v = LayoutInflater.from(context).inflate(R.layout.category_list_layout, this, true)
    }

    override fun isChecked(): Boolean = mChecked

    override fun toggle() {
        mChecked != mChecked
        this.isChecked = mChecked
    }

    override fun setChecked(checked: Boolean) {
        mChecked = checked

        if (mChecked) {
            category_element_text.paintFlags = category_element_text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            category_element_text.setTextColor(ContextCompat.getColor(context, R.color.text_crossed_out))
            category_element_checkBox.isChecked = false
        } else {
            category_element_text.paintFlags = category_element_text.paintFlags and  Paint.STRIKE_THRU_TEXT_FLAG.inv()
            category_element_text.setTextColor(ContextCompat.getColor(context, R.color.primary_text_color))
            category_element_checkBox.isChecked = true
        }
    }
}