package com.sunilson.firenote.presentation.shared.base

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.View
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*

abstract class BaseDialogFragment : DialogFragment(), IBaseView {

    override val mContext: Context?
        get() = context
    protected lateinit var titleView: View
    protected lateinit var builder: AlertDialog.Builder
    protected val disposable = CompositeDisposable()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        builder = AlertDialog.Builder(activity)
        titleView = activity!!.layoutInflater.inflate(R.layout.alertdialog_custom_title, null)
        if (activity is ElementActivity) titleView.dialog_title_container.setBackgroundColor((activity as ElementActivity).element!!.color)
        builder.setCustomTitle(titleView)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
    }


    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}