package com.pro3.planner.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.dialogs.ConfirmDialog;
import com.pro3.planner.dialogs.EditElementDialog;

public class NoteActivity extends BaseActivity implements ElementInterface, ConfirmDialogResult {

    private EditText notePad;
    private String noteTitle;
    private String noteText;
    private int elementColor;
    private boolean editMode;
    private MenuItem editButton, settingsButton, doneButton;
    private DatabaseReference mElementReference, mTextReference, mTitleReference, mConnectedRef;
    private ValueEventListener mTextValueListener, mTitleValueListener, mConnectedRefListener;
    private FirebaseUser user;

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
        user = mAuth.getCurrentUser();

        //Firebase Reference to the Checklist element we are currently in
        if (user != null) {
            mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(elementID);
        }

        notePad = (EditText) findViewById(R.id.notepad);
        notePad.setScroller(new Scroller(this));
        notePad.setVerticalScrollBarEnabled(true);
        notePad.setMovementMethod(new ScrollingMovementMethod());


        initializeTextListener();
        initializeTitleListener();

        //Handle online/offline status
        mConnectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        initializeOnlineListener();

        if (mElementReference != null) {
            mTextReference = mElementReference.child("text");
            mTextReference.addValueEventListener(mTextValueListener);

            mTitleReference = mElementReference.child("title");
            mTitleReference.addValueEventListener(mTitleValueListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        if (mTextValueListener != null && mTextReference != null) {
            mTextReference.removeEventListener(mTextValueListener);
        }

        if (mTitleValueListener != null && mTitleReference != null) {
            mTitleReference.removeEventListener(mTitleValueListener);
        }
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
            DialogFragment dialogFragment = ConfirmDialog.newInstance(getResources().getString(R.string.delete_note_title), getString(R.string.delete_dialog_confirm_text), "delete");
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.note_menu_settings) {
            DialogFragment dialog = EditElementDialog.newInstance(getResources().getString(R.string.edit_checklist_title), "note", "egal");
            dialog.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.note_menu_done) {
            stopEditMode();
        } else if (id == R.id.note_menu_lock) {
            if (LocalSettingsManager.getInstance().getMasterPassword() != "") {
                mElementReference.child("locked").setValue(true);
                Toast.makeText(this, R.string.locked, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.master_password_not_set, Toast.LENGTH_LONG).show();
            }
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
            Toast.makeText(this, R.string.start_edit_mode, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, R.string.stop_edit_mode, Toast.LENGTH_SHORT).show();
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

    private void initializeTitleListener() {
        mTitleValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title = dataSnapshot.getValue(String.class);
                noteTitle = title;
                setTitle(title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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
    public void confirmDialogResult(boolean bool, String type, Bundle args) {
        if (bool) {
            if (type.equals("delete")) {
                mElementReference.removeValue();
                finish();
            }
        }
    }
}
