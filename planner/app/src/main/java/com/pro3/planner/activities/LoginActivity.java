package com.pro3.planner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro3.planner.R;
import com.pro3.planner.baseClasses.ChecklistElement;
import com.pro3.planner.baseClasses.Element;

/**
 * @author Linus Weiss
 *
 * Klasse wickelt den Login über Firebase ab. Anbindung an Register Activity.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText loginEmail, loginPassword;
    private TextView forgotPassword;
    private Button loginButton, googleButton;
    private GoogleApiClient mGoogleApiClient;
    private int googleSignInRequestCode = 1;
    private FirebaseUser user;
    private DatabaseReference mReference;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        googleButton = (Button) findViewById(R.id.google_sign_in);
        forgotPassword = (TextView) findViewById(R.id.forgotPasswordLink);

        loginEmail.setText(LoginActivity.this.getSharedPreferences("general", MODE_PRIVATE).getString("lastEmail", ""));

        //Login Submit Click Handler
        loginButton.setOnClickListener(this);

        loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    loginButton.performClick();
                }
                return false;
            }
        });

        //Initialize Firebase Auth Instance and the Auth Listener
        //Auth Listener is used to detect any change in the authentication state of the user
        //like logged in and logged out
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();

        //Google Sign In
        googleButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("376414129715-ehhkuv1f9acftujtvuk0r9biir5c98v2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Ende Google Sign IN

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Listener zu Instanz hinzufügen
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Listener von Instanz entfernen
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    private void initializeAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Check if user has verified his email
                    if(user.isEmailVerified()) {
                        //User is signed in and verified. Continue
                        SharedPreferences.Editor editor = LoginActivity.this.getSharedPreferences("general", MODE_PRIVATE).edit();
                        editor.putString("lastEmail", loginEmail.getText().toString());
                        editor.commit();
                        mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("settings").child("registered");
                        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Boolean registered = dataSnapshot.getValue(Boolean.class);

                                if (registered == null) {
                                    loadDefaultData(user);
                                } else {
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                googleButton.setText(getString(R.string.google_sign_in));
                                loginButton.setText(getString(R.string.login_button));
                            }
                        });
                    } else {
                        //If not verified, sign user out and switch to login activity
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), R.string.verification_error, Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                } else {
                    googleButton.setText(getString(R.string.google_sign_in));
                    loginButton.setText(getString(R.string.login_button));
                    //User is signed out. Do nothing
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton){
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            //Überprüfen von Username und Passwort
            //TODO: genauere Überprüfung und Stripping von unerwünschten Zeichen
            if(email.length() != 0 && password.length() != 0) {
                signInEmail(email, password);
                loginButton.setText(getResources().getString(R.string.login_loading));
            } else {
                Toast.makeText(LoginActivity.this, R.string.error_register_empty, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.google_sign_in) {
            //Start the google sign in proccess
            googleSignIn();
        }
    }

    /**
     * Google Connection Failed Listener
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*
    ---------------------------
    ---- sign in functions ----
    ---------------------------
     */

    private void signInEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //On success, listener is called
                if(!task.isSuccessful()) {
                    //Sign in failed
                    Toast.makeText(LoginActivity.this, "Sign in Failed", Toast.LENGTH_LONG).show();
                    loginButton.setText(getResources().getString(R.string.login_button));
                }
            }
        });
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
        Log.d("Google", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            googleButton.setText(getString(R.string.login_loading));
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    /**
     * Connect signed in Google User with a Firebase user
     *
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //Sign in Failed
                        }
                    }
                });
    }

    private void loadDefaultData(final FirebaseUser user) {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        //Add Elements
        Element firstChecklist = new Element("checklist", getString(R.string.example_checklist));
        Element firstNote = new Element("note", getString(R.string.example_note));
        Element firstBundle = new Element("bundle", getString(R.string.example_bundle));

        DatabaseReference dRef = mReference.child("elements").child("main").push();
        firstChecklist.setElementID(dRef.getKey());
        firstChecklist.setCategoryID("general");
        firstChecklist.setColor(ContextCompat.getColor(this, R.color.note_color_1));
        firstChecklist.setCategoryName(getString(R.string.category_general));
        dRef.setValue(firstChecklist);

        dRef = mReference.child("elements").child("main").push();
        firstNote.setElementID(dRef.getKey());
        firstNote.setCategoryID("general");
        firstNote.setColor(ContextCompat.getColor(this, R.color.note_color_4));
        firstNote.setCategoryName(getString(R.string.category_general));
        dRef.setValue(firstNote);

        dRef = mReference.child("elements").child("main").push();
        firstBundle.setElementID(dRef.getKey());
        firstBundle.setCategoryID("general");
        firstBundle.setColor(ContextCompat.getColor(this, R.color.note_color_7));
        firstBundle.setCategoryName(getString(R.string.category_general));
        dRef.setValue(firstBundle);

        //Add Checklist Elements
        ChecklistElement checklistElement = new ChecklistElement(getString(R.string.example_checklist_element));

        dRef = mReference.child("contents").child(firstChecklist.getElementID()).child("elements").push();
        checklistElement.setElementID(dRef.getKey());
        dRef.setValue(checklistElement);

        dRef = mReference.child("contents").child(firstChecklist.getElementID()).child("elements").push();
        checklistElement.setElementID(dRef.getKey());
        dRef.setValue(checklistElement);

        dRef = mReference.child("contents").child(firstChecklist.getElementID()).child("elements").push();
        checklistElement.setElementID(dRef.getKey());
        dRef.setValue(checklistElement);

        //Add Bundle Items
        Element bundleChecklist = new Element("checklist", getString(R.string.example_checklist));
        Element bundleNote = new Element("note", getString(R.string.example_note));

        dRef = mReference.child("elements").child("bundles").child(firstBundle.getElementID()).push();
        bundleChecklist.setElementID(dRef.getKey());
        bundleChecklist.setCategoryID("general");
        bundleChecklist.setColor(ContextCompat.getColor(this, R.color.note_color_2));
        bundleChecklist.setCategoryName(getString(R.string.category_general));
        dRef.setValue(bundleChecklist);

        dRef = mReference.child("elements").child("bundles").child(firstBundle.getElementID()).push();
        bundleNote.setElementID(dRef.getKey());
        bundleNote.setCategoryID("general");
        bundleNote.setColor(ContextCompat.getColor(this, R.color.note_color_6));
        bundleNote.setCategoryName(getString(R.string.category_general));
        dRef.setValue(bundleNote);

        //Set Registered
        mReference.child("settings").child("registered").setValue(true);

        //Add Note Content
        mReference.child("contents").child(firstNote.getElementID()).child("text").setValue(getString(R.string.example_note_text)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
