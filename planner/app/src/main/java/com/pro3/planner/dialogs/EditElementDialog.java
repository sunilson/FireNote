package com.pro3.planner.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.pro3.planner.Interfaces.CanAddDeleteElement;
import com.pro3.planner.Interfaces.CanBeEdited;
import com.pro3.planner.R;

/**
 * Created by linus_000 on 12.11.2016.
 */

public class EditElementDialog extends SuperDialog {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String id = getArguments().getString("elementID");
        String type = getArguments().getString("type");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);

        TextView titleText = (TextView)title.findViewById(R.id.dialog_title);
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
        if (activity instanceof CanBeEdited) {
            editTitleText.setText(getActivity().getTitle());
        } else if (activity instanceof CanAddDeleteElement) {
            CanAddDeleteElement canAddDeleteElement = (CanAddDeleteElement) activity;
            editTitleText.setText(canAddDeleteElement.getElementAdapter().getElement(id).getTitle());
        }

        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (activity instanceof CanBeEdited) {
                    String title = editTitleText.getText().toString();
                    CanBeEdited canBeEdited = (CanBeEdited) getActivity();
                    canBeEdited.getElementReference().child("title").setValue(title);
                } else if (activity instanceof CanAddDeleteElement) {
                    String title = editTitleText.getText().toString();
                    CanAddDeleteElement canAddDeleteElement = (CanAddDeleteElement) activity;
                    canAddDeleteElement.getElementsReference().child(id).child("title").setValue(title);
                }

            }
        });

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_negative_button));
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
}
