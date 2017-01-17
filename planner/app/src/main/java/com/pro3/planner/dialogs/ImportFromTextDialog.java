package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.Interfaces.ChecklistInterface;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.ChecklistElement;

import java.util.Scanner;

/**
 * Created by linus_000 on 17.01.2017.
 */

public class ImportFromTextDialog extends SuperDialog {

    private ChecklistInterface checklistInterface;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if (activity instanceof ChecklistInterface) {
            checklistInterface = (ChecklistInterface) activity;
        }

        titleText.setText(getContext().getString(R.string.import_from_textfile));
        builder.setCustomTitle(title);

        View content = inflater.inflate(R.layout.alertdialog_body_import_text, null);
        final EditText editText = (EditText) content.findViewById(R.id.import_text);
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int counter = 0;
                Scanner scanner = new Scanner(editText.getText().toString());

                while (scanner.hasNextLine()) {
                    if (counter < 20) {
                        if (checklistInterface != null) {
                            String next = scanner.nextLine();
                            if(next != null && !next.isEmpty()) {
                                DatabaseReference dRef = checklistInterface.getElementsReference().push();
                                ChecklistElement checklistElement = new ChecklistElement(next.trim());
                                if(next.charAt(0) == '*' || next.charAt(0) == '☒') {
                                    checklistElement.setFinished(true);
                                    checklistElement.setText(next.substring(1).trim());
                                } else if (next.charAt(0) == '☐') {
                                    checklistElement.setText(next.substring(1).trim());
                                }
                                dRef.setValue(checklistElement);
                            }
                        }
                    } else {
                        break;
                    }
                    counter++;
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        return builder.create();
    }

    public static ImportFromTextDialog newInstance() {
        ImportFromTextDialog dialog = new ImportFromTextDialog();
        return dialog;
    }

}
