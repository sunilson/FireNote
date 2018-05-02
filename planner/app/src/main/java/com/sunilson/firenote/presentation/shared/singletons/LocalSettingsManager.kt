package com.sunilson.firenote.presentation.shared.singletons

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalSettingsManager @Inject constructor() {

    @Inject
    lateinit var application: Application

    private val sharedPrefs: SharedPreferences

    fun setCategoryVisibility(color: Int, visibility: Int) {
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

    fun getMasterPassword() = sharedPrefs.getString("masterPassword", "");
    fun getSortingMethod() = sharedPrefs.getString("mainElementSorting", null)
    fun getCategoryVisibility(category: String) = sharedPrefs.getInt(category, -1)
    fun getSHA1Hash(masterPasswordHash: String): String = String(Hex.encodeHex(DigestUtils.sha1(masterPasswordHash)))

    init {
        sharedPrefs = application.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }
}