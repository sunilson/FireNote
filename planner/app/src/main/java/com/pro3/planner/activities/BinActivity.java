package com.pro3.planner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro3.planner.Interfaces.BinInterface;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.ItemTouchHelper.SimpleItemTouchHelperCallbackMain;
import com.pro3.planner.R;
import com.pro3.planner.adapters.BinRecyclerAdapter;
import com.pro3.planner.baseClasses.Checklist;
import com.pro3.planner.baseClasses.Element;
import com.pro3.planner.dialogs.ConfirmDialog;

public class BinActivity extends BaseActivity implements BinInterface, ConfirmDialogResult {

    private DatabaseReference mReference, mBinReference, mElementsRefernce, mChecklistBinElementsReference, mNoteBinElementsReference;
    private FirebaseUser user;
    private RecyclerView binList;
    private boolean restore = false;
    private View.OnClickListener recycleOnClickListener;
    private View.OnLongClickListener recycleOnLongClickListener;
    private ChildEventListener mBinListener, mChecklistElementsListener, mNoteElementsListener;
    private BinRecyclerAdapter binRecyclerAdapter;
    private CoordinatorLayout coordinatorLayout;
    private Element currentlySelectedElement;
    private String elementID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = mAuth.getCurrentUser();

        Intent i = getIntent();
        elementID = i.getStringExtra("elementID");

        if(user != null) {
            if(elementID == null) {
                mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            } else {
                mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(elementID);
            }

            if (mReference != null) {
                mBinReference = mReference.child("bin");
                if(elementID == null) {
                    mElementsRefernce = mReference.child("elements");
                    initializeBinListener();
                    mBinReference.addChildEventListener(mBinListener);
                } else {
                    mChecklistBinElementsReference = mReference.child("bin").child("checklists");
                    mNoteBinElementsReference = mReference.child("bin").child("notes");
                    initializeChecklistBinListener();
                    initializeNoteBinListener();
                    mChecklistBinElementsReference.addChildEventListener(mChecklistElementsListener);
                    mNoteBinElementsReference.addChildEventListener(mNoteElementsListener);
                }
            }

            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_bin);

            binList = (RecyclerView) findViewById(R.id.binList);
            binList.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            binList.setLayoutManager(linearLayoutManager);

            initializeOnClickListener();
            initializeOnLongClickListener();
            binRecyclerAdapter = new BinRecyclerAdapter(this, recycleOnClickListener, recycleOnLongClickListener);
            binList.setAdapter(binRecyclerAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallbackMain(binRecyclerAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(binList);
        }
    }

    @Override
    protected void onPause() {
        super.onStop();

        if (mConnectedRefListener != null) {
            mConnectedRef.removeEventListener(mConnectedRefListener);
        }

        FirebaseDatabase.getInstance().goOffline();
    }

    @Override
    protected void onResume() {
        super.onStart();
        FirebaseDatabase.getInstance().goOnline();

        if (mConnectedRefListener != null) {
            mConnectedRef.addValueEventListener(mConnectedRefListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBinListener != null) {
            mBinReference.removeEventListener(mBinListener);
        }
    }

    private void initializeChecklistBinListener() {
        mChecklistElementsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Checklist checklist = dataSnapshot.getValue(Checklist.class);
                binRecyclerAdapter.add(checklist);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Element element = dataSnapshot.getValue(Element.class);
                binRecyclerAdapter.remove(element.getNoteID());

                if (restore) {
                    DatabaseReference dRef = mReference.child("checklists").push();
                    Checklist checklist = dataSnapshot.getValue(Checklist.class);
                    checklist.setNoteID(dRef.getKey());
                    dRef.setValue(checklist);


                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.element_restored, 6000);
                    snackbar.show();

                    restore = false;
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initializeNoteBinListener() {
        mNoteElementsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                binRecyclerAdapter.add(element);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Element element = dataSnapshot.getValue(Element.class);
                binRecyclerAdapter.remove(element.getNoteID());

                if (restore) {
                    DatabaseReference dRef = mReference.child("notes").push();
                    element.setNoteID(dRef.getKey());
                    dRef.setValue(element);

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.element_restored, 6000);
                    snackbar.show();

                    restore = false;
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initializeBinListener() {
        mBinListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                if (element.getNoteType().equals("checklist")) {
                    Checklist checklist = dataSnapshot.getValue(Checklist.class);
                    binRecyclerAdapter.add(checklist);
                } else if (element.getNoteType().equals("bundle")) {
                    com.pro3.planner.baseClasses.Bundle bundle = dataSnapshot.getValue(com.pro3.planner.baseClasses.Bundle.class);
                    binRecyclerAdapter.add(bundle);
                } else {
                    binRecyclerAdapter.add(element);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Element element = dataSnapshot.getValue(Element.class);
                binRecyclerAdapter.remove(element.getNoteID());

                if (restore) {
                    DatabaseReference dRef = mNoteBinElementsReference.push();
                    if(element.getNoteType().equals("checklist")) {
                        Checklist checklist = dataSnapshot.getValue(Checklist.class);
                        checklist.setNoteID(dRef.getKey());
                        dRef.setValue(checklist);
                    } else if (element.getNoteType().equals("bundle")) {
                        com.pro3.planner.baseClasses.Bundle bundle = dataSnapshot.getValue(com.pro3.planner.baseClasses.Bundle.class);
                        bundle.setNoteID(dRef.getKey());
                        dRef.setValue(bundle);
                    }else {
                        element.setNoteID(dRef.getKey());
                        dRef.setValue(element);
                    }

                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.element_restored, 6000);
                    snackbar.show();

                    restore = false;
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initializeOnLongClickListener(){
        recycleOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!restore) {
                    int itemPosition = binList.getChildLayoutPosition(v);
                    currentlySelectedElement = binRecyclerAdapter.getItem(itemPosition);
                    DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.restore_element), getString(R.string.restore_element_question), "restore");
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                }

                return true;
            }
        };
    }


    private void initializeOnClickListener() {
        recycleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!restore) {
                    int itemPosition = binList.getChildLayoutPosition(v);
                    currentlySelectedElement = binRecyclerAdapter.getItem(itemPosition);
                    DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.restore_element), getString(R.string.restore_element_question), "restore");
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.bin_delete_all) {
            initializeDeleteAllDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeDeleteAllDialog(){
        DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.clear_bin_title), getString(R.string.clear_bin_question), "clear");
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void confirmDialogResult(boolean result, String type, Bundle args) {
        if (result) {
            if(type.equals("clear")) {
                mBinReference.removeValue();
            } else if (type.equals("restore")) {
                if (currentlySelectedElement != null) {
                    if (elementID == null) {
                        mBinReference.child(currentlySelectedElement.getNoteID()).removeValue();
                    } else {
                        mChecklistBinElementsReference.child(currentlySelectedElement.getNoteID()).removeValue();
                        mNoteBinElementsReference.child(currentlySelectedElement.getNoteID()).removeValue();
                    }
                }
                restore = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bin, menu);
        return true;
    }

    @Override
    public DatabaseReference getBinReference() {
        return mBinReference;
    }
}
