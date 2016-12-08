package com.pro3.planner.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.Interfaces.MainInterface;
import com.pro3.planner.Interfaces.ChecklistInterface;
import com.pro3.planner.Interfaces.HasSortableList;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.adapters.DialogMenuAdapter;
import com.pro3.planner.baseClasses.Checklist;
import com.pro3.planner.baseClasses.Element;
import com.pro3.planner.baseClasses.Note;
import com.pro3.planner.views.AddElementView;

/**
 * Created by linus_000 on 11.11.2016.
 */

public class MenuAlertDialog extends SuperDialog {

    private DialogMenuAdapter dialogAdapter;
    //private AlertDialog dialog;
    private String elementType;
    private DatabaseReference mCategoryReference;
    private ChildEventListener mCategoryListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String type = getArguments().getString("type");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        View content = inflater.inflate(R.layout.alertdialog_menu_listview, null);

        TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
        ListView contentListView = (ListView) content.findViewById(R.id.dialog_menu_listview);

        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        dialogAdapter = new DialogMenuAdapter(getActivity(), R.layout.alertdialog_menu_list_layout);
        contentListView.setAdapter(dialogAdapter);

        if (type.equals("sort")) {
            dialogAdapter.add(getResources().getString(R.string.menu_sort_ascending_date), R.drawable.ic_date_range_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.menu_sort_descending_date), R.drawable.ic_date_range_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.menu_sort_ascending_name), R.drawable.ic_text_fields_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.menu_sort_descending_name), R.drawable.ic_text_fields_black_24dp);
        } else if (type.equals("addElement")) {
            dialogAdapter.add(getResources().getString(R.string.element_checklist), R.drawable.ic_done_all_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.element_note), R.drawable.ic_note_black_24dp);
        } else if (type.equals("editElement")) {
            dialogAdapter.add(getResources().getString(R.string.delete_element), R.drawable.ic_delete_black_24dp);
            dialogAdapter.add(getResources().getString(R.string.edit), R.drawable.ic_mode_edit_black_24dp);
        } else if (type.equals("editChecklistElement")) {
            dialogAdapter.add(getResources().getString(R.string.delete_checklist_element), R.drawable.ic_delete_black_24dp);
        }

        builder.setView(content);
        AlertDialog dialog = builder.create();

        if (type.equals("sort")) {
            initializeSortItemListener(contentListView);
        } else if (type.equals("addElement")) {
            initializeAddElementItemListener(contentListView);
        } else if (type.equals("editElement")) {
            initializeEditElementItemListener(contentListView, getArguments().getInt("extra"));
        } else if (type.equals("editChecklistElement")) {
            initializeEditChecklistElementItemListener(contentListView, getArguments().getInt("extra"));
        }

        return dialog;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static MenuAlertDialog newInstance(String title, String type, int extra) {
        MenuAlertDialog dialog = new MenuAlertDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("type", type);
        args.putInt("extra", extra);
        dialog.setArguments(args);
        return dialog;
    }

    private void initializeSortItemListener(ListView contentListView) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HasSortableList hasSortingAdapter = (HasSortableList) getActivity();

                String strName = dialogAdapter.getName(position);
                String shortName = "dateAscending";
                SharedPreferences.Editor editor = hasSortingAdapter.getSharedPrefs().edit();

                if (strName.equals(getString(R.string.menu_sort_ascending_date))) {
                    shortName = "dateAscending";
                } else if (strName.equals(getString(R.string.menu_sort_descending_date))) {
                    shortName = "dateDescending";
                } else if (strName.equals(getResources().getString(R.string.menu_sort_descending_name))) {
                    shortName = "nameDescending";
                } else if (strName.equals(getResources().getString(R.string.menu_sort_ascending_name))) {
                    shortName = "nameAscending";
                }

                hasSortingAdapter.getElementAdapter().sort(shortName);
                LocalSettingsManager.getInstance().setSortingMethod(shortName);
                getDialog().dismiss();
            }
        });
    }

    private void initializeAddElementItemListener(ListView contentListView) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final MainInterface mainInterface = (MainInterface) getActivity();
                final AddElementView content = new AddElementView(getActivity(), mainInterface.getSpinnerCategoryAdapter());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final Activity activity = getActivity();
                View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                elementType = dialogAdapter.getName(position);
                getDialog().dismiss();

                TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
                titleText.setText(getArguments().getString("title"));
                builder.setCustomTitle(title);
                builder.setView(content);

                builder.setPositiveButton(getString(R.string.confirm_add_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Element element = null;
                        String elementTitle = content.getTitle();

                        if (elementType.equals(activity.getString(R.string.element_checklist))) {
                            element = new Checklist("checklist", elementTitle, activity);
                        } else if (elementType.equals(activity.getString(R.string.element_note))) {
                            element = new Note("note", elementTitle, activity);
                        }

                        element.setColor(content.getColor());
                        element.setCategory(content.getCategory());

                        DatabaseReference dRef = mainInterface.getElementsReference().push();
                        element.setNoteID(dRef.getKey());
                        dRef.setValue(element);
                    }
                });

                builder.setNegativeButton(getString(R.string.cancel_add_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog dialog = builder.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_negative_button));
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.dialog_negative_button));
    }

    private void initializeEditElementItemListener(ListView contentListView, final int elementPosition) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainInterface mainInterface = (MainInterface) getActivity();

                String elementID = mainInterface.getElementAdapter().getItem(elementPosition).getNoteID();
                String strName = dialogAdapter.getName(position);

                if (strName.equals(getResources().getString(R.string.delete_element))) {
                    mainInterface.getElementsReference().child((mainInterface.getElementAdapter().getItem(elementPosition)).getNoteID()).removeValue();
                } else if (strName.equals(getResources().getString(R.string.edit))) {
                    DialogFragment dialog = EditElementDialog.newInstance(getResources().getString(R.string.edit_checklist_title), "checklist", elementID);
                    dialog.show(getFragmentManager(), "dialog");
                }

                getDialog().dismiss();
            }
        });
    }

    private void initializeEditChecklistElementItemListener(ListView contentListView, final int elementPosition) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChecklistInterface checklistInterface = (ChecklistInterface) getActivity();
                String elementID = checklistInterface.getCheckListRecyclerAdapter().getItem(elementPosition).getElementID();

                String strName = dialogAdapter.getName(position);

                if (strName.equals(getResources().getString(R.string.delete_checklist_element))) {
                    checklistInterface.getElementsReference().child(elementID).removeValue();
                }

                getDialog().dismiss();
            }
        });
    }
}
