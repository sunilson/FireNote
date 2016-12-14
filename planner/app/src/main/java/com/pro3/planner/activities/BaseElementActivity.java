package com.pro3.planner.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.Interfaces.ElementInterface;
import com.pro3.planner.R;

/**
 * Created by linus_000 on 09.12.2016.
 */

public abstract class BaseElementActivity extends BaseActivity implements ElementInterface{

    protected boolean locked;
    protected int elementColor;
    protected String elementID, elementType, elementTitle;
    protected ValueEventListener mTitleListener, mLockedListener;
    protected FirebaseUser user;
    protected DatabaseReference mElementReference, mTitleReference, mLockedReference;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        elementType = i.getStringExtra("elementType");

        switch (elementType) {
            case "checklist":
                setContentView(R.layout.activity_checklist);
                break;
            case "note":
                setContentView(R.layout.activity_note);
                break;
            case "bundle":
                setContentView(R.layout.activity_bundle);
                break;
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Element ID from clicked element and set Title
        elementID = i.getStringExtra("elementID");
        elementColor = i.getIntExtra("elementColor", 1);

        setTitle(i.getStringExtra("elementTitle"));

        setColors();

        //Firebase Authentication
        user = mAuth.getCurrentUser();

        //Firebase Reference to the Checklist element we are currently in
        if (user != null) {
            String parentID = i.getStringExtra("parent");
            if(parentID == null) {
                mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(elementID);
            } else {
                if (elementType.equals("checklist")) {
                    mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(parentID).child("checklists").child(elementID);
                } else {
                    mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child(parentID).child("notes").child(elementID);
                }
            }
        }

        if (mElementReference != null) {
            //Title Listener
            initializeTitleListener();
            mTitleReference = mElementReference.child("title");
            mTitleReference.addValueEventListener(mTitleListener);

            //Locked Listener
            initializeLockedListener();
            mLockedReference = mElementReference.child("locked");
            mLockedReference.addValueEventListener(mLockedListener);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTitleListener != null) {
            mElementReference.child("title").removeEventListener(mTitleListener);
        }

        if (mLockedListener != null) {
            mLockedReference.removeEventListener(mLockedListener);
        }
    }

    protected void setColors() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(elementColor);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf(elementColor));
        }

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

     /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    protected void initializeTitleListener() {
        mTitleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newTitle = dataSnapshot.getValue(String.class);
                setTitle(newTitle);
                elementTitle = newTitle;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initializeLockedListener(){
        mLockedListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locked = dataSnapshot.getValue(Boolean.class);
                if (locked) {
                    Toast.makeText(BaseElementActivity.this, R.string.locked, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BaseElementActivity.this, R.string.unlocked, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public DatabaseReference getElementReference() {
        return mElementReference;
    }

    @Override
    public String getElementTitle() {
        return elementTitle;
    }
}
