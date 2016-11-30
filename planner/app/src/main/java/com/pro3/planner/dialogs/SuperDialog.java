package com.pro3.planner.dialogs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * Created by linus_000 on 30.11.2016.
 */

public class SuperDialog extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();

        final View decorView = getDialog()
                .getWindow()
                .getDecorView();

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator first = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.1f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
        first.setDuration(400);


        ObjectAnimator second = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f, 0.9f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f, 0.9f));
        second.setDuration(100);

        ObjectAnimator third = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1.0f));
        third.setDuration(100);

        animatorSet.play(first).before(second);
        animatorSet.play(second).before(third);
        animatorSet.start();
    }
}
