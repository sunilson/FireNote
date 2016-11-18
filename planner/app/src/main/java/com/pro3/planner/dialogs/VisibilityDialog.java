package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.pro3.planner.R;
import com.pro3.planner.adapters.VisibilityPagerAdapter;

/**
 * Created by linus_000 on 17.11.2016.
 */

public class VisibilityDialog extends android.support.v4.app.DialogFragment {

    FragmentPagerAdapter adapterViewPager;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();


        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setAttributes(layoutParams);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        adapterViewPager = new VisibilityPagerAdapter(getChildFragmentManager(), getActivity());

        View content = inflater.inflate(R.layout.alertdialog_body_visibility, container);
        ViewPager vpPager = (ViewPager) content.findViewById(R.id.menu_visibility_pager);
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout) content.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        return content;
    }
}
