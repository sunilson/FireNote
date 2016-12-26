package com.pro3.planner.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
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
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.R;
import com.pro3.planner.dialogs.ConfirmDialog;

import static android.view.View.GONE;
import static com.pro3.planner.R.id.notepad;

public class NoteActivity extends BaseElementActivity implements ConfirmDialogResult {

    private EditText notePad;
    private boolean editMode;
    private MenuItem editButton, settingsButton, doneButton;
    private ValueEventListener mContentsListener;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        notePad = (EditText) findViewById(notepad);
        notePad.setScroller(new Scroller(this));
        notePad.setVerticalScrollBarEnabled(true);
        notePad.setMovementMethod(new ScrollingMovementMethod());

        if (mElementReference != null) {
            mContentReference = mContentReference.child("text");
            initializeContentsListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopEditMode();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mContentReference.addValueEventListener(mContentsListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mContentsListener != null) {
            mContentReference.removeEventListener(mContentsListener);
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
        settingsButton = menu.findItem(R.id.menu_settings);
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
            DialogFragment dialogFragment = ConfirmDialog.newInstance(getResources().getString(R.string.delete_note_title), getString(R.string.delete_dialog_confirm_text), "delete", null);
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.note_menu_done) {
            stopEditMode();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    private void initializeContentsListener() {

        mContentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
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
            if (!titleEdit) {
                startTitleEdit();
            }

            titleDoneButton.setVisibility(GONE);
            editMode = true;
            editButton.setVisible(false);
            settingsButton.setVisible(true);
            doneButton.setVisible(true);
            notePad.setEnabled(true);
            notePad.setFocusableInTouchMode(true);
            notePad.setFocusable(true);
            notePad.clearFocus();
            notePad.requestFocus();
            notePad.setRawInputType(InputType.TYPE_CLASS_TEXT);
            notePad.setTextIsSelectable(true);
            notePad.setSelection(notePad.getText().length());
            imm.showSoftInput(notePad, InputMethodManager.SHOW_IMPLICIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            Toast.makeText(this, R.string.start_edit_mode, Toast.LENGTH_SHORT).show();
        }
    }

    private void stopEditMode() {
        if (editMode) {
            if (titleEdit) {
                stopTitleEdit();
            }
            editMode = false;
            editButton.setVisible(true);
            settingsButton.setVisible(false);
            doneButton.setVisible(false);
            notePad.setEnabled(false);
            mContentReference.setValue(notePad.getText().toString());
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
