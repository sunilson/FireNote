package com.sunilson.firenote.presentation.shared.interfaces

import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapter
import com.sunilson.firenote.presentation.shared.base.IBaseView
import com.sunilson.firenote.presentation.shared.dialogs.MasterPasswordDialog
import com.sunilson.firenote.presentation.shared.dialogs.interfaces.DialogListener

interface HasElementList {
    val adapter: ElementRecyclerAdapter
    fun elementAdded(element: Element) = adapter.add(element)
    fun elementChanged(element: Element) = adapter.update(element)
    fun elementRemoved(element: Element) = adapter.remove(element)

    fun openElement(element: Element, activity: FragmentActivity) {
        if (element.locked) {
            val dialog = MasterPasswordDialog.newInstance()
            dialog.listener = object : DialogListener<Boolean> {
                override fun onResult(result: Boolean?) {
                    if (result != null && result) {
                        val intent = Intent(activity, ElementActivity::class.java)
                        intent.putExtra("elementID", element.elementID)
                        intent.putExtra("noteType", element.noteType)
                        intent.putExtra("elementColor", element.color)
                        if(element.parent != null) intent.putExtra("parentID", element.parent)
                        activity.startActivity(intent)
                    } else {
                        if(activity is IBaseView) activity.showError(activity.getString(R.string.wrong_password))
                    }
                }
            }
            dialog.show(activity.supportFragmentManager, "dialog")
        } else {
            val intent = Intent(activity, ElementActivity::class.java)
            intent.putExtra("elementID", element.elementID)
            intent.putExtra("noteType", element.noteType)
            intent.putExtra("elementColor", element.color)
            if(element.parent != null) intent.putExtra("parentID", element.parent)
            activity.startActivity(intent)
        }
    }
}