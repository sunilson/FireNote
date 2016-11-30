package com.pro3.planner.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.pro3.planner.R;

/**
 * Created by linus_000 on 30.11.2016.
 */

public class SuperDialog extends DialogFragment {


    @Override
    public void onStart() {
        super.onStart();

        /*

        final View decorView = getDialog()
                .getWindow()
                .getDecorView();

        decorView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        decorView.setDrawingCacheEnabled(true);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator first = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.05f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.05f)
        );
        first.setDuration(200);

        first.setInterpolator(new LinearInterpolator());

        first.start();

        ObjectAnimator second = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 1.05f, 0.95f),
                PropertyValuesHolder.ofFloat("scaleY", 1.05f, 0.95f));
        second.setDuration(50);

        ObjectAnimator third = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.95f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.95f, 1.0f));
        third.setDuration(50);

        animatorSet.setInterpolator(new LinearInterpolator());

        animatorSet.play(first).before(second);
        animatorSet.play(second).before(third);
        //animatorSet.start();
        */

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
    }
}
