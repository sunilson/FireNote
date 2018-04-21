package com.sunilson.firenote.presentation.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.Interfaces.BundleInterface;
import com.sunilson.firenote.Interfaces.ElementInterface;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.R;
import com.sunilson.firenote.presentation.views.EditElementView;

/**
 * @author Linus Weiss
 */

/**
 * Dialog used to edit existing elements
 */
public class EditElementDialog extends SuperDialog {

    private BundleInterface bundleInterface;
    private DatabaseReference elementReference;
    private EditElementView content;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
        String elementType = getArguments().getString("elementType");
        String elementID = getArguments().getString("elementID");

        Activity activity = (Activity) getContext();

        if (activity instanceof BundleInterface) {
            bundleInterface = (BundleInterface) activity;
        }

        //Get correct database reference
        if (elementType != null && elementID != null) {
            if (activity instanceof BundleInterface && !elementType.equals("bundle")) {
                elementReference = bundleInterface.getElementsReference().child(elementID);
            } else if (activity instanceof ElementInterface) {
                ElementInterface elementInterface = (ElementInterface) activity;
                elementReference = elementInterface.getElementReference();
            } else {
                elementReference = mainActivityInterface.getElementsReference().child(elementID);
            }
        }

        //Set dialog title
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        //Set dialog content
        content = new EditElementView(getContext(), mainActivityInterface.getSpinnerCategoryAdapter(), elementType, elementID);

        //If dialog has been resumed, restore color list state
        if (savedInstanceState != null) {
            content.setColor(savedInstanceState.getInt("color"));
        }
        builder.setView(content);

        //Initialize "Confirm" button
        builder.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Get current values
                        String title = content.getTitle();
                        String categoryName = content.getCategory().getCategoryName();
                        String categoryID = content.getCategory().getCategoryID();
                        int color = content.getColor();
                        if(!((BaseApplication) getActivity().getApplicationContext()).getInternetConnected()) {
                            Toast.makeText(getActivity(), R.string.edit_no_connection, Toast.LENGTH_LONG).show();
                        }

                        //Apply to values to database
                        elementReference.child("title").setValue(title);
                        elementReference.child("categoryID").setValue(categoryID);
                        elementReference.child("categoryName").setValue(categoryName);
                        elementReference.child("color").setValue(color);
                    }
                }
        );

        //Initialize "Cancel" button
        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );

        AlertDialog dialog = builder.create();
        setDialogLayoutParams(dialog);
        //Add open and close animation
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        }
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Don't destroy references of dialog when orientation is changed(activity is restarted)
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        //Avoiding that the dialog is closed on orientation change
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private void setDialogLayoutParams(Dialog dialog) {
        if (dialog.getWindow() != null) {
            //Set dialog width and height to fit screen
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            dialog.getWindow().setAttributes(lp);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Restore color list state. EditTexts are restored automatically
        outState.putInt("color", content.getColor());
    }

    /**
     * Create new EditElementDialog
     *
     * @param title Title of dialog window
     * @param elementType Type of element that is being edited
     * @param elementID Id of element that is being edited
     * @return New EditElementDialog
     */
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
