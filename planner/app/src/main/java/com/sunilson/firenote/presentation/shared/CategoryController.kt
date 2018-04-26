package com.sunilson.firenote.presentation.shared

import android.app.Application
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryController @Inject constructor(val application: Application) {

    val categories : List<Category>

    init {
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