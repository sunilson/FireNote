package com.sunilson.firenote.presentation.shared.views

import android.content.Context
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.sunilson.firenote.R
import kotlinx.android.synthetic.main.checklist_list_layout.view.*

class ChecklistView(context: Context) : LinearLayout(context) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    val view: View

    init {
        view = inflater.inflate(R.layout.checklist_list_layout, this, false)
    }

    var checked: Boolean = false
        set(value) {
            field = value
            if(value) {
                checkList_element_text.paintFlags = checkList_element_text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                checkList_element_text.setTextColor(ContextCompat.getColor(context, R.color.text_crossed_out))
                checkList_element_checkBox.isChecked = value
            } else {
                checkList_element_text.paintFlags = checkList_element_text.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                checkList_element_text.setTextColor(ContextCompat.getColor(context, R.color.primary_text_color))
                checkList_element_checkBox.isChecked = value
            }
        }
}