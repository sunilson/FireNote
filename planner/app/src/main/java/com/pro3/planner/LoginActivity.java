package com.pro3.planner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

/**
 * @author Linus Weiss
 *
 * Klasse wickelt den Login über Firebase ab. Anbindung an Register Activity.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText loginEmail, loginPassword;
    private TextView loginRegisterText;
    private Button loginButton;
    private GoogleApiClient mGoogleApiClient;
    private int googleSignInRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginRegisterText = (TextView) findViewById(R.id.loginRegisterText);

        //Used to make part of the text blue
        loginRegisterText.setText(Html.fromHtml(getResources().getString(R.string.login_register_text)));

        //Login Submit Click Handler
        loginButton.setOnClickListener(this);
        loginRegisterText.setOnClickListener(this);

        //Initialize Firebase Auth Instance and the Auth Listener
        //Auth Listener is used to detect any change in the authentication state of the user
        //like logged in and logged out
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    finish();
                } else {
                    // User is signed out
                }
            }
        };

        //Google Sign In
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("376414129715-ehhkuv1f9acftujtvuk0r9biir5c98v2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Ende Google Sign IN
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

    private void signInEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //On success, listener is called

                if(!task.isSuccessful()) {
                    //Sign in failed
                    Toast.makeText(LoginActivity.this, "Sign in Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Sign in with Google and start Google Activity
    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, googleSignInRequestCode);
    }

    //Handles results from started Activities
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from Google Sign In Intent
        if (requestCode == googleSignInRequestCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    //Handle the result from the Google Sign In
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            Log.i("Linus", "Passt");
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            Log.i("Linus", "Passt ned");
            // Signed out, show unauthenticated UI.
        }
    }

    //Connect signed in Google User with Firebase
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton){
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            //Überprüfen von Username und Passwort
            //TODO: genauere Überprüfung und Stripping von unerwünschten Zeichen
            if(email != null && password != null) {
                signInEmail(email, password);
            }
        } else if (v.getId() == R.id.loginRegisterText) {
            //Start register activity
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.sign_in_button) {
            //Start the google sign in proccess
            googleSignIn();
        }
    }

    //Google Connection Failed Listener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
