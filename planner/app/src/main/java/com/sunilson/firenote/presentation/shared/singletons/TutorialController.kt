package com.sunilson.firenote.presentation.shared.singletons

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.homepage.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TutorialController @Inject constructor() {
    fun showMainActivityTutorial(activity: MainActivity) {
        val showCaseView = ShowcaseView.Builder(activity)
                .withMaterialShowcase()
                .setTarget(ViewTarget(R.id.fab, activity))
                .setContentTitle(activity.getString(R.string.welcome_to_firenote))
                .setContentText(activity.getString(R.string.tutorial_add_elements))
                .hideOnTouchOutside()
                .setStyle(R.style.ShowCaseViewStyle)
                .singleShot(1)
                .setShowcaseEventListener(object : OnShowcaseEventListener {
                    override fun onShowcaseViewHide(showcaseView: ShowcaseView) {
                        activity.fab.isEnabled = true
                        activity.findViewById<View>(R.id.main_element_sort).isEnabled = false
                        ShowcaseView.Builder(activity)
                                .withMaterialShowcase()
                                .setTarget(ViewTarget(R.id.main_element_sort, activity))
                                .setContentTitle(activity.getString(R.string.tutorial_title_sort))
                                .setContentText(activity.getString(R.string.tutorial_sort))
                                .hideOnTouchOutside()
                                .setStyle(R.style.ShowCaseViewStyle)
                                .setShowcaseEventListener(object : OnShowcaseEventListener {
                                    override fun onShowcaseViewHide(showcaseView: ShowcaseView) {
                                        activity.findViewById<View>(R.id.main_element_sort).isEnabled = false
                                        if (activity.findViewById<View>(R.id.main_element_visibility) != null) {
                                            activity.findViewById<View>(R.id.main_element_visibility).isEnabled = false
                                            ShowcaseView.Builder(activity)
                                                    .withMaterialShowcase()
                                                    .setTarget(ViewTarget(R.id.main_element_visibility, activity))
                                                    .setContentTitle(activity.getString(R.string.tutorial_title_hide))
                                                    .setContentText(activity.getString(R.string.tutorial_hide))
                                                    .hideOnTouchOutside()
                                                    .setStyle(R.style.ShowCaseViewStyle)
                                                    .setShowcaseEventListener(object : OnShowcaseEventListener {
                                                        override fun onShowcaseViewHide(showcaseView: ShowcaseView) {
                                                            activity.findViewById<View>(R.id.main_element_visibility).isEnabled = false
                                                        }
                                                        override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) {}
                                                        override fun onShowcaseViewShow(showcaseView: ShowcaseView) {}
                                                        override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent) {}
                                                    })
                                                    .build()
                                        }
                                    }
                                    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) {}
                                    override fun onShowcaseViewShow(showcaseView: ShowcaseView) {}
                                    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent) {}
                                })
                                .build()
                    }
                    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) {}
                    override fun onShowcaseViewShow(showcaseView: ShowcaseView) {
                        activity.fab.isEnabled = false
                    }
                    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent) {}
                })

        val lps = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        val margin = (activity.resources.displayMetrics.density * 16).toInt()
        lps.setMargins(margin, margin, margin, margin)
        showCaseView.build().setButtonPosition(lps)
    }
}