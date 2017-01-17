package com.pro3.planner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.CalendarContract;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.text.util.Linkify;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.R;

import java.util.Locale;

import static com.pro3.planner.R.id.notepad;

public class NoteActivity extends BaseElementActivity {

    private EditText notePad;
    private boolean editMode;
    private MenuItem settingsButton;
    private ValueEventListener mContentsListener;
    private FloatingActionButton fab;
    private RelativeLayout content;
    private TextToSpeech textToSpeech;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        content = (RelativeLayout) findViewById(R.id.content_note);
        notePad = (EditText) findViewById(notepad);

         notePad.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(NoteActivity.this, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (!editMode) {
                        startEditMode();
                    }
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                if (editMode) {
                    imm.showSoftInput(notePad, InputMethodManager.SHOW_FORCED);
                }
                return false;
            }
        });

        if (mElementReference != null) {
            mContentReference = mContentReference.child("text");
            initializeContentsListener();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode) {
                    stopEditMode();
                } else {
                    startEditMode();
                }
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
            mContentReference.setValue(notePad.getText().toString());
            mContentReference.removeEventListener(mContentsListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    /*
    ----------------------
    ---- Options Menu ----
    ----------------------
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);

        settingsButton = menu.findItem(R.id.menu_settings);
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
        } else if (id == R.id.menu_share) {

            String shareBody = getString(R.string.element_note) + " \"" + elementTitle + "\" " + getString(R.string.from_app) + ": " + "\n" + notePad.getText().toString().trim();

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, elementTitle);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, ""));
        } else if (id == R.id.menu_reminder) {
            String shareBody = getString(R.string.element_note) + " \"" + elementTitle + "\" " + getString(R.string.from_app) + ": " + "\n" + notePad.getText().toString().trim();

            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setData(CalendarContract.Events.CONTENT_URI);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE, elementTitle + " - " + getString(R.string.app_name));
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, shareBody);
            startActivityForResult(calIntent, 123);
        } else if (id == R.id.menu_text_to_speech) {
            String toSpeak = notePad.getText().toString();
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
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
            imm.showSoftInput(notePad, InputMethodManager.SHOW_FORCED);
            fab.setVisibility(View.GONE);
            editMode = true;
            notePad.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            notePad.setSingleLine(false);
            notePad.clearFocus();
            notePad.requestFocus();
            notePad.setLinksClickable(false);
            notePad.setAutoLinkMask(0);
            notePad.setText(notePad.getText().toString());
            notePad.setSelection(notePad.getText().length());
        }
    }

    private void stopEditMode() {
        if (editMode) {
            if (titleEdit) {
                stopTitleEdit();
            }
            fab.setVisibility(View.VISIBLE);
            imm.hideSoftInputFromWindow(notePad.getWindowToken(), 0);
            editMode = false;
            notePad.clearFocus();
            notePad.setLinksClickable(true);
            notePad.setAutoLinkMask(Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
            notePad.setText(notePad.getText().toString());
            notePad.setInputType(InputType.TYPE_NULL);
            notePad.setSingleLine(false);
            mContentReference.setValue(notePad.getText().toString());
            Toast.makeText(this, R.string.stop_edit_mode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void titleEditStarted() {
        super.titleEditStarted();
        if (!editMode) {
            startEditMode();
        }
        titleEditText.requestFocus();
    }

    @Override
    protected void titleEditStopped() {
        super.titleEditStopped();
        if (editMode) {
            stopEditMode();
        }
    }
}
