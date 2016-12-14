package com.pro3.planner.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.dialogs.ConfirmDialog;
import com.pro3.planner.dialogs.EditElementDialog;

public class NoteActivity extends BaseElementActivity implements ConfirmDialogResult {

    private EditText notePad;
    private String noteText;
    private boolean editMode;
    private MenuItem editButton, settingsButton, doneButton;
    private DatabaseReference mTextReference;
    private ValueEventListener mTextValueListener;

    String elementID;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        notePad = (EditText) findViewById(R.id.notepad);
        notePad.setScroller(new Scroller(this));
        notePad.setVerticalScrollBarEnabled(true);
        notePad.setMovementMethod(new ScrollingMovementMethod());


        initializeTextListener();
        initializeTitleListener();

        if (mElementReference != null) {
            mTextReference = mElementReference.child("text");
            mTextReference.addValueEventListener(mTextValueListener);
        }
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

        if (mTextValueListener != null && mTextReference != null) {
            mTextReference.removeEventListener(mTextValueListener);
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

    /*
    ----------------------
    ---- Options Menu ----
    ----------------------
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);

        editButton = menu.findItem(R.id.note_menu_edit);
        settingsButton = menu.findItem(R.id.note_menu_settings);
        doneButton = menu.findItem(R.id.note_menu_done);
        return super.onCreateOptionsMenu(menu);
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
                mElementReference.child("locked").setValue(!locked);
            } else {
                Toast.makeText(this, R.string.master_password_not_set, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

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

    /*
    ---------------------------
    ---- Interface methods ----
    ---------------------------
     */

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
