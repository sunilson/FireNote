package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pro3.planner.Interfaces.SettingsInterface;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by linus_000 on 01.12.2016.
 */

public class MasterPasswordDialog extends SuperDialog {

    View content;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SettingsInterface settings = (SettingsInterface) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        titleText.setText(getString(R.string.set_master_password));
        builder.setCustomTitle(title);

        content = inflater.inflate(R.layout.alertdialog_body_master_password, null);
        final EditText pwOld = ((EditText) content.findViewById(R.id.master_password_old));

        if (LocalSettingsManager.getInstance().getMasterPassword().equals("")) {
            pwOld.setVisibility(View.GONE);
        }

        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, null);

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        final AlertDialog dialog = builder.create();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();

        final SettingsInterface settings = (SettingsInterface) getActivity();
        final EditText pwOld = ((EditText) content.findViewById(R.id.master_password_old));
        final EditText pwNew = ((EditText) content.findViewById(R.id.master_password_new));
        final EditText pwNew2 = ((EditText) content.findViewById(R.id.master_password_new2));

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settings.getConnected()) {
                    String old = pwOld.getText().toString();
                    String newS = pwNew.getText().toString().trim();
                    String newS2 = pwNew2.getText().toString().trim();
                    String oldHash = "";
                    String newHash = "";

                    try {
                        oldHash = LocalSettingsManager.getInstance().getMD5Hash(old);
                        newHash = LocalSettingsManager.getInstance().getMD5Hash(newS);
                    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    if (newS.equals(newS2) && !newS.equals("")) {
                        if (LocalSettingsManager.getInstance().getMasterPassword() != "") {
                            if (oldHash.equals(LocalSettingsManager.getInstance().getMasterPassword())) {
                                settings.getSettingsReference().child("masterPassword").setValue(newHash);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Old password not correct", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            settings.getSettingsReference().child("masterPassword").setValue(newHash);
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Passwords not equal or empty", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.need_connection, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public static MasterPasswordDialog newInstance() {
        MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog();
        Bundle args = new Bundle();
        masterPasswordDialog.setArguments(args);
        return masterPasswordDialog;
    }
}
