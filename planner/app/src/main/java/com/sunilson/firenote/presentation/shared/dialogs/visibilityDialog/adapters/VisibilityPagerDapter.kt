package com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.CategoryFragment
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.ColorFragment

class VisibilityPagerDapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm) {

    private val NUM_ITEMS: Int = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CategoryFragment.newInstance()
            else -> ColorFragment.newInstance()
        }
    }

    override fun getCount(): Int = NUM_ITEMS

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.categories)
            1 -> context.getString(R.string.colors)
            else -> ""
        }
    }
}