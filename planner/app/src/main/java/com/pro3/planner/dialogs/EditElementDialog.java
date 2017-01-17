package com.pro3.planner.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.BundleInterface;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.R;
import com.pro3.planner.views.EditElementView;

/**
 * Created by linus_000 on 05.01.2017.
 */

public class EditElementDialog extends SuperDialog {

    private MainActivityInterface mainActivityInterface;
    private BundleInterface bundleInterface;
    private DatabaseReference elementReference;
    private EditElementView content;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
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

        titleText.setText(getArguments().getString("title"));

        builder.setCustomTitle(title);

        content = new EditElementView(getContext(), mainActivityInterface.getSpinnerCategoryAdapter(), elementType, elementID);

        if (savedInstanceState != null) {
            content.setColor(savedInstanceState.getInt("color"));
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();

        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private void setDialogLayoutParams(Dialog dialog) {
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color", content.getColor());
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
