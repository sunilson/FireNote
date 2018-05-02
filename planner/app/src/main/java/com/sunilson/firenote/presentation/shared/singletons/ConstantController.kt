package com.sunilson.firenote.presentation.shared.singletons

import android.app.Application
import android.support.v4.content.ContextCompat
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.NoteColor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConstantController @Inject constructor(val application: Application) {

    val categories : List<Category>
    val colors : List<NoteColor>

    init {
        colors = listOf(NoteColor("note_color_1", ContextCompat.getColor(application, R.color.note_color_1)),
        NoteColor("note_color_2", ContextCompat.getColor(application, R.color.note_color_2)),
        NoteColor("note_color_3", ContextCompat.getColor(application, R.color.note_color_3)),
        NoteColor("note_color_4", ContextCompat.getColor(application, R.color.note_color_4)),
        NoteColor("note_color_5", ContextCompat.getColor(application, R.color.note_color_5)),
        NoteColor("note_color_6", ContextCompat.getColor(application, R.color.note_color_6)),
        NoteColor("note_color_7", ContextCompat.getColor(application, R.color.note_color_7)),
        NoteColor("note_color_8", ContextCompat.getColor(application, R.color.note_color_8)),
        NoteColor("note_color_9", ContextCompat.getColor(application, R.color.note_color_9)))

        categories= listOf(
            Category(application.getString(R.string.category_events), "events"),
            Category(application.getString(R.string.category_events), "events"),
            Category(application.getString(R.string.category_events), "events"),
            Category(application.getString(R.string.category_events), "events"),
            Category(application.getString(R.string.category_events), "events"),
            Category(application.getString(R.string.category_events), "events"),
            Category(application.getString(R.string.category_events), "events"),
            Category(application.getString(R.string.category_events), "events")
        )

        categories.sortedWith(Comparator { o1, o2 ->
            when {
                o1.name.compareTo(o2.name, true) < 0 -> -1
                o1.name.compareTo(o2.name, true) > 0 -> 1
                else -> 0
            }
        })
    }
}