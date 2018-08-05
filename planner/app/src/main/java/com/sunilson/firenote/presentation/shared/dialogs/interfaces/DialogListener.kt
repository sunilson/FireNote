package com.sunilson.firenote.presentation.shared.dialogs.interfaces

interface DialogListener <in T> {
    fun onResult(result : T?)
}