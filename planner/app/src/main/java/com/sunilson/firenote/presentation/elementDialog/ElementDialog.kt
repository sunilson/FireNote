package com.sunilson.firenote.presentation.elementDialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import com.sunilson.firenote.R
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.adapters.CategorySpinnerAdapter
import com.sunilson.firenote.presentation.adapters.ColorAddArrayAdapter
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.shared.colors
import com.sunilson.firenote.presentation.shared.singletons.ConnectivityManager
import com.sunilson.firenote.presentation.shared.views.ColorElementView
import kotlinx.android.synthetic.main.alertdialog_body_add_element.view.*
import kotlinx.android.synthetic.main.alertdialog_custom_title.view.*
import javax.inject.Inject

class ElementDialog : BaseDialogFragment(), ElementDialogPresenterContract.View {

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    @Inject
    lateinit var presenter: ElementDialogPresenterContract.Presenter

    private lateinit var element: Element
    private lateinit var v: View
    private lateinit var colorAdapter: ColorAddArrayAdapter
    private lateinit var categorySpinnerAdapter: CategorySpinnerAdapter
    private var editMode: Boolean = false
    private var elementType: String = "note"
    private lateinit var imm: InputMethodManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        elementType = arguments?.getString("elementType")!!
        editMode = arguments?.getBoolean("editMode") == true
        element = Element("", Category("", ""), elementType)
        colorAdapter = ColorAddArrayAdapter(context!!)
        categorySpinnerAdapter = CategorySpinnerAdapter(context!!)
        context!!.colors().forEach { colorAdapter.add(it) }
        imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        v = LayoutInflater.from(context).inflate(R.layout.alertdialog_body_add_element, null, false)

        titleView.dialog_title.text = arguments?.getString("title")
        if(editMode) {
            titleView.dialog_title_action.visibility = View.VISIBLE
            titleView.dialog_title_action.setOnClickListener {
                //TODO
            }
        }

        //Initialize Color List
        v.colorlist.adapter = colorAdapter
        v.colorlist.setOnItemClickListener { _, view, position, _ ->
            val colorView = view as ColorElementView
            if (!colorView.isChecked) {
                element.color = colorAdapter.getItem(position).color
                colorAdapter.uncheckAll()
                colorAdapter.setCheckedPosition(position)
                colorView.isChecked = true
            }
        }

        //Initialize Category List
        v.add_element_categorySpinner.adapter = categorySpinnerAdapter
        v.add_element_categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                element.category = (parent?.adapter as CategorySpinnerAdapter).data[position]
            }
        }
        v.add_element_categorySpinner.prompt = resources.getString(R.string.spinner_prompt)
        v.add_element_categorySpinner.setOnTouchListener { _, _ ->
            v.add_element_title.clearFocus()
            v.add_element_categorySpinner.requestFocus()
            false
        }

        v.add_element_title.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) imm.hideSoftInputFromWindow(v.windowToken, 0)
        }


        //TODO if (savedInstanceState != null) elementDialogView.selectedColor = savedInstanceState.getInt("color")
        builder.setCustomTitle(titleView)
        builder.setView(v)
        builder.setPositiveButton(getString(R.string.confirm_add_dialog), { _, _ -> })
        builder.setNegativeButton(getString(R.string.cancel_add_dialog), { _, _ -> dismiss() })
        selectColor(0)

        if (!connectivityManager.isConnected()) Toast.makeText(activity, R.string.edit_no_connection, Toast.LENGTH_LONG).show()

        val dialog = builder.create()
        setDialogLayoutParams(dialog)
        dialog.window.attributes.windowAnimations = R.style.dialogAnimation
        return dialog
    }

    override fun onStart() {
        super.onStart()
        (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
            element.title = v.add_element_title.text.toString()
            presenter.addElement(element)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) dialog.setDismissMessage(null)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //TODO outState.putInt("color", elementDialogView.selectedColor)
    }

    override fun showSuccess(message: String?) {
        super<ElementDialogPresenterContract.View>.showSuccess(message)
        dismiss()
    }

    private fun setDialogLayoutParams(dialog: Dialog) {
        val lp = dialog.window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = lp
    }

    private fun selectColor(position: Int) {
        colorAdapter.setCheckedPosition(position)
        (colorAdapter.getView(position, null, null) as ColorElementView).isChecked = true
        element.color = colorAdapter.getItem(position).color
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    companion object {
        fun newInstance(title: String, elementType: String): ElementDialog {
            val dialog = ElementDialog()
            val args = Bundle()
            args.putString("title", title)
            args.putString("elementType", elementType)
            dialog.arguments = args
            return dialog
        }
    }
}