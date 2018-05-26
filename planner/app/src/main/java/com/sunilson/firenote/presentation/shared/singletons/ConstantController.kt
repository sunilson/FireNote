/*
package com.sunilson.firenote.presentation.shared.singletons

import android.content.Context
import android.support.v4.content.ContextCompat
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.ElementComparators
import com.sunilson.firenote.data.models.NoteColor
import com.sunilson.firenote.data.models.SortingMethod
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConstantController @Inject constructor(val context: Context) {

    var categories: List<Category> = listOf(
            Category(context.getString(R.string.category_events), "events"),
            Category(context.getString(R.string.category_events), "events"),
            Category(context.getString(R.string.category_events), "events"),
            Category(context.getString(R.string.category_events), "events"),
            Category(context.getString(R.string.category_events), "events"),
            Category(context.getString(R.string.category_events), "events"),
            Category(context.getString(R.string.category_events), "events"),
            Category(context.getString(R.string.category_events), "events")
    )
    val colors: List<NoteColor> = listOf(NoteColor("note_color_1", ContextCompat.getColor(context, R.color.note_color_1)),
            NoteColor("note_color_2", ContextCompat.getColor(context, R.color.note_color_2)),
            NoteColor("note_color_3", ContextCompat.getColor(context, R.color.note_color_3)),
            NoteColor("note_color_4", ContextCompat.getColor(context, R.color.note_color_4)),
            NoteColor("note_color_5", ContextCompat.getColor(context, R.color.note_color_5)),
            NoteColor("note_color_6", ContextCompat.getColor(context, R.color.note_color_6)),
            NoteColor("note_color_7", ContextCompat.getColor(context, R.color.note_color_7)),
            NoteColor("note_color_8", ContextCompat.getColor(context, R.color.note_color_8)),
            NoteColor("note_color_9", ContextCompat.getColor(context, R.color.note_color_9)))

    val sortingMethods: List<SortingMethod> = listOf(
            SortingMethod(context.resources.getString(R.string.sort_descending_date), R.drawable.ic_date_range_black_24dp, ElementComparators.sortByDate(true)),
            SortingMethod(context.resources.getString(R.string.sort_ascending_date), R.drawable.ic_date_range_black_24dp, ElementComparators.sortByDate(false)),
            SortingMethod(context.resources.getString(R.string.sort_descending_name), R.drawable.ic_text_fields_black_24dp, ElementComparators.sortByName(true)),
            SortingMethod(context.resources.getString(R.string.sort_ascending_name), R.drawable.ic_text_fields_black_24dp, ElementComparators.sortByName(false)),
            SortingMethod(context.resources.getString(R.string.sort_category_name), R.drawable.ic_label_black_24dp, ElementComparators.sortByCategory())
    )

    init {
        categories = categories.sortedWith(Comparator { o1, o2 ->
            when {
                o1.name.compareTo(o2.name, true) < 0 -> -1
                o1.name.compareTo(o2.name, true) > 0 -> 1
                else -> 0
            }
        })
    }
}*/
