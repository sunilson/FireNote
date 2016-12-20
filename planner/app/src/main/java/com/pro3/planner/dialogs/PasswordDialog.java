package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by linus_000 on 07.12.2016.
 */

public class PasswordDialog extends SuperDialog {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);

        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        titleText.setText(getString(R.string.enter_password));
        builder.setCustomTitle(title);

        View content = inflater.inflate(R.layout.alertdialog_body_password, null);
        final EditText password = (EditText) content.findViewById(R.id.password);
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pw = "";

                try {
                    pw = LocalSettingsManager.getInstance().getMD5Hash(password.getText().toString());
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                if (pw.equals(LocalSettingsManager.getInstance().getMasterPassword())) {
                    ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
                    confirmDialogResult.confirmDialogResult(true, getArguments().getString("type"), getArguments());
                    dialog.dismiss();
                } else {
                    ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
                    confirmDialogResult.confirmDialogResult(false, getArguments().getString("type"), null);
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        final AlertDialog dialog = builder.create();

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                }
                return false;
            }
        });

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_negative_button));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_positive_button));
    }

    public static PasswordDialog newInstance(String type, String elementType, String elementID, String elementTitle, int elementColor) {
        PasswordDialog dialog = new PasswordDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("elementType", elementType);
        args.putString("elementID", elementID);
        args.putString("elementTitle", elementTitle);
        args.putInt("elementColor", elementColor);
        dialog.setArguments(args);
        return dialog;
    }
}
