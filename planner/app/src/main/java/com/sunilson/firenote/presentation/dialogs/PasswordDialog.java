//package com.sunilson.firenote.presentation.dialogs;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AlertDialog;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.sunilson.firenote.Interfaces.ConfirmDialogResult;
//import com.sunilson.firenote.R;
//import com.sunilson.firenote.presentation.shared.base.BaseDialogFragment;
//
//import java.io.UnsupportedEncodingException;
//import java.security.NoSuchAlgorithmException;
//
///**
// * @author  Linus Weiss
// */
//
///**
// * Dialog used to ask for the MasterPassword
// */
//public class PasswordDialog extends BaseDialogFragment {
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        super.onCreateDialog(savedInstanceState);
//
//        //Title
//        titleText.setText(getString(R.string.enter_password));
//        builder.setCustomTitle(title);
//
//        //Content
//        View content = inflater.inflate(R.layout.alertdialog_body_password, null);
//        final EditText password = (EditText) content.findViewById(R.id.password);
//        builder.setView(content);
//
//        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                String pw = "";
//
//                //Get stored Master Password
//                try {
//                    pw = LocalSettingsManager.getInstance().getSHA1Hash(password.getText().toString());
//                } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                }
//
//                //Call interface method from confirmDialogResult with the result
//                if (pw.equals(LocalSettingsManager.getInstance().getMasterPassword())) {
//                    ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
//                    confirmDialogResult.confirmDialogResult(true, getArguments().getString("type"), getArguments());
//                    dialog.dismiss();
//                } else {
//                    ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
//                    confirmDialogResult.confirmDialogResult(false, getArguments().getString("type"), getArguments());
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        });
//
//        final AlertDialog dialog = builder.create();
//
//        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
//                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
//                }
//                return false;
//            }
//        });
//
//        return dialog;
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        super.onDismiss(dialog);
//
//
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (getDialog().getWindow() != null) {
//            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//    /**
//     * Create new Password Dialog
//     *
//     * @param type Who called, with what purpose
//     * @param elementType Type of element
//     * @param elementID ID of element
//     * @param elementTitle Title of element
//     * @param elementColor Color of element
//     * @return New DialogFragment
//     */
//    public static PasswordDialog newInstance(String type, String elementType, String elementID, String elementTitle, int elementColor) {
//        PasswordDialog dialog = new PasswordDialog();
//        Bundle args = new Bundle();
//        args.putString("type", type);
//        args.putString("elementType", elementType);
//        args.putString("elementID", elementID);
//        args.putString("elementTitle", elementTitle);
//        args.putInt("elementColor", elementColor);
//        dialog.setArguments(args);
//        return dialog;
//    }
//}
