package com.pro3.planner.activities;

import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.Interfaces.CanBeEdited;
import com.pro3.planner.Interfaces.CanDeleteChecklistElement;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.ItemTouchHelper.SimpleItemTouchHelperCallbackChecklist;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ChecklistRecyclerAdapter;
import com.pro3.planner.baseClasses.ChecklistElement;
import com.pro3.planner.dialogs.ConfirmDialog;
import com.pro3.planner.dialogs.EditElementDialog;
import com.pro3.planner.dialogs.MenuAlertDialog;

import static com.pro3.planner.R.id.checkListView;

public class ChecklistActivity extends BaseActivity implements CanBeEdited, CanDeleteChecklistElement, ConfirmDialogResult {

    private String elementID;
    private boolean editMode = false;
    private int elementColor;
    private MenuItem editButton, settingsButton, doneButton;
    private DatabaseReference mElementReference, mChecklistElementsReference, mSettingsReference, mTitleReference, mConnectedRef;
    private ChildEventListener mChecklistElementsListener, mSettingsListener;
    private ValueEventListener mTitleListener, mConnectedRefListener;
    private FirebaseUser user;
    private SharedPreferences prefs;
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
        setContentView(R.layout.activity_checklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Firebase Authentication
        user = mAuth.getCurrentUser();

        //Get Element ID from clicked element and set Title
        Intent i = getIntent();
        elementID = i.getStringExtra("elementID");
        elementColor = i.getIntExtra("elementColor", 1);
        setTitle(i.getStringExtra("elementTitle"));

        setColors();

        //Firebase Reference to the Checklist element we are currently in
        if (user != null) {
            mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(elementID);
        }

        if (mElementReference != null) {
            initializeChecklistElementListener();
            initializeTitleListener();
            mChecklistElementsReference = mElementReference.child("elements");
            mTitleReference = mElementReference.child("title");
            mChecklistElementsReference.addChildEventListener(mChecklistElementsListener);
            mTitleReference.addValueEventListener(mTitleListener);
            //checklistRecyclerAdapter.clear();
        }

        //Initialize the Listview and it's adapter and it's onClick Handler
        setUpListView();

        //Handle online/offline status
        mConnectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        initializeOnlineListener();

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
    protected void onResume() {
        super.onStart();

        FirebaseDatabase.getInstance().goOnline();
        if (mConnectedRefListener != null) {
            mConnectedRef.addValueEventListener(mConnectedRefListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopEditMode();

        if (mConnectedRefListener != null) {
            mConnectedRef.removeEventListener(mConnectedRefListener);
        }

        FirebaseDatabase.getInstance().goOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mChecklistElementsListener != null) {
            mChecklistElementsReference.removeEventListener(mChecklistElementsListener);
        }

        if (mTitleListener != null) {
            mElementReference.child("title").removeEventListener(mTitleListener);
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

    private void initializeOnlineListener() {
        mConnectedRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.i("Linus", "connected");
                } else {
                    Log.i("Linus", "disconnected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        };
    }

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

    private void initializeTitleListener() {
        mTitleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newTitle = dataSnapshot.getValue(String.class);
                setTitle(newTitle);
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
                DialogFragment dialog = MenuAlertDialog.newInstance(getResources().getString(R.string.edit_element_title), "editChecklistElement", itemPosition);
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

    @Override
    public DatabaseReference getElementReference() {
        return mElementReference;
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

    private void setColors() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(elementColor);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(elementColor));

        //Darken notification bar color and set it to status bar. Only works in Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float[] hsv = new float[3];
            Color.colorToHSV(elementColor, hsv);
            hsv[2] *= 0.6f;
            int darkenedColor = Color.HSVToColor(hsv);

            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darkenedColor);
        }
    }

    @Override
    public void confirmDialogResult(boolean bool, String type) {
        if (bool) {
            if (type.equals("delete")) {
                mElementReference.removeValue();
                finish();
            }
        }
    }
}
