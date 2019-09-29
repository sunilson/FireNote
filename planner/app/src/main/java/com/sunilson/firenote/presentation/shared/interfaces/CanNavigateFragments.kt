package com.sunilson.firenote.presentation.shared.interfaces

import androidx.fragment.app.Fragment

interface CanNavigateFragments {
    fun navigateTo(fragment: Fragment, replace: Boolean = true, addToBackStack : Boolean = true)
    fun pop()
}