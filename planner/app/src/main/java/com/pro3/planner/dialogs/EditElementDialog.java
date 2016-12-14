package com.pro3.planner.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.BundleInterface;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.R;

/**
 * Created by linus_000 on 12.11.2016.
 */

public class EditElementDialog extends SuperDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String id = getArguments().getString("elementID");
        final String type = getArguments().getString("type");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);

        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        View content = null;
        int editTitleResource = 0;

        if (type.equals("checklist")) {
            content = inflater.inflate(R.layout.alertdialog_body_checklist_edit, null);
            editTitleResource = R.id.checklist_edit_element_title;
        } else if (type.equals("note")) {
            content = inflater.inflate(R.layout.alertdialog_body_note_edit, null);
            editTitleResource = R.id.note_edit_element_title;
        }

        final Activity activity = getActivity();
        final EditText editTitleText = (EditText) content.findViewById(editTitleResource);

        if (activity instanceof BundleInterface) {
            BundleInterface bundleInterface = (BundleInterface) activity;
            editTitleText.setText(bundleInterface.getElementAdapter().getElement(id).getTitle());
        } else if (activity instanceof ElementInterface) {
            ElementInterface elementInterface = (ElementInterface) activity;
            editTitleText.setText(elementInterface.getElementTitle());
        } else if (activity instanceof MainActivityInterface) {
            MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
            editTitleText.setText(mainActivityInterface.getElementAdapter().getElement(id).getTitle());
        }

        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = editTitleText.getText().toString();
                        if (activity instanceof BundleInterface) {
                            BundleInterface bundleInterface = (BundleInterface) getActivity();
                            if (type.equals("checklist")) {
                                bundleInterface.getChecklistElementsReference().child(id).child("title").setValue(title);
                            } else {
                                bundleInterface.getNoteElementsReference().child(id).child("title").setValue(title);
                            }
                        } else if (activity instanceof ElementInterface) {
                            ElementInterface elementInterface = (ElementInterface) getActivity();
                            elementInterface.getElementReference().child("title").setValue(title);
                        } else if (activity instanceof MainActivityInterface) {
                            MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
                            mainActivityInterface.getElementsReference().child(id).child("title").setValue(title);
                        }
                    }
                }
        );

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }

        );

        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public static EditElementDialog newInstance(String title, String type, String elementID) {
        EditElementDialog dialog = new EditElementDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("type", type);
        args.putString("elementID", elementID);
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
