package com.pro3.planner.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pro3.planner.R;
import com.pro3.planner.activities.BaseElementActivity;

/**
 * Created by linus_000 on 30.11.2016.
 */

public class SuperDialog extends DialogFragment {

    protected TextView titleText;
    protected LayoutInflater inflater;
    protected AlertDialog.Builder builder;
    protected Activity activity;
    protected View title;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        builder = new AlertDialog.Builder(getActivity());
        activity = getActivity();
        inflater = getActivity().getLayoutInflater();
        title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        titleText = (TextView) title.findViewById(R.id.dialog_title);

        if (activity instanceof BaseElementActivity) {
            (title.findViewById(R.id.dialog_title_container)).setBackgroundColor(((BaseElementActivity)activity).getElementColor());
        }

        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
