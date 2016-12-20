package com.pro3.planner.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.Element;

/**
 * Created by linus_000 on 09.12.2016.
 */

public abstract class BaseElementActivity extends BaseActivity implements ElementInterface {

    protected boolean locked;
    protected int elementColor;
    protected String elementID, elementType, elementTitle, parentID;
    protected ValueEventListener mElementListener;
    protected FirebaseUser user;
    protected DatabaseReference mElementReference, mContentReference;

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
        parentID = i.getStringExtra("parentID");
        elementColor = i.getIntExtra("elementColor", 1);
        setTitle(i.getStringExtra("elementTitle"));

        setColors();

        //Firebase Authentication
        user = mAuth.getCurrentUser();

        //Firebase Reference to the Checklist element we are currently in and the contents of that element
        if (user != null) {
            if (parentID == null) {
                mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child("main").child(elementID);
            } else {
                mElementReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child("bundles").child(parentID).child(elementID);
            }
            mContentReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("contents").child(elementID);
        }

        if (mElementReference != null) {
            initializeElementListener();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mElementReference.addValueEventListener(mElementListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mElementListener != null) {
            mElementReference.removeEventListener(mElementListener);
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

    protected void initializeElementListener() {
        mElementListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Element element = dataSnapshot.getValue(Element.class);
                updateElement(element);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void updateElement(Element element) {
        //Set Title
        setTitle(element.getTitle());
        elementTitle = element.getTitle();

        //Check Locked
        locked = element.getLocked();

        if (locked) {
            Toast.makeText(BaseElementActivity.this, R.string.locked, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(BaseElementActivity.this, R.string.unlocked, Toast.LENGTH_SHORT).show();
        }

        //Set colors
        elementColor = element.getColor();
        setColors();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_reminder) {
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setData(CalendarContract.Events.CONTENT_URI);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE, elementTitle + " - " + getString(R.string.app_name));
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "A reminder for your " + elementType + "!");
            startActivityForResult(calIntent, 123);
        } else if (id == R.id.menu_lock) {
            if (LocalSettingsManager.getInstance().getMasterPassword() != "") {
                mElementReference.child("locked").setValue(!locked);
            } else {
                Toast.makeText(this, R.string.master_password_not_set, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getElementColor() {
        return elementColor;
    }

    @Override
    public DatabaseReference getElementReference() {
        return mElementReference;
    }

    @Override
    public DatabaseReference getContentsReference() {
        return mContentReference;
    }

    @Override
    public String getElementTitle() {
        return elementTitle;
    }
}
