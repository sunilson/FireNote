package com.sunilson.firenote.presentation.elementDialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.shared.singletons.ConnectivityManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*
import javax.inject.Inject

class AddElementDialog : BaseDialogFragment() {

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    lateinit var elementDialogView: ElementDialogView

    private lateinit var element: Element

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val elementType = arguments?.getString("elementType")
        if (elementType != null) element = Element("", Category("", ""), elementType)

        titleView.dialog_title.text = arguments?.getString("title")
        titleView.dialog_title_action.visibility = View.VISIBLE
        titleView.dialog_title_action.setOnClickListener {
            //TODO
        }

        elementDialogView = ElementDialogView(context!!)
        //if (savedInstanceState != null) elementDialogView.selectedColor = savedInstanceState.getInt("color")

        builder.setCustomTitle(titleView)
        builder.setView(elementDialogView)
        builder.setPositiveButton(getString(R.string.confirm_add_dialog), { _, _ -> (activity as AddElementListener).addElement(element) })
        builder.setNegativeButton(getString(R.string.cancel_add_dialog), { _, _ -> dismiss() })

        if (!connectivityManager.isConnected()) Toast.makeText(activity, R.string.edit_no_connection, Toast.LENGTH_LONG).show()

        val dialog = builder.create()
        setDialogLayoutParams(dialog)
        dialog.window.attributes.windowAnimations = R.style.dialogAnimation
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // outState.putInt("color", elementDialogView.selectedColor)
    }

    private fun setDialogLayoutParams(dialog: Dialog) {
        val lp = dialog.window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window!!.attributes = lp
    }

    companion object {
        fun newInstance(title: String, elementType: String): AddElementDialog {
            val dialog = AddElementDialog()
            val args = Bundle()
            args.putString("title", title)
            args.putString("elementType", elementType)
            dialog.arguments = args
            return dialog
        }
    }
}