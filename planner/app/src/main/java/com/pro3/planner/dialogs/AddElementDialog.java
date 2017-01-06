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
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.R;
import com.pro3.planner.activities.MainActivity;
import com.pro3.planner.baseClasses.Element;
import com.pro3.planner.views.AddElementView;

/**
 * Created by linus_000 on 05.01.2017.
 */

public class AddElementDialog extends SuperDialog {

    private MainActivityInterface mainActivityInterface;
    private AddElementView content;
    private String savedTitle, savedCategoryID;
    private int savedColor = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;

        content = new AddElementView(getActivity(), mainActivityInterface.getSpinnerCategoryAdapter());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final Activity activity = getActivity();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String elementType = getArguments().getString("elementType");

        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);
        builder.setView(content);

        if (savedTitle != null && !savedTitle.equals("")) {
            content.setTitle(savedTitle);
        }

        if (savedCategoryID != null) {
            content.setCategory(savedCategoryID);
        }

        if (savedColor != 0) {
            content.setColor(savedColor);
        }

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


    public static AddElementDialog newInstance(String title, String elementType) {
        AddElementDialog dialog = new AddElementDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("elementType", elementType);
        dialog.setArguments(args);
        return dialog;
    }
}
