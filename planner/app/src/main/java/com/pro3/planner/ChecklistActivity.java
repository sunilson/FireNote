package com.pro3.planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChecklistActivity extends BaseActivity {

    private ListView checkListView;
    private ChecklistAdapter checklistAdapter;
    private String elementID;

    private DatabaseReference mElementReference, mChecklistElementsReference, mSettingsReference;
    private ChildEventListener mChecklistElementsListener, mSettingsListener;
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();
        user = mAuth.getCurrentUser();

        //Get Element ID from clicked element
        Intent i = getIntent();
        elementID = i.getStringExtra("elementID");

        if(user != null) {
            mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(elementID);
        }

        //Initialize the Listview and it's adapter
        checkListView = (ListView) findViewById(R.id.checkListView);
        checklistAdapter = new ChecklistAdapter(this, R.layout.checklist_list_layout);
        checkListView.setAdapter(checklistAdapter);

        checkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChecklistElement checklistElement = (ChecklistElement) checklistAdapter.getItem(position);
                boolean finished = checklistElement.isFinished();
                finished = !finished;
                mChecklistElementsReference.child(checklistElement.getElementID()).child("finished").setValue(finished);
                checklistElement.setFinished(finished);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        initializeChecklistElementListener();
        if(mElementReference != null) {
            checklistAdapter.clear();
            mChecklistElementsReference = mElementReference.child("elements");
            mChecklistElementsReference.addChildEventListener(mChecklistElementsListener);
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

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
