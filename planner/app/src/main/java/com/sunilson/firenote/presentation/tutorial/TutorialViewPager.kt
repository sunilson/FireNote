package com.sunilson.firenote.presentation.tutorial

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TutorialViewPager(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> TutorialFragment.newInstance()
            1 -> TutorialFragment.newInstance()
            2 -> TutorialFragment.newInstance()
            3 -> TutorialFragment.newInstance()
            4 -> TutorialFragment.newInstance()
            else -> TutorialFragment.newInstance()
        }
    }

    override fun getCount(): Int = 5

}