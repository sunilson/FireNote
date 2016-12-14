package com.pro3.planner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;

/**
 * Created by linus_000 on 05.11.2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private FirebaseUser user;
    protected FirebaseAuth mAuth;
    protected boolean connected;
    protected DatabaseReference mSettingsReference, mConnectedRef;
    protected ValueEventListener mConnectedRefListener;
    private FirebaseAuth.AuthStateListener mAuthListener;
    protected ChildEventListener mSettingsListener;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the Auth system and the Listener, which detects changes to the user state
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();

        //Handle online/offline status
        mConnectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        initializeOnlineListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Auth Listener zu Instanz hinzufügen
        mAuth.addAuthStateListener(mAuthListener);

        if (mAuth.getCurrentUser() != null) {
            initializeSettingsListener();
            mSettingsReference =  FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("settings");
            mSettingsReference.addChildEventListener(mSettingsListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if(mSettingsListener != null) {
            mSettingsReference.removeEventListener(mSettingsListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().goOnline();

        if (mConnectedRefListener != null) {
            mConnectedRef.addValueEventListener(mConnectedRefListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mConnectedRefListener != null) {
            mConnectedRef.removeEventListener(mConnectedRefListener);
        }
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    private void initializeOnlineListener() {
        mConnectedRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.i("Linus", "connected");
                } else {
                    Log.i("Linus", "disconnected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        };
    }

    private void initializeSettingsListener() {
        mSettingsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("masterPassword")) {
                    LocalSettingsManager.getInstance().setMasterPassword(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals("masterPassword")) {
                    LocalSettingsManager.getInstance().setMasterPassword("");
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

    private void initializeAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Check if user has verified his email
                    if(user.isEmailVerified()) {
                        //User is signed in and verified. Do nothing
                    } else {
                        //If not verified, sign user out and switch to login activity
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), R.string.verification_error, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                } else {
                    //User is signed out. Go to login
                    Toast.makeText(getApplicationContext(), R.string.action_logOut, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            }
        };
    }
}
