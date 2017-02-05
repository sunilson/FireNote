package com.sunilson.firenote.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.LocalSettingsManager;
import com.sunilson.firenote.R;

/**
 * @author Linus Weiss
 *
 *
 *
 * Created by Linus Weiss on 05.11.2016.
 */

/**
 * Base Class of all other activities. Contains basic stuff like Internet Connection Listener and Authentication
 * Listener
 */
public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseUser user;
    protected FirebaseAuth mAuth;
    protected boolean connected;
    protected DatabaseReference mSettingsReference, mConnectedRef;
    protected ValueEventListener mConnectedRefListener;
    private FirebaseAuth.AuthStateListener mAuthListener;
    protected ChildEventListener mSettingsListener;
    private SharedPreferences prefs;
    protected LayoutInflater inflater;
    private int googleSignInRequestCode = 1;
    private GoogleApiClient mGoogleApiClient;
    private AlertDialog reAuthDialog;
    protected BaseApplication baseApplication;
    private String reAuthType = "";

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = getLayoutInflater();

        //Initialize the Auth system and the Listener, which detects changes to the user state
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();

        if (mAuth.getCurrentUser() != null) {
            //Prefs initialization
            prefs = this.getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
            LocalSettingsManager.getInstance().setPrefs(prefs);
        }

        //Handle online/offline status
        mConnectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        initializeOnlineListener();

        //Google Sign In Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("376414129715-ehhkuv1f9acftujtvuk0r9biir5c98v2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Ende Google Sign IN

        ImageView view = (ImageView)findViewById(android.R.id.home);
        if (view != null) {
            view.setPadding(0, 0, 0, 0);
        }

        //Get the baseApplication and store it for usage in all other activities
        baseApplication = (BaseApplication)this.getApplicationContext();

        //Settings Listener anlegen
        initializeSettingsListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If App is resumed, send current Activity to BaseApplication to assign to reference
        baseApplication.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        //If Activity is paused in any way, clear the current Activity references
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        //Remove currentActivity Reference from BaseApplication
        Activity currActivity = baseApplication.getCurrentActivity();
        if (this.equals(currActivity)) {
            baseApplication.setCurrentActivity(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Auth Listener starten
        mAuth.addAuthStateListener(mAuthListener);

        //If user is logged in
        if (mAuth.getCurrentUser() != null) {
            //Add settingsListener to settingsReference
            mSettingsReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("settings");
            mSettingsReference.addChildEventListener(mSettingsListener);

            //Add connectedListener to connectedReference
            mConnectedRef.addValueEventListener(mConnectedRefListener);
        }
    }

    //Wenn App is stopped or Activity is closed
    @Override
    protected void onStop() {
        super.onStop();

        //Remove all Listeners
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (mSettingsListener != null && mSettingsReference != null) {
            mSettingsReference.removeEventListener(mSettingsListener);
        }
        if (mConnectedRefListener != null && mConnectedRef != null) {
            mConnectedRef.removeEventListener(mConnectedRefListener);
        }
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    //Checks if we are still connected to Firebase Database
    private void initializeOnlineListener() {
        mConnectedRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                Log.i("Linus", Boolean.toString(connected));
                connectionChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };
    }

    //Listens to changes to the "Settings" part of the Database
    private void initializeSettingsListener() {
        mSettingsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("masterPassword")) {
                    LocalSettingsManager.getInstance().setMasterPassword(dataSnapshot.getValue(String.class));
                }

                if (dataSnapshot.getKey().equals("deleted")) {
                    if (dataSnapshot.getValue(Boolean.class)) {
                        if (mAuth.getCurrentUser() != null) {
                            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                            mReference.child("contents").removeValue();
                            mReference.child("elements").removeValue();
                            mReference.child("bin").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mAuth.signOut();
                                    Toast.makeText(BaseActivity.this, R.string.account_deleted, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("masterPassword")) {
                    LocalSettingsManager.getInstance().setMasterPassword(dataSnapshot.getValue(String.class));
                }
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

    //Listening to changes to the Authentication state of the current Firebase Session
    private void initializeAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Check if user has verified his email
                    if (user.isEmailVerified()) {
                        //User is signed in and verified. Do nothing
                        //user.getToken(true);
                    } else {
                        //If not verified, sign user out and switch to login activity
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), R.string.verification_error, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity(i);
                    }
                } else {
                    //User is signed out. Go to login
                    Intent i = new Intent(getApplicationContext(), StartActivity.class);
                    startActivity(i);
                }
            }
        };
    }

    /**
     * Getter for connected value
     *
     * @return If Application is connected to Firebase Database
     */
    public boolean getConnected() {
        return connected;
    }

    /**
     * Gets called when connection state of Firebase Database changes. Can be
     * overwritten by children classes
     */
    protected void connectionChanged() {

    }

    /**
     * Provides a starting point for creating an AlertDialog
     *
     * @param title Title of the AlertDialog
     * @return Builder which can be used to create an AlertDialog
     */
    protected AlertDialog.Builder setupDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View header = inflater.inflate(R.layout.alertdialog_custom_title, null);

        TextView titleText = (TextView) header.findViewById(R.id.dialog_title);
        titleText.setText(title);
        builder.setCustomTitle(header);

        return builder;
    }

    /**
     * Starts an AlertDialog which asks the user to reAuthenticate.
     *
     */
    protected void authenticateDialog(String type) {
        reAuthType = type;
        AlertDialog.Builder builder = setupDialog(getString(R.string.reauth_title));

        final View content = inflater.inflate(R.layout.alertdialog_body_authenticate, null);
        final EditText password = (EditText) content.findViewById(R.id.password);
        final EditText email = (EditText) content.findViewById(R.id.loginEmail);
        builder.setView(content);

        builder.setPositiveButton(R.string.confirm_clear_bin_dialog, null);

        builder.setNegativeButton(R.string.cancel_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        final AlertDialog dialog = builder.create();
        reAuthDialog = dialog;

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final Button button = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pw = password.getText().toString();
                        String em = email.getText().toString();

                        if (pw.equals("") && em.equals("")) {
                            Toast.makeText(BaseActivity.this, R.string.password_not_equal_empty, Toast.LENGTH_SHORT).show();
                        } else {
                            button.setText(R.string.login_loading);
                            button.setClickable(false);

                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(em, pw);

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reAuthenticated(reAuthType);
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(BaseActivity.this, R.string.reauth_failed, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });

                final LinearLayout googleButton = (LinearLayout) content.findViewById(R.id.google_sign_in);
                googleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        googleSignIn();
                    }
                });
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                }
                return false;
            }
        });


        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        }

        dialog.show();
    }


    /**
     * Sign in with Google and start Google Activity
     */
    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, googleSignInRequestCode);
    }

    /**
     *Handles results from started Activities. In our case it's the Google Sign In Activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from Google Sign In Intent
        if (requestCode == googleSignInRequestCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     * Handle the result from the Google Sign In
     *
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            Toast.makeText(BaseActivity.this, R.string.reauth_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Connect signed in Google User with a Firebase user
     *
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    reAuthenticated(reAuthType);
                    reAuthDialog.dismiss();
                } else {
                    Toast.makeText(BaseActivity.this, R.string.reauth_failed, Toast.LENGTH_SHORT).show();
                    reAuthDialog.dismiss();
                }
            }
        });
    }

    /**
     * Google Connection Failed Listener
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.google_auth_failed, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called after user successfully reAuthenticated
     *
     */
    protected void reAuthenticated(String type) {
        Toast.makeText(this, R.string.reauth_success, Toast.LENGTH_SHORT).show();
    }
}
