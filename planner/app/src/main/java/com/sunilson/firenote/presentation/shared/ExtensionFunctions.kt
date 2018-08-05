package com.sunilson.firenote.presentation.shared

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.*
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

fun Window.changeStatusBarColor(color: Int) {
    //Darken notification bar color and set it to status bar. Only works in Lollipop and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.6f

        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = Color.HSVToColor(hsv)
    }
}

fun DatabaseReference.storeElement(element: Element): Task<*> {
    return this.setValue(FirebaseElement(
            element.elementID,
            element.category.name,
            element.category.id,
            element.noteType,
            element.color,
            element.locked,
            element.timeStamp,
            element.title
    ))
}

fun Context.showToast(message: String? = "No message given!", duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun CoordinatorLayout.showSnackbar(message: String = "", button: Boolean = false, actionMessage: String = "", actionCallback: () -> Unit = {}) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    if (button) {
        snackbar.setAction(actionMessage) {
            actionCallback()
        }
    }
    snackbar.show()
}

fun DataSnapshot.parseFirebaseElement(): FirebaseElement {
    return FirebaseElement(
            this.key,
            this.child("categoryName").value as String,
            this.child("categoryID").value as String,
            this.child("noteType").value as String,
            (this.child("color").value as Long).toInt(),
            this.child("locked").value as Boolean,
            this.child("timeStamp").getValue(Long::class.java)!!,
            this.child("title").value as String
    )
}

fun Context.sortingMethods(): List<SortingMethod> {
    return listOf(
            SortingMethod(this.resources.getString(R.string.sort_descending_date), R.drawable.ic_date_range_black_24dp, ElementComparators.sortByDate(true)),
            SortingMethod(this.resources.getString(R.string.sort_ascending_date), R.drawable.ic_date_range_black_24dp, ElementComparators.sortByDate(false)),
            SortingMethod(this.resources.getString(R.string.sort_descending_name), R.drawable.ic_text_fields_black_24dp, ElementComparators.sortByName(true)),
            SortingMethod(this.resources.getString(R.string.sort_ascending_name), R.drawable.ic_text_fields_black_24dp, ElementComparators.sortByName(false)),
            SortingMethod(this.resources.getString(R.string.sort_category_name), R.drawable.ic_label_black_24dp, ElementComparators.sortByCategory())
    )
}

fun Context.categories(): List<Category> {
    val categories = listOf(
            Category(this.getString(R.string.category_business), "business"),
            Category(this.getString(R.string.category_events), "events"),
            Category(this.getString(R.string.category_finances), "finances"),
            Category(this.getString(R.string.category_general), "general"),
            Category(this.getString(R.string.category_hobbies), "hobbies"),
            Category(this.getString(R.string.category_holidays), "holidays"),
            Category(this.getString(R.string.category_project), "project"),
            Category(this.getString(R.string.category_school), "school"),
            Category(this.getString(R.string.category_shopping), "shopping"),
            Category(this.getString(R.string.category_sport), "sport")
    )

    return categories.sortedWith(Comparator { o1, o2 ->
        when {
            o1.name.compareTo(o2.name, true) < 0 -> -1
            o1.name.compareTo(o2.name, true) > 0 -> 1
            else -> 0
        }
    })
}

fun String.encodePassword(): String = String(Hex.encodeHex(DigestUtils.sha1(this)))

fun Context.colors(): List<NoteColor> {
    return listOf(
            NoteColor("note_color_1", ContextCompat.getColor(this, R.color.note_color_1)),
            NoteColor("note_color_2", ContextCompat.getColor(this, R.color.note_color_2)),
            NoteColor("note_color_3", ContextCompat.getColor(this, R.color.note_color_3)),
            NoteColor("note_color_4", ContextCompat.getColor(this, R.color.note_color_4)),
            NoteColor("note_color_5", ContextCompat.getColor(this, R.color.note_color_5)),
            NoteColor("note_color_6", ContextCompat.getColor(this, R.color.note_color_6)),
            NoteColor("note_color_7", ContextCompat.getColor(this, R.color.note_color_7)),
            NoteColor("note_color_8", ContextCompat.getColor(this, R.color.note_color_8)),
            NoteColor("note_color_9", ContextCompat.getColor(this, R.color.note_color_9))
    )
}
