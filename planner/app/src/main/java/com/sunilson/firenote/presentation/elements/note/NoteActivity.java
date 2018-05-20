//package com.sunilson.firenote.presentation.elements.note;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.PersistableBundle;
//import android.provider.CalendarContract;
//import android.support.design.widget.FloatingActionButton;
//import android.view.GestureDetector;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.RelativeLayout;
//
//import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
//import com.github.amlcurran.showcaseview.ShowcaseView;
//import com.github.amlcurran.showcaseview.targets.ViewTarget;
//import com.google.firebase.database.ValueEventListener;
//import com.sunilson.firenote.R;
//import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity;
//
//import static com.sunilson.firenote.R.id.notepad;
//
//public class NoteActivity extends ElementActivity {
//
//    private EditText notePad;
//    private boolean editMode;
//    private MenuItem settingsButton;
//    private ValueEventListener mContentsListener;
//    private FloatingActionButton fab;
//    private RelativeLayout content;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//
//        content = (RelativeLayout) findViewById(R.id.content_note);
//        notePad = (EditText) findViewById(notepad);
//
//         notePad.setOnTouchListener(new View.OnTouchListener() {
//            private GestureDetector gestureDetector = new GestureDetector(NoteActivity.this, new GestureDetector.SimpleOnGestureListener(){
//                @Override
//                public boolean onDoubleTap(MotionEvent e) {
//                    if (!editMode) {
//                        startEditMode();
//                    }
//                    return super.onDoubleTap(e);
//                }
//            });
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                gestureDetector.onTouchEvent(motionEvent);
//                if (editMode) {
//                    imm.showSoftInput(notePad, InputMethodManager.SHOW_FORCED);
//                }
//                return false;
//            }
//        });
//
//        if (mElementReference != null) {
//            mContentReference = mContentReference.child("text");
//            initializeContentsListener();
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//    }
//
//    /**
//     * Stop editing if activity looses focus
//     *
//     * @param hasFocus
//     */
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        stopEditMode();
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_note, menu);
//        settingsButton = menu.findItem(R.id.menu_settings);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == android.R.id.home) {
//            if (editMode) {
//                stopEditMode();
//            } else {
//                this.finish();
//            }
//            return true;
//        } else if (id == R.id.menu_share) {
//            //Start share dialog
//            String shareBody = notePad.getText().toString().trim();
//            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//            sharingIntent.setType("text/plain");
//            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, elementTitle);
//            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//            startActivity(Intent.createChooser(sharingIntent, ""));
//        } else if (id == R.id.menu_reminder) {
//            //Go to default calendar app
//            String shareBody = getString(R.string.element_note) + " \"" + elementTitle + "\" " + getString(R.string.from_app) + ": " + "\n" + notePad.getText().toString().trim();
//            Intent calIntent = new Intent(Intent.ACTION_INSERT);
//            calIntent.setData(CalendarContract.Events.CONTENT_URI);
//            calIntent.setType("vnd.android.cursor.item/event");
//            calIntent.putExtra(CalendarContract.Events.TITLE, elementTitle + " - " + getString(R.string.app_name));
//            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, shareBody);
//            startActivityForResult(calIntent, 123);
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void showTutorial() {
//        ShowcaseView.Builder showCaseView = new ShowcaseView.Builder(this)
//                .withMaterialShowcase()
//                .setTarget(new ViewTarget(R.id.fab, this))
//                .setContentTitle(getString(R.string.welcome_to_note))
//                .setContentText(getString(R.string.tutorial_edit_note))
//                .hideOnTouchOutside()
//                .setStyle(R.style.ShowCaseViewStyle)
//                .singleShot(2)
//                .setShowcaseEventListener(new OnShowcaseEventListener() {
//                    @Override
//                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
//                        findViewById(R.id.fab).setEnabled(true);
//                        findViewById(R.id.menu_settings).setEnabled(false);
//                        new ShowcaseView.Builder(NoteActivity.this)
//                                .withMaterialShowcase()
//                                .setTarget(new ViewTarget(R.id.menu_settings, NoteActivity.this))
//                                .setContentTitle(getString(R.string.tutorial_edit_element_title))
//                                .setContentText(getString(R.string.tutorial_edit_element))
//                                .hideOnTouchOutside()
//                                .setStyle(R.style.ShowCaseViewStyle)
//                                .setShowcaseEventListener(new OnShowcaseEventListener() {
//                                    @Override
//                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
//                                        findViewById(R.id.menu_settings).setEnabled(true);
//                                        if (findViewById(R.id.menu_lock) != null) {
//                                            findViewById(R.id.menu_lock).setEnabled(false);
//                                            new ShowcaseView.Builder(NoteActivity.this)
//                                                    .withMaterialShowcase()
//                                                    .setTarget(new ViewTarget(R.id.menu_lock, NoteActivity.this))
//                                                    .setContentTitle(getString(R.string.tutorial_lock_element_title))
//                                                    .setContentText(getString(R.string.tutorial_lock_element))
//                                                    .hideOnTouchOutside()
//                                                    .setStyle(R.style.ShowCaseViewStyle)
//                                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
//                                                        @Override
//                                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
//                                                            findViewById(R.id.menu_lock).setEnabled(true);
//                                                        }
//
//                                                        @Override
//                                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
//
//                                                        }
//
//                                                        @Override
//                                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {
//
//                                                        }
//
//                                                        @Override
//                                                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
//
//                                                        }
//                                                    })
//                                                    .build();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
//
//                                    }
//
//                                    @Override
//                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
//
//                                    }
//
//                                    @Override
//                                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
//
//                                    }
//                                })
//                                .build();
//                    }
//
//                    @Override
//                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
//
//                    }
//
//                    @Override
//                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
//                        findViewById(R.id.fab).setEnabled(false);
//                    }
//
//                    @Override
//                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
//
//                    }
//                });
//
//        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
//        lps.setMargins(margin, margin, margin, margin);
//        showCaseView.build().setButtonPosition(lps);
//    }
//}
