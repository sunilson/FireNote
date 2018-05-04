package com.sunilson.firenote.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sunilson.firenote.Interfaces.BundleInterface
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.shared.singletons.ConnectivityManager
import com.sunilson.firenote.presentation.views.AddElementView
import javax.inject.Inject

class AddElementDialog : BaseDialogFragment() {

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    private lateinit var addElementView: AddElementView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        addElementView = AddElementView(activity)
        val elementType = arguments?.getString("elementType")
        titleView.dialog_title.text = arguments?.getString("title")
        if(savedInstanceState != null) addElementView.color = savedInstanceState.getInt("color")
        builder.setCustomTitle(titleView)
        builder.setView(addElementView)

        builder.setPositiveButton(getString(R.string.confirm_add_dialog),  { dialog, which ->
            val element = when(elementType) {
                getString(R.string.element_note) -> Element("note", addElementView.title, addElementView.color, addElementView.category)
                getString(R.string.element_checklist) -> Element("checklist", addElementView.title, addElementView.color, addElementView.category)
                else -> Element("bundle", addElementView.title, addElementView.color, addElementView.category)
            }

            if(activity is MainActivity) FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser?.uid).child("elements").child("main").push().setValue(element)
            else (activity as BundleInterface).elementsReference.push().setValue(element)
        })

        builder.setNegativeButton(getString(R.string.cancel_add_dialog), { _, _ ->  dismiss()})

        if(!connectivityManager.isConnected()) Toast.makeText(activity, R.string.edit_no_connection, Toast.LENGTH_LONG).show()

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
        outState.putInt("color", addElementView.color)
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