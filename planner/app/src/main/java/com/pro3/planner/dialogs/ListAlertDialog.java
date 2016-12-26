package com.pro3.planner.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.BundleInterface;
import com.pro3.planner.Interfaces.ChecklistInterface;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.Interfaces.HasSortableList;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.activities.BaseElementActivity;
import com.pro3.planner.activities.MainActivity;
import com.pro3.planner.adapters.DialogMenuAdapter;
import com.pro3.planner.baseClasses.Element;
import com.pro3.planner.views.AddElementView;
import com.pro3.planner.views.EditElementView;

/**
 * Created by linus_000 on 11.11.2016.
 */

public class ListAlertDialog extends SuperDialog {

    private DialogMenuAdapter dialogAdapter;
    //private AlertDialog dialog;
    private String elementType;
    private MainActivityInterface mainActivityInterface;
    private BundleInterface bundleInterface;

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

        Activity activity = getActivity();
        if (activity instanceof BaseElementActivity) {
            (title.findViewById(R.id.dialog_title_container)).setBackgroundColor(((BaseElementActivity) activity).getElementColor());
        }

        dialogAdapter = new DialogMenuAdapter(getActivity(), R.layout.alertdialog_menu_list_layout);
        contentListView.setAdapter(dialogAdapter);

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
            dialogAdapter.add(getResources().getString(R.string.delete_checklist_element), R.drawable.ic_delete_black_24dp);
        }

        builder.setView(content);
        AlertDialog dialog = builder.create();

        if (type.equals("sort")) {
            initializeSortItemListener(contentListView);
        } else if (type.equals("addElement") || type.equals("addElementBundle")) {
            initializeAddElementItemListener(contentListView);
        } else if (type.equals("editElement")) {
            initializeEditElementItemListener(contentListView, getArguments().getString("elementID"), getArguments().getString("elementType"));
        } else if (type.equals("editChecklistElement")) {
            initializeEditChecklistElementItemListener(contentListView, getArguments().getInt("extra"));
        }

        return dialog;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

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

    private void initializeSortItemListener(ListView contentListView) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                LocalSettingsManager.getInstance().setSortingMethod(name);
                getDialog().dismiss();
            }
        });
    }

    private void initializeAddElementItemListener(ListView contentListView) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
                final AddElementView content = new AddElementView(getActivity(), mainActivityInterface.getSpinnerCategoryAdapter());

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
                dialog.show();
            }
        });
    }

    private void initializeEditElementItemListener(ListView contentListView, final String elementID, final String elementType) {
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDialog().dismiss();
                final Activity activity = getActivity();
                String strName = dialogAdapter.getName(position);

                mainActivityInterface = (MainActivityInterface) ((BaseApplication) getContext().getApplicationContext()).mainContext;
                if (activity instanceof BundleInterface) {
                    bundleInterface = (BundleInterface) activity;
                }

                if (strName.equals(getResources().getString(R.string.delete_element))) {
                    if (mainActivityInterface != null) {
                        mainActivityInterface.getElementsReference().child(elementID).removeValue();
                    } else {
                        bundleInterface.getElementsReference().child(elementID).removeValue();
                    }
                } else if (strName.equals(getResources().getString(R.string.edit))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View title = inflater.inflate(R.layout.alertdialog_custom_title, null);

                    TextView titleText = (TextView) title.findViewById(R.id.dialog_title);
                    titleText.setText(getArguments().getString("title"));

                    if (activity instanceof BaseElementActivity) {
                        (title.findViewById(R.id.dialog_title_container)).setBackgroundColor(((BaseElementActivity) activity).getElementColor());
                    }

                    builder.setCustomTitle(title);

                    final EditElementView content = new EditElementView(getContext(), mainActivityInterface.getSpinnerCategoryAdapter(), elementType, elementID);

                    builder.setView(content);
                    builder.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String title = content.getTitle();
                                    String categoryName = content.getCategory().getCategoryName();
                                    String categoryID = content.getCategory().getCategoryID();
                                    int color = content.getColor();
                                    DatabaseReference elementReference = null;
                                    if (activity instanceof BundleInterface && !elementType.equals("bundle")) {
                                        BundleInterface bundleInterface = (BundleInterface) activity;
                                        elementReference = bundleInterface.getElementsReference().child(elementID);
                                    } else if (activity instanceof ElementInterface) {
                                        ElementInterface elementInterface = (ElementInterface) activity;
                                        elementReference = elementInterface.getElementReference();
                                    } else {
                                        elementReference = mainActivityInterface.getElementsReference().child(elementID);
                                    }
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

                    final AlertDialog dialog = builder.create();
                    setDialogLayoutParams(dialog);
                    dialog.show();
                }
            }
        });
    }

    private void setDialogLayoutParams(Dialog dialog) {
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setAttributes(lp);
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