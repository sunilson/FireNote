package com.pro3.planner.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pro3.planner.HasSortableList;
import com.pro3.planner.R;
import com.pro3.planner.adapters.DialogMenuAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linus_000 on 11.11.2016.
 */

public class SortingAlertDialog extends DialogFragment {

    private DialogMenuAdapter sortingDialogAdapter;
    private AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        List<String> bundleElements = new ArrayList<>();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        View content = inflater.inflate(R.layout.alertdialog_menu_listview, null);

        TextView titleText = (TextView)title.findViewById(R.id.dialog_title);
        ListView contentListView = (ListView) content.findViewById(R.id.dialog_menu_listview);

        titleText.setText(getArguments().getString("title"));
        builder.setCustomTitle(title);

        sortingDialogAdapter = new DialogMenuAdapter(getActivity(), R.layout.alertdialog_menu_list_layout);
        contentListView.setAdapter(sortingDialogAdapter);
        sortingDialogAdapter.add(getResources().getString(R.string.menu_sort_ascending_date), R.drawable.ic_done_all_black_24dp);
        sortingDialogAdapter.add(getResources().getString(R.string.menu_sort_descending_date), R.drawable.ic_note_black_24dp);

        builder.setView(content);
        dialog = builder.create();

        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HasSortableList hasSortingAdapter = (HasSortableList) getActivity();

                String strName = sortingDialogAdapter.getName(position);
                String shortName = "dateAscending";
                SharedPreferences.Editor editor = hasSortingAdapter.getSharedPrefs().edit();

                if(strName.equals(getString(R.string.menu_sort_ascending_date))) {

                } else if (strName.equals(getString(R.string.menu_sort_descending_date))){
                    shortName = "dateDescending";
                }

                hasSortingAdapter.getElementAdapter().sort(shortName);

                editor.putString("mainElementSorting", shortName);
                editor.commit();

                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static SortingAlertDialog newInstance(String title) {
        SortingAlertDialog dialog = new SortingAlertDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
    }
}
