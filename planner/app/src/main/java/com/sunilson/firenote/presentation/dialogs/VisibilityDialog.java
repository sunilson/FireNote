package com.sunilson.firenote.presentation.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sunilson.firenote.R;
import com.sunilson.firenote.adapters.VisibilityPagerAdapter;
import com.sunilson.firenote.presentation.shared.SuperDialog;

/**
 * @author  Linus Weiss
 */

/**
 * Dialog for hiding and showing elements in the main list
 */
public class VisibilityDialog extends SuperDialog {

    FragmentPagerAdapter adapterViewPager;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
        }

        //Dialog height and width
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (getDialog().getWindow() != null) {
            layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        }
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(layoutParams);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View Pager containing the Category and Color Fragments
        adapterViewPager = new VisibilityPagerAdapter(getChildFragmentManager(), getActivity());
        View content = inflater.inflate(R.layout.alertdialog_body_visibility, container);
        ViewPager vpPager = content.findViewById(R.id.menu_visibility_pager);
        vpPager.setAdapter(adapterViewPager);
        TabLayout tabLayout =  content.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
        return content;
    }
}
