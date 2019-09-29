package com.sunilson.firenote.presentation.shared

import android.content.res.ColorStateList
import androidx.databinding.BindingAdapter
import android.graphics.Paint
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sunilson.firenote.R
import java.util.*

@BindingAdapter("android:formatDate")
fun formatDate(view: TextView, timestamp: Long?) {
    if (timestamp != null) {
        val dateFormat = android.text.format.DateFormat.getDateFormat(view.context)
        view.text = dateFormat.format(Date(timestamp))
    }
}

@BindingAdapter("android:checklistTextStyle")
fun checklistTextStyle(view: TextView, finished: Boolean) {
    if (finished) {
        view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        view.setTextColor(ContextCompat.getColor(view.context, R.color.text_crossed_out))
    } else {
        view.paintFlags = view.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        view.setTextColor(ContextCompat.getColor(view.context, R.color.primary_text_color))
    }
}

@BindingAdapter("android:fabBackgroundColor")
fun fabBackgroundColor(view: FloatingActionButton, color: Int?) {
    if (color != null) view.backgroundTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("android:multiFabBackgroundColor")
fun multiFabBackgroundColor(view: com.getbase.floatingactionbutton.FloatingActionsMenu, color: Int?) {
    if(color != null) view.setBackgroundColor(color)
}

@BindingAdapter("android:noteTypeIcon")
fun noteTypeIcon(view: ImageView, noteType: String?) {
    when (noteType) {
        "note" -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_note_white_24dp))
        "checklist" -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_done_all_white_24dp))
        else -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_list_white_24dp))
    }
}

@BindingAdapter("android:fabIconFromType")
fun fabIconFromType(view: FloatingActionButton, noteType: String?) {
    when (noteType) {
        "note" -> view.setImageResource(R.drawable.ic_mode_edit_black_24dp)
        "checklist" -> view.setImageResource(R.drawable.ic_add_white_24dp)
        "bundle" -> view.setImageResource(R.drawable.ic_add_white_24dp)
    }
}

@BindingAdapter("android:transparentColor")
fun transparentColor(view: View, color: Int?) {
    if (color != null) view.setBackgroundColor(ColorUtils.setAlphaComponent(color, 80))
}