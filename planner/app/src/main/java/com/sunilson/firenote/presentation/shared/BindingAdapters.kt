package com.sunilson.firenote.presentation.shared

import android.databinding.BindingAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sunilson.firenote.R
import java.util.*

@BindingAdapter("android:formatDate")
fun formatDate(view: TextView, date: Date) {
    view.text = "20.12.1993"
}

@BindingAdapter("android:noteTypeIcon")
fun noteTypeIcon(view: ImageView, noteType: String) {
    when(noteType) {
        "note" -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_note_white_24dp))
        else -> view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_list_white_24dp))
    }
}

@BindingAdapter("android:transparentColor")
fun transparentColor(view: View, color: Int) = view.setBackgroundColor(ColorUtils.setAlphaComponent(color, 80))