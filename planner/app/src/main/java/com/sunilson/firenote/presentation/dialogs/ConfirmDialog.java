package com.sunilson.firenote.presentation.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.sunilson.firenote.Interfaces.ConfirmDialogResult;
import com.sunilson.firenote.R;
import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment;

/**
 * @author Linus Weiss
 */

/**
 * Simple dialog with "Confirm" or "Cancel". Calls interface method of activity when done
 */
public class ConfirmDialog extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Set dialog title
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        //Set dialog content
        View content = inflater.inflate(R.layout.alertdialog_body_confirm, null);
        ((TextView) content.findViewById(R.id.dialog_confirm_text)).setText(getArguments().getString("text"));
        builder.setView(content);

        //Set "Confirm" button
        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Call interface method
                ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
                confirmDialogResult.confirmDialogResult(true, getArguments().getString("type"), getArguments());
                dialog.dismiss();
            }
        });

        //Set "cancel" button
        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        return builder.create();
    }

    /**
     * Create new ConfirmDialog instance
     *
     * @param title Title of dialog
     * @param text Text of dialog
     * @param type Type of dialog
     * @param extra Custom String, returned to interface
     * @return New ConfirmDialog
     */
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
}
