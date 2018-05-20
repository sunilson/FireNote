//package com.sunilson.firenote.presentation.dialogs;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AlertDialog;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.sunilson.firenote.Interfaces.SettingsInterface;
//import com.sunilson.firenote.R;
//import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment;
//
//import java.io.UnsupportedEncodingException;
//import java.security.NoSuchAlgorithmException;
//
///**
// * @author Linus Weiss
// */
//
///**
// * Dialog to enter new Master Password
// */
//public class MasterPasswordDialog extends BaseDialogFragment {
//
//    View content;
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        super.onCreateDialog(savedInstanceState);
//
//        //Title
//        titleText.setText(getString(R.string.set_master_password));
//        builder.setCustomTitle(title);
//
//        //Content
//        content = inflater.inflate(R.layout.alertdialog_body_master_password, null);
//        final EditText pwOld = ((EditText) content.findViewById(R.id.master_password_old));
//
//        //If no password has been set, no old password field should be displayed
//        if (LocalSettingsManager.getInstance().getMasterPassword().equals("")) {
//            pwOld.setVisibility(View.GONE);
//        }
//
//        builder.setView(content);
//        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, null);
//        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        });
//
//        return builder.create();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        final AlertDialog dialog = (AlertDialog) getDialog();
//
//        final SettingsInterface settings = (SettingsInterface) getActivity();
//        final EditText pwOld = ((EditText) content.findViewById(R.id.master_password_old));
//        final EditText pwNew = ((EditText) content.findViewById(R.id.master_password_new));
//        final EditText pwNew2 = ((EditText) content.findViewById(R.id.master_password_new2));
//
//        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Check for internet connection
//                if (settings.getConnected()) {
//                    String old = pwOld.getText().toString();
//                    String newS = pwNew.getText().toString().trim();
//                    String newS2 = pwNew2.getText().toString().trim();
//                    String oldHash = "";
//                    String newHash = "";
//
//                    try {
//                        oldHash = LocalSettingsManager.getInstance().getSHA1Hash(old);
//                        newHash = LocalSettingsManager.getInstance().getSHA1Hash(newS);
//                    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    }
//
//                    //Check for errors
//                    if (newS.equals(newS2) && !newS.equals("")) {
//                        if (!LocalSettingsManager.getInstance().getMasterPassword().equals("")) {
//                            if (oldHash.equals(LocalSettingsManager.getInstance().getMasterPassword())) {
//                                final Activity activity = getActivity();
//                                //Updated password in database
//                                settings.getSettingsReference().child("masterPassword").setValue(newHash).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(activity, R.string.master_password_changed, Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                                dialog.dismiss();
//                            } else {
//                                Toast.makeText(getActivity(), R.string.old_password_error, Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            settings.getSettingsReference().child("masterPassword").setValue(newHash);
//                            dialog.dismiss();
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), R.string.password_not_equal_empty, Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), R.string.need_connection, Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        //Open Keyboard on Dialg opening
//        if (getDialog().getWindow() != null) {
//            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        }
//    }
//
//    /**
//     * Get new Instance of MasterPasswordDialog
//     *
//     * @return New DialogFragment
//     */
//    public static MasterPasswordDialog newInstance() {
//        MasterPasswordDialog masterPasswordDialog = new MasterPasswordDialog();
//        Bundle args = new Bundle();
//        masterPasswordDialog.setArguments(args);
//        return masterPasswordDialog;
//    }
//}
