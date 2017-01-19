package com.pro3.planner.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.Interfaces.ChecklistInterface;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.ItemTouchHelper.SimpleItemTouchHelperCallbackChecklist;
import com.pro3.planner.R;
import com.pro3.planner.adapters.ChecklistRecyclerAdapter;
import com.pro3.planner.baseClasses.ChecklistElement;
import com.pro3.planner.dialogs.ConfirmDialog;
import com.pro3.planner.dialogs.ImportFromTextDialog;
import com.pro3.planner.dialogs.ListAlertDialog;

import java.util.Iterator;
import java.util.List;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

import static com.pro3.planner.R.id.checkListView;
import static com.pro3.planner.R.id.swipeContainerChecklist;

public class ChecklistActivity extends BaseElementActivity implements ChecklistInterface, ConfirmDialogResult {

    private ChildEventListener mContentsListener;
    private RecyclerView recyclerView;
    private View.OnClickListener recycleOnClickListener;
    private View.OnLongClickListener recycleOnLongClickListener;
    private ChecklistRecyclerAdapter checklistRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ItemTouchHelper itemTouchHelper;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean started = false;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(swipeContainerChecklist);


        if (mElementReference != null) {
            mContentReference = mContentReference.child("elements");
            //Checklist Content Listener initialization
            initializeContentsListener();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mContentReference.removeEventListener(mContentsListener);
                    checklistRecyclerAdapter.clear();
                    mContentReference.addChildEventListener(mContentsListener);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        //Initialize the Listview and it's adapter and it's onClick Handler
        setUpListView();

        //Floating action button for adding checklist elements
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeAddChecklistElementDialog();
            }
        });

        /*
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
        */
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!started) {
            started = true;
            //Delay Animation for 200 ms so they are displayed correctly
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mContentReference.addChildEventListener(mContentsListener);
                }
            }, 200);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mContentsListener != null) {
            //Remove Content Listener
            mContentReference.removeEventListener(mContentsListener);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.menu_share) {
            String shareBody = checklistRecyclerAdapter.toString();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, elementTitle);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, ""));
        } else if (id == R.id.menu_reminder) {
            String shareBody = getString(R.string.element_checklist) + " \"" + elementTitle + "\" " + getString(R.string.from_app) + ": " + "\n" + checklistRecyclerAdapter.toString();
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setData(CalendarContract.Events.CONTENT_URI);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE, elementTitle + " - " + getString(R.string.app_name));
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, shareBody);
            startActivityForResult(calIntent, 123);
        } else if (id == R.id.clean_checklist) {
            DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.remove_checked_items_title), getString(R.string.remove_checked_items), "sweep", null);
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.menu_import) {
            DialogFragment dialogFragment = ImportFromTextDialog.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.check_checklist) {
            List<ChecklistElement> list = checklistRecyclerAdapter.getList();
            Iterator<ChecklistElement> it = list.iterator();
            while (it.hasNext()) {
                ChecklistElement element = it.next();
                mContentReference.child(element.getElementID()).child("finished").setValue(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checklist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    //Checklist content listener
    private void initializeContentsListener() {
        mContentsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //New checklist element added. Add to adapter
                ChecklistElement element = dataSnapshot.getValue(ChecklistElement.class);
                element.setElementID(dataSnapshot.getKey());
                checklistRecyclerAdapter.add(element);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Checklist element changed. Update Adapter
                ChecklistElement element = dataSnapshot.getValue(ChecklistElement.class);
                element.setElementID(dataSnapshot.getKey());
                checklistRecyclerAdapter.update(element);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Checklist element removed. Remove from Adapter
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

    //Click on Checklist Element
    private void initializeRecyclerOnClickListener() {
        recycleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Position of Element
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                ChecklistElement checklistElement = checklistRecyclerAdapter.getItem(itemPosition);
                //Reverse finished state and set value to database
                boolean finished = checklistElement.isFinished();
                finished = !finished;
                mContentReference.child(checklistElement.getElementID()).child("finished").setValue(finished);
            }
        };
    }

    //Long Click on Checklist Element
    private void initializeRecyclerOnLongClickListener() {
        recycleOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Get Position of Element
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                ChecklistElement element = checklistRecyclerAdapter.getItem(itemPosition);
                //Start new Dialog with edit and delete
                DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.edit_element_title), "editChecklistElement", element.getElementID(), null);
                dialog.show(getSupportFragmentManager(), "dialog");
                return true;
            }
        };
    }

    /**
     * Dialog to add new Element to the Checklist
     */
    private void initializeAddChecklistElementDialog() {
        //Create new AlertDialog
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View title = inflater.inflate(R.layout.alertdialog_custom_title, null);
        View content = inflater.inflate(R.layout.alertdialog_body_checklist_add, null);
        ((TextView) title.findViewById(R.id.dialog_title)).setText(getResources().getString(R.string.add_checklist_item_title));
        final EditText elementTitle = (EditText) content.findViewById(R.id.checklist_add_element_title);
        title.findViewById(R.id.dialog_title_container).setBackgroundColor(elementColor);
        alert.setCustomTitle(title);

        alert.setView(content);
        alert.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Create Checklist Element and add it to database
                String entry = elementTitle.getText().toString();
                DatabaseReference dRef = mContentReference.push();
                ChecklistElement checklistElement = new ChecklistElement(entry);
                dRef.setValue(checklistElement);
            }
        });

        alert.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        final AlertDialog dialog = alert.create();

        //Open keyboard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Set Dialog animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;

        //Press positive button on keyboard "enter"
        elementTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                }
                return false;
            }
        });

        dialog.show();
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

        //Initialize Click Listeners
        initializeRecyclerOnClickListener();
        initializeRecyclerOnLongClickListener();

        //Create adapter and add to List
        checklistRecyclerAdapter = new ChecklistRecyclerAdapter(this, recycleOnClickListener, recycleOnLongClickListener, recyclerView);
        recyclerView.setAdapter(checklistRecyclerAdapter);

        //Set ListView animations
        recyclerView.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
        recyclerView.getItemAnimator().setAddDuration(400);

        //Set ItemToucHelper for Swipe
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
        return mContentReference;
    }

    @Override
    public ChecklistRecyclerAdapter getCheckListRecyclerAdapter() {
        return checklistRecyclerAdapter;
    }

    @Override
    public void confirmDialogResult(boolean bool, String type, Bundle args) {
        super.confirmDialogResult(bool, type, args);

        if (bool) {
            if (type.equals("sweep")) {
                List<ChecklistElement> list = checklistRecyclerAdapter.getList();

                Iterator<ChecklistElement> it = list.iterator();

                while (it.hasNext()) {
                    ChecklistElement element = it.next();
                    if (element.isFinished()) {
                        mContentReference.child(element.getElementID()).removeValue();
                    }
                }
            }
        }
    }
}
