package com.sunilson.firenote.presentation.shared.dialogs.interfaces

interface DialogWithResult<T> {
    var listener: DialogListener<T>?
}