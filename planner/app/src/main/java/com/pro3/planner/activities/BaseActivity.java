package com.pro3.planner.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.pro3.planner.baseClasses.Settings;

/**
 * Created by linus_000 on 05.11.2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    protected ChildEventListener mSettingsListener;
    protected Settings settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the Auth system and the Listener, which detects changes to the user state
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();


    }

    private void initializeAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        //Check if user has verified his email
                        if(user.isEmailVerified()) {
                        } else {
                            //If not verified, do nothing
                        }
                    } else {
                        //User is signed out, do nothing
                    }
                }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Auth Listener zu Instanz hinzufügen
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
