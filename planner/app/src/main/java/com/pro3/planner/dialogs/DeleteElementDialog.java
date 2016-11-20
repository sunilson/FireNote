package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pro3.planner.Interfaces.CanBeEdited;
import com.pro3.planner.R;

/**
 * Created by linus_000 on 12.11.2016.
 */

public class DeleteElementDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);

        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        View content = inflater.inflate(R.layout.alertdialog_body_element_delete, null);
        ((TextView) content.findViewById(R.id.dialog_delete_element_text)).setText(getActivity().getResources().getString(R.string.delete_dialog_confirm_text) + " '" + getArguments().getString("elementTitle") + "'");
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_delete_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                CanBeEdited canBeEdited = (CanBeEdited) getActivity();
                canBeEdited.getElementReference().removeValue();
                dialog.dismiss();
                getActivity().finish();
            }
        });

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_negative_button));
    }

    public static DeleteElementDialog newInstance(String title, String elementTitle) {
        DeleteElementDialog dialog = new DeleteElementDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("elementTitle", elementTitle);
        dialog.setArguments(args);
        return dialog;
    }


}
