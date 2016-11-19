package com.pro3.planner.activities;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.Interfaces.CanBeEdited;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ChecklistAdapter;
import com.pro3.planner.baseClasses.ChecklistElement;
import com.pro3.planner.dialogs.DeleteElementDialog;
import com.pro3.planner.dialogs.EditElementDialog;

public class ChecklistActivity extends BaseActivity implements CanBeEdited{

    private ListView checkListView;
    private ChecklistAdapter checklistAdapter;
    private String elementID;
    private boolean editMode = false;
    private int elementColor;
    private MenuItem editButton, settingsButton, doneButton;

    private DatabaseReference mElementReference, mChecklistElementsReference, mSettingsReference, mTitleReference;
    private ChildEventListener mChecklistElementsListener, mSettingsListener;
    private ValueEventListener mTitleListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();
        user = mAuth.getCurrentUser();

        //Get Element ID from clicked element and set Title
        Intent i = getIntent();
        elementID = i.getStringExtra("elementID");
        elementColor = i.getIntExtra("elementColor", 1);
        setTitle(i.getStringExtra("elementTitle"));

        setColors();

        //Firebase Reference to the Checklist element we are currently in
        if(user != null) {
            mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(elementID);
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
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if(mChecklistElementsListener != null) {
            mChecklistElementsReference.removeEventListener(mChecklistElementsListener);
        }

        if(mTitleListener != null) {
            mElementReference.child("title").removeEventListener(mTitleListener);
        }

        stopEditMode();
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
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        initializeChecklistElementListener();
        initializeTitleListener();

        if(mElementReference != null) {
            checklistAdapter.clear();
            mChecklistElementsReference = mElementReference.child("elements");
            mTitleReference = mElementReference.child("title");
            mChecklistElementsReference.addChildEventListener(mChecklistElementsListener);
            mTitleReference.addValueEventListener(mTitleListener);
        }
    }

    private void initializeAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Check if user has verified his email
                    if(user.isEmailVerified()) {
                        //User is signed in and verified. Do nothing
                    } else {
                        //If not verified, sign user out and switch to login activity
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), R.string.verification_error, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                } else {
                    //User is signed out. Go to login
                    Toast.makeText(getApplicationContext(), R.string.action_logOut, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            }
        };
    }

    private void initializeChecklistElementListener() {
        mChecklistElementsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChecklistElement element = dataSnapshot.getValue(ChecklistElement.class);
                checklistAdapter.add(element);
                int position = checklistAdapter.getPosition(element.getElementID());
                if (element.isFinished()) {
                    checkListView.setItemChecked(position, true);
                } else {
                    checkListView.setItemChecked(position, false);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChecklistElement element = dataSnapshot.getValue(ChecklistElement.class);
                int position = checklistAdapter.getPosition(element.getElementID());
                checklistAdapter.update(position, element);
                if (element.isFinished()) {
                    checkListView.setItemChecked(position, true);
                } else {
                    checkListView.setItemChecked(position, false);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String elementKey = dataSnapshot.getKey();
                checklistAdapter.remove(elementKey);
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

    private void setUpListView() {
        checkListView = (ListView) findViewById(R.id.checkListView);
        checklistAdapter = new ChecklistAdapter(this, R.layout.checklist_list_layout);
        checkListView.setAdapter(checklistAdapter);

        checkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChecklistElement checklistElement = (ChecklistElement) checklistAdapter.getItem(position);
                if (!editMode) {
                    boolean finished = checklistElement.isFinished();
                    finished = !finished;
                    mChecklistElementsReference.child(checklistElement.getElementID()).child("finished").setValue(finished);
                    checklistElement.setFinished(finished);
                } else {
                    mChecklistElementsReference.child(checklistElement.getElementID()).removeValue();
                    checkListView.setItemChecked(position, checklistElement.isFinished());
                }
            }
        });
    }

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
            DialogFragment dialogFragment = DeleteElementDialog.newInstance(getResources().getString(R.string.delete_checklist_title), getTitle().toString());
            dialogFragment.show(getFragmentManager(), "dialog");
        } else if (id == R.id.checklist_menu_done) {
            stopEditMode();
        } else if (id == R.id.checklist_menu_settings) {
            DialogFragment dialog = EditElementDialog.newInstance(getResources().getString(R.string.edit_checklist_title), "checklist");
            dialog.show(getFragmentManager(), "dialog");
        }
        return super.onOptionsItemSelected(item);
    }

    private void startEditMode() {
        if (!editMode) {
            editMode = true;
            editButton.setVisible(false);
            settingsButton.setVisible(true);
            doneButton.setVisible(true);
            checklistAdapter.toggleEditMode();
        }
    }

    private void stopEditMode() {
        if (editMode) {
            editMode = false;
            editButton.setVisible(true);
            settingsButton.setVisible(false);
            doneButton.setVisible(false);
            checklistAdapter.toggleEditMode();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checklist, menu);

        editButton = menu.findItem(R.id.checklist_menu_edit);
        settingsButton = menu.findItem(R.id.checklist_menu_settings);
        doneButton = menu.findItem(R.id.checklist_menu_done);

        return super.onCreateOptionsMenu(menu);
    }

    private void initializeAddChecklistElementDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        View content = inflater.inflate(R.layout.alertdialog_body_checklist_add, null);
        ((TextView)title.findViewById(R.id.dialog_title)).setText(getResources().getString(R.string.add_checklist_item_title));
        final TextView elementTitleTextView = (TextView) content.findViewById(R.id.checklist_add_element_title);
        alert.setCustomTitle(title);

        alert.setView(content);
        alert.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String entry = elementTitleTextView.getText().toString();
                DatabaseReference dRef = mChecklistElementsReference.push();
                String elementID = dRef.getKey();
                ChecklistElement checklistElement = new ChecklistElement(entry, elementID);
                dRef.setValue(checklistElement);
            }
        });

        alert.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    @Override
    public DatabaseReference getElementReference() {
        return mElementReference;
    }

    private void setColors() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(elementColor);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(elementColor));
    }
}
