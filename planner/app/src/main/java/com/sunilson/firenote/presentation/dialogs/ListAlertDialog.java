package com.sunilson.firenote.presentation.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.sunilson.firenote.presentation.application.BaseApplication;
import com.sunilson.firenote.Interfaces.BundleInterface;
import com.sunilson.firenote.Interfaces.ChecklistInterface;
import com.sunilson.firenote.Interfaces.ConfirmDialogResult;
import com.sunilson.firenote.Interfaces.ElementInterface;
import com.sunilson.firenote.Interfaces.HasSortableList;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.LocalSettingsManager;
import com.sunilson.firenote.R;
import com.sunilson.firenote.presentation.shared.BaseElementActivity;
import com.sunilson.firenote.adapters.DialogMenuAdapter;

/**
 * @author Linus Weiss
 */

/**
 * Dialog used to display lists of menu choices
 */
public class ListAlertDialog extends SuperDialog {

    private DialogMenuAdapter dialogAdapter;
    private MainActivityInterface mainActivityInterface;
    private BundleInterface bundleInterface;
    private DatabaseReference elementReference;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Dialog ocntent
        String type = getArguments().getString("type");
        View content = inflater.inflate(R.layout.alertdialog_menu_listview, null);
        ListView contentListView = content.findViewById(R.id.dialog_menu_listview);

        //Dialog title
        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        //Dialog list
        dialogAdapter = new DialogMenuAdapter(getActivity(), R.layout.alertdialog_menu_list_layout);
        contentListView.setAdapter(dialogAdapter);

