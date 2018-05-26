package com.sunilson.firenote.presentation.visibilityDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment
import com.sunilson.firenote.presentation.visibilityDialog.adapters.VisibilityPagerDapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
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
        setDimensions(dialog)
    }

    private fun setDimensions(dialog: Dialog) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window.attributes)
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window.attributes = layoutParams
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

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    companion object {
        fun newInstance(): VisibilityDialog {
            return VisibilityDialog()
        }
    }
}