package com.sunilson.firenote.presentation.shared.interfaces

import android.support.v4.app.Fragment

interface CanNavigateFragments {
    fun navigateTo(fragment: Fragment, replace: Boolean = true, addToBackStack : Boolean = true)
}