        //Add the correct menu items to the dialog
        if (type.equals("sort")) {
            dialogAdapter.add(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.sort_ascending_date), R.drawable.ic_date_range_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.sort_descending_date), R.drawable.ic_date_range_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.sort_ascending_name), R.drawable.ic_text_fields_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.sort_descending_name), R.drawable.ic_text_fields_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.sort_by) + " " + getResources().getString(R.string.sort_category_name), R.drawable.ic_label_black_24dp);
        } else if (type.equals("addElement")) {
            dialogAdapter.add(getResources().getString(R.string.element_checklist), R.drawable.ic_done_all_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.element_note), R.drawable.ic_note_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.element_bundle), R.drawable.ic_list_black_24dp);
        } else if (type.equals("addElementBundle")) {
            dialogAdapter.add(getResources().getString(R.string.element_checklist), R.drawable.ic_done_all_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.element_note), R.drawable.ic_note_black_24dp);
        } else if (type.equals("editElement")) {
            dialogAdapter.add(getResources().getString(R.string.edit), R.drawable.ic_mode_edit_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.delete_element), R.drawable.ic_delete_black_24dp);
        } else if (type.equals("editChecklistElement")) {
            dialogAdapter.add(getResources().getString(R.string.edit), R.drawable.ic_mode_edit_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.delete_checklist_element), R.drawable.ic_delete_black_24dp);
        }

        builder.setView(content);
        AlertDialog dialog = builder.create();

        //Initialize the correct click listener for the dialog
        if (type.equals("sort")) {
            initializeSortItemListener(contentListView);
        } else if (type.equals("addElement") || type.equals("addElementBundle")) {
            initializeAddElementItemListener(contentListView);
        } else if (type.equals("editElement")) {
            initializeEditElementItemListener(contentListView, getArguments().getString("elementID"), getArguments().getString("elementType"));
        } else if (type.equals("editChecklistElement")) {
            initializeEditChecklistElementItemListener(contentListView, getArguments().getString("elementID"));
        }

        return dialog;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Create new ListAlertDialog
     *
     * @param title Window title
     * @param type Dialog type
     * @param elementID Element ID (when started from element)
     * @param elementType Element Type (when started from element)
     * @return new ListAlertDialog instance
     */
    public static ListAlertDialog newInstance(String title, String type, String elementID, String elementType) {
        ListAlertDialog dialog = new ListAlertDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("type", type);
        if (elementID != null) {
            args.putString("elementID", elementID);
        }
        if (elementType != null) {
            args.putString("elementType", elementType);
        }
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Set sorting method main element list
     *
     * @param contentListView
     */
    private void initializeSortItemListener(ListView contentListView) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Cast current activity to Interface "HasSortableList"
                HasSortableList hasSortingAdapter = (HasSortableList) getActivity();

                String strName = dialogAdapter.getName(position);
                String name = "";

                if (strName.equals(getString(R.string.sort_by) + " " + getString(R.string.sort_ascending_date))) {
                    name = getString(R.string.sort_ascending_date);
                } else if (strName.equals(getString(R.string.sort_by) + " " + getString(R.string.sort_descending_date))) {
                    name = getString(R.string.sort_descending_date);
                } else if (strName.equals(getString(R.string.sort_by) + " " + getString(R.string.sort_descending_name))) {
                    name = getString(R.string.sort_descending_name);
                } else if (strName.equals(getString(R.string.sort_by) + " " + getString(R.string.sort_ascending_name))) {
                    name = getString(R.string.sort_ascending_name);
                } else if (strName.equals(getString(R.string.sort_by) + " " + getString(R.string.sort_category_name))) {
                    name = getString(R.string.sort_category_name);
                }

                hasSortingAdapter.getSortTextView().setText(getString(R.string.current_sorthing_method) + " " + name);
                hasSortingAdapter.getElementAdapter().sort(name);
                hasSortingAdapter.getElementAdapter().notifyDataSetChanged();
                LocalSettingsManager.getInstance().setSortingMethod(name);
                getDialog().dismiss();
            }
        });
    }

    /**
     * On click on list element, start new AddElementDialog with correct type
     *
     * @param contentListView
     */
    private void initializeAddElementItemListener(ListView contentListView) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDialog().dismiss();
                Bundle bundle = new Bundle();
                bundle.putString("elementType", dialogAdapter.getName(position));
                ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
                confirmDialogResult.confirmDialogResult(true, "addElement", bundle);
            }
        });
    }

    /**
     * On click on list element, start new EditElementDialog with correct type and ID or delete it
     *
     * @param contentListView
     * @param elementID ID of element to be edited
     * @param elementType Type of element to be edited
     */
    private void initializeEditElementItemListener(ListView contentListView, final String elementID, final String elementType) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDialog().dismiss();
                final Activity activity = getActivity();
                String strName = dialogAdapter.getName(position);

                //Delete selected element
                if (strName.equals(getResources().getString(R.string.delete_element))) {
                    if (activity instanceof BundleInterface) {
                        bundleInterface = (BundleInterface) activity;
                    }
                    mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;

                    //Get the correct reference to the element
                    if (activity instanceof BundleInterface && !elementType.equals("bundle")) {
                        elementReference = bundleInterface.getElementsReference().child(elementID);
                        bundleInterface.setDeletedElement(true);
                        ElementInterface elementInterface = (ElementInterface) activity;
                        elementInterface.stopListeners();
                        elementReference.removeValue();
                        activity.finish();
                    } else if (activity instanceof ElementInterface) {
                        mainActivityInterface.setDeletedElement(true);
                        ElementInterface elementInterface = (ElementInterface) activity;
                        elementReference = elementInterface.getElementReference();
                        elementInterface.stopListeners();
                        elementReference.removeValue();
                        activity.finish();

                    } else {
                        mainActivityInterface.setDeletedElement(true);
                        elementReference = mainActivityInterface.getElementsReference().child(elementID);
                        elementReference.removeValue();
                    }

                    if(!((BaseApplication) getActivity().getApplicationContext()).getInternetConnected()) {
                        Toast.makeText(getActivity(), R.string.edit_no_connection, Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Start new EditElementDialog
                    Bundle bundle = new Bundle();
                    bundle.putString("elementType", elementType);
                    bundle.putString("elementID", elementID);
                    ConfirmDialogResult confirmDialogResult = (ConfirmDialogResult) getActivity();
                    confirmDialogResult.confirmDialogResult(true, "editElement", bundle);
                }
            }
        });
    }

    /**
     * On click on list element, start new AlertDialog for the selected ChecklistElement with correct type and ID or delete element
     *
     * @param contentListView
     * @param elementID
     */
    private void initializeEditChecklistElementItemListener(ListView contentListView, final String elementID) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ChecklistInterface checklistInterface = (ChecklistInterface) getActivity();

                String strName = dialogAdapter.getName(position);

                getDialog().dismiss();

                //Delete element
                if (strName.equals(getResources().getString(R.string.delete_checklist_element))) {
                    checklistInterface.getElementsReference().child(elementID).removeValue();
                } else if (strName.equals(getString(R.string.edit))) {
                    //Start new Edit Dialog of Checklist Element
                    final Activity activity = getActivity();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
                    View content = inflater.inflate(R.layout.alertdialog_body_checklist_add, null);
                    ((TextView) title.findViewById(R.id.dialog_title)).setText(getResources().getString(R.string.edit_checklist_item_title));
                    final EditText elementTitle = (EditText) content.findViewById(R.id.checklist_add_element_title);
                    String currentTitle = checklistInterface.getCheckListRecyclerAdapter().getItemWithID(elementID).getText();
                    if (currentTitle != null) {
                        elementTitle.setText(currentTitle);
                        elementTitle.setSelection(elementTitle.length());
                    }

                    if (activity instanceof BaseElementActivity) {
                        (title.findViewById(R.id.dialog_title_container)).setBackgroundColor(((BaseElementActivity) activity).getElementColor());
                    }
                    alert.setCustomTitle(title);

                    alert.setView(content);
                    alert.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String entry = elementTitle.getText().toString();
                            checklistInterface.getElementsReference().child(elementID).child("text").setValue(entry);
                        }
                    });

                    alert.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    final AlertDialog dialog = alert.create();
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

                    elementTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                            }
                            return false;
                        }
                    });

                    dialog.show();
                }
            }
        });
    }
}