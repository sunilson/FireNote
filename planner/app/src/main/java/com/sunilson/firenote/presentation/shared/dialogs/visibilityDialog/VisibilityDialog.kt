package com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.*
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.adapters.VisibilityPagerDapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class VisibilityDialog : BaseDialogFragment(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setDimensions(dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onResume() {
        super.onResume()
        setDimensions(requireDialog())
    }

    private fun setDimensions(dialog: Dialog) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val adapterViewPager = VisibilityPagerDapter(childFragmentManager, activity as Context)
        val content = inflater.inflate(R.layout.alertdialog_body_visibility, container)
        val vpPager = content.findViewById<ViewPager>(R.id.menu_visibility_pager)
        vpPager.adapter = adapterViewPager
        val tabLayout = content.findViewById<TabLayout>(R.id.sliding_tabs)
        tabLayout.setupWithViewPager(vpPager)
        return content
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun toggleLoading(loading: Boolean, message: String?) {}

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    companion object {
        fun newInstance(): VisibilityDialog {
            return VisibilityDialog()
        }
    }
}