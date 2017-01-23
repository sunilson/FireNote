package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.R;

/**
 * Created by linus_000 on 29.11.2016.
 */

public class ConfirmDialog extends SuperDialog {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        View content = inflater.inflate(R.layout.alertdialog_body_confirm, null);
        ((TextView) content.findViewById(R.id.dialog_confirm_text)).setText(getArguments().getString("text"));
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
                confirmDialogResult.confirmDialogResult(true, getArguments().getString("type"), getArguments());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        return builder.create();
    }

    public static ConfirmDialog newInstance(String title, String text, String type, String extra) {
        ConfirmDialog dialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);
        args.putString("type", type);
        args.putString("extra", extra);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
