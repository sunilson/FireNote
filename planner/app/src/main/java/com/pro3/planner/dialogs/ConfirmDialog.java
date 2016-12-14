package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.R;

/**
 * Created by linus_000 on 29.11.2016.
 */

public class ConfirmDialog extends SuperDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);

        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        View content = inflater.inflate(R.layout.alertdialog_body_confirm, null);
        ((TextView) content.findViewById(R.id.dialog_confirm_text)).setText(getArguments().getString("text"));
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
                confirmDialogResult.confirmDialogResult(true, getArguments().getString("type"), null);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        return builder.create();
    }

    public static ConfirmDialog newInstance(String title, String text, String type) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);
        args.putString("type", type);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_negative_button));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_positive_button));
    }
}
