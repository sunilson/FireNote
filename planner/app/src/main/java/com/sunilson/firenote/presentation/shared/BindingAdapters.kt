package com.sunilson.firenote.presentation.shared

import android.content.res.ColorStateList
import android.databinding.BindingAdapter
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sunilson.firenote.R
import java.util.*

@BindingAdapter("android:formatDate")
fun formatDate(view: TextView, timestamp: Long?) {
    if(timestamp != null) {
        val dateFormat = android.text.format.DateFormat.getDateFormat(view.context)
        view.text = dateFormat.format(Date(timestamp))
    }
}

@BindingAdapter("android:fabBackgroundColor")
fun fabBackgroundColor(view: FloatingActionButton, color: Int?) {
    if(color != null) view.backgroundTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("android:noteTypeIcon")
fun noteTypeIcon(view: ImageView, noteType: String?) {
    when(noteType) {
        "note" -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_note_white_24dp))
        "checklist" -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_done_all_white_24dp))
        else -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_list_white_24dp))
    }
}

@BindingAdapter("android:fabIconFromType")
fun fabIconFromType(view: FloatingActionButton, noteType: String?) {
    when(noteType) {
        "note" -> view.setImageResource(R.drawable.ic_mode_edit_black_24dp)
        "checklist" -> view.setImageResource(R.drawable.ic_add_white_24dp)
        "bundle" -> view.setImageResource(R.drawable.ic_add_white_24dp)
    }
}

@BindingAdapter("android:transparentColor")
fun transparentColor(view: View, color: Int?) {
   if(color != null) view.setBackgroundColor(ColorUtils.setAlphaComponent(color, 80))
}