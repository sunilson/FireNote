package com.sunilson.firenote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.text.util.Linkify;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.R;

import static com.sunilson.firenote.R.id.notepad;

public class NoteActivity extends BaseElementActivity {

    private EditText notePad;
    private boolean editMode;
    private MenuItem settingsButton;
    private ValueEventListener mContentsListener;
    private FloatingActionButton fab;
    private RelativeLayout content;

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
            if(!((BaseApplication) getApplicationContext()).getInternetConnected()) {
                Toast.makeText(this, R.string.edit_no_connection, Toast.LENGTH_LONG).show();
            }
            mContentReference.setValue(notePad.getText().toString());
            mContentReference.removeEventListener(mContentsListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            String shareBody = notePad.getText().toString().trim();
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
            if(!((BaseApplication) getApplicationContext()).getInternetConnected()) {
                Toast.makeText(this, R.string.edit_no_connection, Toast.LENGTH_LONG).show();
            }
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

    /*
    ---------------------------
    ---- Other methods ----
    ---------------------------
     */

    @Override
    protected void showTutorial() {
        ShowcaseView.Builder showCaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(new ViewTarget(R.id.fab, this))
                .setContentTitle(getString(R.string.welcome_to_note))
                .setContentText(getString(R.string.tutorial_edit_note))
                .hideOnTouchOutside()
                .setStyle(R.style.ShowCaseViewStyle)
                .singleShot(2)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        findViewById(R.id.fab).setEnabled(true);
                        findViewById(R.id.menu_settings).setEnabled(false);
                        new ShowcaseView.Builder(NoteActivity.this)
                                .withMaterialShowcase()
                                .setTarget(new ViewTarget(R.id.menu_settings, NoteActivity.this))
                                .setContentTitle(getString(R.string.tutorial_edit_element_title))
                                .setContentText(getString(R.string.tutorial_edit_element))
                                .hideOnTouchOutside()
                                .setStyle(R.style.ShowCaseViewStyle)
                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                    @Override
                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                        findViewById(R.id.menu_settings).setEnabled(true);
                                        if (findViewById(R.id.menu_lock) != null) {
                                            findViewById(R.id.menu_lock).setEnabled(false);
                                            new ShowcaseView.Builder(NoteActivity.this)
                                                    .withMaterialShowcase()
                                                    .setTarget(new ViewTarget(R.id.menu_lock, NoteActivity.this))
                                                    .setContentTitle(getString(R.string.tutorial_lock_element_title))
                                                    .setContentText(getString(R.string.tutorial_lock_element))
                                                    .hideOnTouchOutside()
                                                    .setStyle(R.style.ShowCaseViewStyle)
                                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                        @Override
                                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                                            findViewById(R.id.menu_lock).setEnabled(true);
                                                        }

                                                        @Override
                                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                        }

                                                        @Override
                                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                        }

                                                        @Override
                                                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                                        }
                                                    })
                                                    .build();
                                        }
                                    }

                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                                    }
                                })
                                .build();
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        findViewById(R.id.fab).setEnabled(false);
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                });

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        showCaseView.build().setButtonPosition(lps);
    }
}
