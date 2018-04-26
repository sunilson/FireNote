package com.sunilson.firenote.presentation.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.sunilson.firenote.presentation.application.BaseApplication;
import com.sunilson.firenote.Interfaces.BundleInterface;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.R;
import com.sunilson.firenote.presentation.views.AddElementView;

import javax.inject.Inject;

/**
 * @author Linus Weiss
 */

/**
 * Dialog for adding new Elements
 */
public class AddElementDialog extends SuperDialog {

    private MainActivityInterface mainActivityInterface;
    private AddElementView content;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;

        //Set dialog content
        content = new AddElementView(getActivity(), mainActivityInterface.getSpinnerCategoryAdapter());
        final String elementType = getArguments().getString("elementType");

        //Set dialog title
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        //Restore state of dialog after orientation change
        if (savedInstanceState != null) {
            content.setColor(savedInstanceState.getInt("color"));
        }

        builder.setView(content);

        //On confirm, create new element
        builder.setPositiveButton(getString(R.string.confirm_add_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Element element = null;
                String elementTitle = content.getTitle();

                if (elementType.equals(activity.getString(R.string.element_note))) {
                    element = new Element("note", elementTitle);
                } else if (elementType.equals(activity.getString(R.string.element_checklist))) {
                    element = new Element("checklist", elementTitle);
                } else {
                    element = new Element("bundle", elementTitle);
                }

                element.setColor(content.getColor());

                //Push new element to the correct database reference (depending on if we are in a Bundle or not)
                DatabaseReference dRef = null;
                if (activity instanceof MainActivity) {
                    dRef = mainActivityInterface.getElementsReference().push();
                    element.setCategoryName(content.getCategory().getCategoryName());
                    element.setCategoryID(content.getCategory().getCategoryID());
                    dRef.setValue(element);
                } else {
                    BundleInterface bundleInterface = (BundleInterface) activity;
                    dRef = bundleInterface.getElementsReference().push();
                    element.setCategoryName(content.getCategory().getCategoryName());
                    element.setCategoryID(content.getCategory().getCategoryID());
                    dRef.setValue(element);
                }
                if(!((BaseApplication) getActivity().getApplicationContext()).getInternetConnected()) {
                    Toast.makeText(getActivity(), R.string.edit_no_connection, Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancel_add_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        setDialogLayoutParams(dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        return dialog;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color", content.getColor());
    }

    /**
     * Create new instance of AddElementDialog
     *
     * @param title Title of dialog window
     * @param elementType Element Type that should be created
     * @return New DialogFragment
     */
    public static AddElementDialog newInstance(String title, String elementType) {
        AddElementDialog dialog = new AddElementDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("elementType", elementType);
        dialog.setArguments(args);
        return dialog;
    }
}
