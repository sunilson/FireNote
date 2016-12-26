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
import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.BinInterface;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.ItemTouchHelper.SimpleItemTouchHelperCallbackMain;
import com.pro3.planner.R;
import com.pro3.planner.adapters.BinRecyclerAdapter;
import com.pro3.planner.adapters.CategoryVisibilityAdapter;
import com.pro3.planner.baseClasses.Element;
import com.pro3.planner.dialogs.ConfirmDialog;

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
    private String elementID;
    private MainActivityInterface mainActivityInterface;
    private CategoryVisibilityAdapter CategoryVisibilityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainActivityInterface = (MainActivityInterface) ((BaseApplication) getApplicationContext()).mainContext;
        CategoryVisibilityAdapter = (CategoryVisibilityAdapter) mainActivityInterface.getListCategoryVisibilityAdapter();

        user = mAuth.getCurrentUser();

        Intent i = getIntent();
        elementID = i.getStringExtra("elementID");

        if (user != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
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
            initializeBinListener();
            if (elementID == null) {
                mElementsRefernce = mReference.child("elements").child("main");
                mBinReference = mReference.child("bin").child("main");
            } else {
                mElementsRefernce = mReference.child("elements").child("bundles").child(elementID);
                mBinReference = mReference.child("bin").child("bundles").child(elementID);
            }

            mContentsReference = mReference.child("contents");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mReference != null) {
            mBinReference.addChildEventListener(mBinListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBinListener != null) {
            mBinReference.removeEventListener(mBinListener);
        }
    }

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

                if (restore) {
                    element.setElementID(element.getElementID());
                    mElementsRefernce.child(element.getElementID()).setValue(element);
                } else {
                    mContentsReference.child(element.getElementID()).removeValue();
                }

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, R.string.element_restored, 6000);
                snackbar.show();

                restore = false;
            }


            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

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

    private void initializeDeleteAllDialog() {
        DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.clear_bin_title), getString(R.string.clear_bin_question), "clear", null);
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void confirmDialogResult(boolean result, String type, Bundle args) {
        if (result) {
            if (type.equals("clear")) {
                mBinReference.removeValue();
            } else if (type.equals("restore")) {
                if (currentlySelectedElement != null) {
                    mBinReference.child(currentlySelectedElement.getElementID()).removeValue();
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
