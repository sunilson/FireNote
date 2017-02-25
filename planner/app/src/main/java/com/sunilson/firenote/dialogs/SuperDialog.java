package com.sunilson.firenote.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sunilson.firenote.R;
import com.sunilson.firenote.activities.BaseElementActivity;

/**
 * @author Linus Weiss
 *
 */

/**
 * Base class of all dialogs
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

        //Basic functionality of AlertDialog. Same for all dialogs
        builder = new AlertDialog.Builder(getActivity());
        activity = getActivity();
        inflater = getActivity().getLayoutInflater();
        title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        titleText = (TextView) title.findViewById(R.id.dialog_title);

        //Check if we are in an Element. If yes, set color of dialog to color of Element
        if (activity instanceof BaseElementActivity) {
            (title.findViewById(R.id.dialog_title_container)).setBackgroundColor(((BaseElementActivity)activity).getElementColor());
        }

        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Apply open and close animation to Dialog
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        }
    }
}
