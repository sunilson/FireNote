package com.pro3.planner.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.BundleInterface;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.R;
import com.pro3.planner.activities.BaseElementActivity;
import com.pro3.planner.views.EditElementView;

/**
 * Created by linus_000 on 05.01.2017.
 */

public class EditElementDialog extends SuperDialog {

    private MainActivityInterface mainActivityInterface;
    private BundleInterface bundleInterface;
    private DatabaseReference elementReference;
    private EditElementView content;
    private String savedTitle, savedCategoryID;
    private int savedColor = 0;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
        String elementType = getArguments().getString("elementType");
        String elementID = getArguments().getString("elementID");

        final Activity activity = (Activity) getContext();
        if (activity instanceof BundleInterface) {
            bundleInterface = (BundleInterface) activity;
        }

        if (activity instanceof BundleInterface && !elementType.equals("bundle")) {
            elementReference = bundleInterface.getElementsReference().child(elementID);
        } else if (activity instanceof ElementInterface) {
            ElementInterface elementInterface = (ElementInterface) activity;
            elementReference = elementInterface.getElementReference();
        } else {
            elementReference = mainActivityInterface.getElementsReference().child(elementID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);

        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        titleText.setText(getArguments().getString("title"));

        if (activity instanceof BaseElementActivity) {
            (title.findViewById(R.id.dialog_title_container)).setBackgroundColor(((BaseElementActivity) activity).getElementColor());
        }

        builder.setCustomTitle(title);

        content = new EditElementView(getContext(), mainActivityInterface.getSpinnerCategoryAdapter(), elementType, elementID);

        if (savedTitle != null && !savedTitle.equals("")) {
            content.setTitle(savedTitle);
        }

        if (savedCategoryID != null) {
            content.setCategory(savedCategoryID);
        }

        if (savedColor != 0) {
            content.setColor(savedColor);
        }

        builder.setView(content);
        builder.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = content.getTitle();
                        String categoryName = content.getCategory().getCategoryName();
                        String categoryID = content.getCategory().getCategoryID();
                        int color = content.getColor();
                        elementReference.child("title").setValue(title);
                        elementReference.child("categoryID").setValue(categoryID);
                        elementReference.child("categoryName").setValue(categoryName);
                        elementReference.child("color").setValue(color);
                    }
                }
        );

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );

        AlertDialog dialog = builder.create();
        setDialogLayoutParams(dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        savedTitle = content.getTitle();
        savedCategoryID = content.getCategory().getCategoryID();
        savedColor = content.getColor();

        super.onDismiss(dialog);
    }

    private void setDialogLayoutParams(Dialog dialog) {
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setAttributes(lp);
    }


    public static EditElementDialog newInstance(String title, String elementType, String elementID) {
        EditElementDialog dialog = new EditElementDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("elementType", elementType);
        args.putString("elementID", elementID);
        dialog.setArguments(args);
        return dialog;
    }
}
