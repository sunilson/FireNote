package com.sunilson.firenote.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.sunilson.firenote.R;

/**
 * Created by linus_000 on 27.12.2016.
 */

public class AuthenticateDialog extends SuperDialog {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        titleText.setText(getString(R.string.enter_password));
        builder.setCustomTitle(title);

        View content = inflater.inflate(R.layout.alertdialog_body_authenticate, null);
        final EditText password = (EditText) content.findViewById(R.id.password);
        final EditText email = (EditText) content.findViewById(R.id.loginEmail);
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pw = password.getText().toString();
                String em = email.getText().toString();

                if (!pw.equals("") && !em.equals("")) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(em, pw);
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


    public static PasswordDialog newInstance(String type) {
        PasswordDialog dialog = new PasswordDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        dialog.setArguments(args);
        return dialog;
    }
}

