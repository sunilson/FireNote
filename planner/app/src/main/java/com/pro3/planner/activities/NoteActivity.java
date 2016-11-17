package com.pro3.planner.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.Interfaces.CanBeEdited;
import com.pro3.planner.R;
import com.pro3.planner.dialogs.DeleteElementDialog;
import com.pro3.planner.dialogs.EditElementDialog;

public class NoteActivity extends AppCompatActivity implements CanBeEdited {

    private EditText notePad;
    private String noteTitle;
    private String noteText;
    private int elementColor;
    private boolean editMode;
    private MenuItem editButton, settingsButton, doneButton;

    private DatabaseReference mElementReference, mTextReference;
    private ValueEventListener mTextValueListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SharedPreferences prefs;

    String elementID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent i = getIntent();
        noteTitle = i.getStringExtra("elementTitle");
        setTitle(noteTitle);
        elementID = i.getStringExtra("elementID");
        elementColor = i.getIntExtra("elementColor", 1);

        setColors();

        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();
        user = mAuth.getCurrentUser();

        //Firebase Reference to the Checklist element we are currently in
        if(user != null) {
            mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(elementID);
        }

        notePad = (EditText) findViewById(R.id.notepad);
        notePad.setScroller(new Scroller(this));
        notePad.setVerticalScrollBarEnabled(true);
        notePad.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        initializeTextListener();

        if (mElementReference != null) {
            mTextReference = mElementReference.child("text");
            mTextReference.addValueEventListener(mTextValueListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (mTextValueListener != null && mTextReference != null) {
            mTextReference.removeEventListener(mTextValueListener);
        }

        stopEditMode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (editMode) {
                stopEditMode();
            } else {
                this.finish();
            }
            return true;
        } else if (id == R.id.note_menu_edit) {
            startEditMode();
        } else if (id == R.id.note_menu_delete) {
            DialogFragment dialogFragment = DeleteElementDialog.newInstance(getResources().getString(R.string.delete_note_title), getTitle().toString());
            dialogFragment.show(getFragmentManager(), "dialog");
        } else if (id == R.id.note_menu_settings) {
            DialogFragment dialog = EditElementDialog.newInstance(getResources().getString(R.string.edit_checklist_title), "note");
            dialog.show(getFragmentManager(), "dialog");
        } else if (id == R.id.note_menu_done) {
            stopEditMode();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startEditMode() {
        if (!editMode) {
            editMode = true;
            editButton.setVisible(false);
            settingsButton.setVisible(true);
            doneButton.setVisible(true);
            notePad.setEnabled(true);
            notePad.setFocusableInTouchMode(true);
            notePad.setFocusable(true);
            notePad.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(notePad, InputMethodManager.SHOW_IMPLICIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void stopEditMode() {
        if (editMode) {
            editMode = false;
            editButton.setVisible(true);
            settingsButton.setVisible(false);
            doneButton.setVisible(false);
            notePad.setEnabled(false);
            mTextReference.setValue(notePad.getText().toString());
        }
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

    private void initializeTextListener() {
        mTextValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                noteText = text;
                notePad.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);

        editButton = menu.findItem(R.id.note_menu_edit);
        settingsButton = menu.findItem(R.id.note_menu_settings);
        doneButton = menu.findItem(R.id.note_menu_done);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public DatabaseReference getElementReference() {
        return mElementReference;
    }

    private void setColors() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(elementColor);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }
}
