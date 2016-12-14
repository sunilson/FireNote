package com.pro3.planner.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro3.planner.Interfaces.ChecklistInterface;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.ItemTouchHelper.SimpleItemTouchHelperCallbackChecklist;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ChecklistRecyclerAdapter;
import com.pro3.planner.baseClasses.ChecklistElement;
import com.pro3.planner.dialogs.ConfirmDialog;
import com.pro3.planner.dialogs.EditElementDialog;
import com.pro3.planner.dialogs.ListAlertDialog;

import static com.pro3.planner.R.id.checkListView;

public class ChecklistActivity extends BaseElementActivity implements ChecklistInterface, ConfirmDialogResult {

    private boolean editMode = false;
    private MenuItem editButton, settingsButton, doneButton;
    private DatabaseReference mChecklistElementsReference;
    private ChildEventListener mChecklistElementsListener;
    private RecyclerView recyclerView;
    private View.OnClickListener recycleOnClickListener;
    private View.OnLongClickListener recycleOnLongClickListener;
    private ChecklistRecyclerAdapter checklistRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ItemTouchHelper itemTouchHelper;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mElementReference != null) {
            //Checklist Element Listener
            initializeChecklistElementListener();
            mChecklistElementsReference = mElementReference.child("elements");
            mChecklistElementsReference.addChildEventListener(mChecklistElementsListener);
        }

        //Initialize the Listview and it's adapter and it's onClick Handler
        setUpListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeAddChecklistElementDialog();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (editMode) {
                stopEditMode();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopEditMode();
        FirebaseDatabase.getInstance().goOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mChecklistElementsListener != null) {
            mChecklistElementsReference.removeEventListener(mChecklistElementsListener);
        }
    }

    /*
    ----------------------
    ---- Options Menu ----
    ----------------------
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (editMode) {
                stopEditMode();
                return true;
            }
            this.finish();
            return true;
        } else if (id == R.id.checklist_menu_edit) {
            startEditMode();
        } else if (id == R.id.checklist_menu_delete) {
            DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.delete_checklist_title), getString(R.string.delete_dialog_confirm_text), "delete");
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.checklist_menu_done) {
            stopEditMode();
        } else if (id == R.id.checklist_menu_settings) {
            DialogFragment dialog = EditElementDialog.newInstance(getResources().getString(R.string.edit_checklist_title), "checklist", "egal");
            dialog.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.checklist_menu_lock) {
            if (LocalSettingsManager.getInstance().getMasterPassword() != "") {
                mElementReference.child("locked").setValue(!locked);
            } else {
                Toast.makeText(this, R.string.master_password_not_set, Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checklist, menu);

        editButton = menu.findItem(R.id.checklist_menu_edit);
        settingsButton = menu.findItem(R.id.checklist_menu_settings);
        doneButton = menu.findItem(R.id.checklist_menu_done);

        return super.onCreateOptionsMenu(menu);
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    private void initializeChecklistElementListener() {
        mChecklistElementsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChecklistElement element = dataSnapshot.getValue(ChecklistElement.class);
                checklistRecyclerAdapter.add(element);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChecklistElement element = dataSnapshot.getValue(ChecklistElement.class);
                checklistRecyclerAdapter.update(element);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String elementKey = dataSnapshot.getKey();
                checklistRecyclerAdapter.remove(elementKey);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initializeRecyclerOnClickListener() {
        recycleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                ChecklistElement checklistElement = checklistRecyclerAdapter.getItem(itemPosition);
                boolean finished = checklistElement.isFinished();
                finished = !finished;
                mChecklistElementsReference.child(checklistElement.getElementID()).child("finished").setValue(finished);
            }
        };
    }

    private void initializeRecyclerOnLongClickListener() {
        recycleOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.edit_element_title), "editChecklistElement", itemPosition);
                dialog.show(getSupportFragmentManager(), "dialog");
                return true;
            }
        };
    }

    private void initializeAddChecklistElementDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        View content = inflater.inflate(R.layout.alertdialog_body_checklist_add, null);
        ((TextView) title.findViewById(R.id.dialog_title)).setText(getResources().getString(R.string.add_checklist_item_title));
        final TextView elementTitleTextView = (TextView) content.findViewById(R.id.checklist_add_element_title);
        alert.setCustomTitle(title);

        alert.setView(content);
        alert.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String entry = elementTitleTextView.getText().toString();
                DatabaseReference dRef = mChecklistElementsReference.push();
                String elementID = dRef.getKey();
                ChecklistElement checklistElement = new ChecklistElement(entry, elementID, checklistRecyclerAdapter.getItemCount());
                dRef.setValue(checklistElement);
            }
        });

        alert.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    /*
    -------------------------------
    ---- Listview Initializing ----
    -------------------------------
     */

    private void setUpListView() {

        //RecyclerView Initialization
        recyclerView = (RecyclerView) findViewById(checkListView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        initializeRecyclerOnClickListener();
        initializeRecyclerOnLongClickListener();
        checklistRecyclerAdapter = new ChecklistRecyclerAdapter(this, recycleOnClickListener, recycleOnLongClickListener);
        recyclerView.setAdapter(checklistRecyclerAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallbackChecklist(checklistRecyclerAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /*
    ---------------------------
    ---- Interface methods ----
    ---------------------------
     */

    @Override
    public DatabaseReference getElementsReference() {
        return mChecklistElementsReference;
    }

    @Override
    public ChecklistRecyclerAdapter getCheckListRecyclerAdapter() {
        return checklistRecyclerAdapter;
    }

    /*
    ---------------------
    ---- Own methods ----
    ---------------------
     */

    private void startEditMode() {
        if (!editMode) {
            editMode = true;
            editButton.setVisible(false);
            settingsButton.setVisible(true);
            doneButton.setVisible(true);
            Toast.makeText(this, R.string.start_edit_mode, Toast.LENGTH_SHORT).show();
        }

    }

    private void stopEditMode() {
        if (editMode) {
            editMode = false;
            editButton.setVisible(true);
            settingsButton.setVisible(false);
            doneButton.setVisible(false);
            Toast.makeText(this, R.string.stop_edit_mode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void confirmDialogResult(boolean bool, String type, Bundle args) {
        if (bool) {
            if (type.equals("delete")) {
                mElementReference.removeValue();
                finish();
            }
        }
    }
}
