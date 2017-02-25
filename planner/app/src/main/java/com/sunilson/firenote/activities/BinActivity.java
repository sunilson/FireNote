package com.sunilson.firenote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.Interfaces.BinInterface;
import com.sunilson.firenote.Interfaces.ConfirmDialogResult;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.ItemTouchHelper.SimpleItemTouchHelperCallbackMain;
import com.sunilson.firenote.R;
import com.sunilson.firenote.adapters.BinRecyclerAdapter;
import com.sunilson.firenote.baseClasses.Element;
import com.sunilson.firenote.dialogs.ConfirmDialog;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

public class BinActivity extends BaseActivity implements BinInterface, ConfirmDialogResult {

    private DatabaseReference mReference, mBinReference, mElementsRefernce, mContentsReference;
    private FirebaseUser user;
    private RecyclerView binList;
    private boolean restore = false;
    private View.OnClickListener recycleOnClickListener;
    private View.OnLongClickListener recycleOnLongClickListener;
    private ChildEventListener mBinListener;
    private BinRecyclerAdapter binRecyclerAdapter;
    private CoordinatorLayout coordinatorLayout;
    private Element currentlySelectedElement;
    private String elementID, elementName;
    private MainActivityInterface mainActivityInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mainActivityInterface = (MainActivityInterface) ((BaseApplication) getApplicationContext()).mainContext;
        user = mAuth.getCurrentUser();

        //Get passed arguments
        Intent i = getIntent();
        elementID = i.getStringExtra("elementID");
        elementName = i.getStringExtra("elementName");

        //If logged in
        if (user != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_bin);

            //Initialize List
            binList = (RecyclerView) findViewById(R.id.binList);
            binList.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            binList.setLayoutManager(linearLayoutManager);
            binRecyclerAdapter = new BinRecyclerAdapter(this, recycleOnClickListener, recycleOnLongClickListener);
            AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(binRecyclerAdapter);
            alphaInAnimationAdapter.setFirstOnly(false);
            alphaInAnimationAdapter.setDuration(200);
            binList.setAdapter(alphaInAnimationAdapter);
            binList.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
            binList.getItemAnimator().setAddDuration(400);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallbackMain(binRecyclerAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(binList);
            initializeBinListener();

            //Click listeners
            initializeOnClickListener();
            initializeOnLongClickListener();

            //Get reference (bundle or not)
            if (elementID == null) {
                mElementsRefernce = mReference.child("elements").child("main");
                mBinReference = mReference.child("bin").child("main");
                setTitle(getString(R.string.bin));
            } else {
                mElementsRefernce = mReference.child("elements").child("bundles").child(elementID);
                mBinReference = mReference.child("bin").child("bundles").child(elementID);
                setTitle(getString(R.string.bin) + " - " + elementName);
            }

            mContentsReference = mReference.child("contents");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mReference != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinReference.addChildEventListener(mBinListener);
                }
            }, 200);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBinListener != null) {
            mBinReference.removeEventListener(mBinListener);
        }

        binRecyclerAdapter.clear();
    }

    /**
     * Listener for bin elements
     */
    private void initializeBinListener() {
        mBinListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                element.setElementID(dataSnapshot.getKey());
                binRecyclerAdapter.add(element);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Element element = dataSnapshot.getValue(Element.class);
                binRecyclerAdapter.remove(element.getElementID());

                //If element was removed by a restore request
                if (restore) {
                    //Move element to "elements" and display "restored"-message
                    element.setElementID(element.getElementID());
                    mElementsRefernce.child(element.getElementID()).setValue(element);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.element_restored, 6000);
                    snackbar.show();
                    restore = false;
                } else {
                    //If element was deleted from bin, also delete its contents
                    if (element.getNoteType().equals("bundle")) {
                        mReference.child("elements").child("bundles").child(element.getElementID()).removeValue();
                    } else {
                        mContentsReference.child(element.getElementID()).removeValue();
                    }
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

    /**
     * Display Restore dialog on long click
     */
    private void initializeOnLongClickListener() {
        recycleOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!restore) {
                    int itemPosition = binList.getChildLayoutPosition(v);
                    currentlySelectedElement = binRecyclerAdapter.getItem(itemPosition);
                    DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.restore_element), getString(R.string.restore_element_question), "restore", null);
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                }

                return true;
            }
        };
    }

    /**
     * Dislay restore dialog on click
     */
    private void initializeOnClickListener() {
        recycleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!restore) {
                    int itemPosition = binList.getChildLayoutPosition(v);
                    currentlySelectedElement = binRecyclerAdapter.getItem(itemPosition);
                    DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.restore_element), getString(R.string.restore_element_question), "restore", null);
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

    /**
     * Create wipe confirm dialog
     */
    private void initializeDeleteAllDialog() {
        DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.clear_bin_title), getString(R.string.clear_bin_question), "clear", null);
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Get results from confirm dialog
     *
     * @param result
     * @param type
     * @param args
     */
    @Override
    public void confirmDialogResult(boolean result, String type, Bundle args) {
        if (result) {
            if (type.equals("clear")) {
                //Remove all entries in the bin
                mBinReference.removeValue();
            } else if (type.equals("restore")) {
                //Restore single element of bin
                if (currentlySelectedElement != null) {
                    mBinReference.child(currentlySelectedElement.getElementID()).removeValue();
                    if(!((BaseApplication) getApplicationContext()).getInternetConnected()) {
                        Toast.makeText(this, R.string.edit_no_connection, Toast.LENGTH_LONG).show();
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

    /**
     * Get reference to all bin elements
     *
     * @return All elements that are currently in bin
     */
    @Override
    public DatabaseReference getBinReference() {
        return mBinReference;
    }
}
