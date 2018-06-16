package com.sunilson.firenote.presentation.shared.singletons

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.sunilson.firenote.presentation.shared.sortingMethods
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSettingsManager @Inject constructor(val context: Application) {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun setCategoryVisiblity(id: String, visibility: Int) {
        val editor = sharedPrefs.edit()
        editor.putInt(id, visibility)
        editor.commit()
    }

    fun setColorVisibility(color: Int, visibility: Int) {
        val editor = sharedPrefs.edit()
        editor.putInt(color.toString(), visibility)
        editor.commit()
    }

    fun setSortingMethod(sortingMethod: String) {
        val editor = sharedPrefs.edit()
        editor.putString("mainElementSorting", sortingMethod)
        editor.commit()
    }

    fun setMasterPassword(password: String) {
        val editor = sharedPrefs.edit()
        editor.putString("masterPassword", password)
        editor.commit()
    }

    fun getMasterPassword() = sharedPrefs.getString("masterPassword", "")
    fun getSortingMethod() = sharedPrefs.getString("mainElementSorting", context.sortingMethods()[0].name)
    fun getColorVisibility(color: Int) = sharedPrefs.getInt(color.toString(), 1)
    fun getCategoryVisibility(category: String) = sharedPrefs.getInt(category, 1)
}