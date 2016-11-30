package com.pro3.planner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.pro3.planner.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password, passwordAgain;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = (EditText) findViewById(R.id.registerEmail);
        password = (EditText) findViewById(R.id.registerPassword);
        passwordAgain = (EditText) findViewById(R.id.registerPasswordAgain);
        registerButton = (Button) findViewById(R.id.registerSubmitButton);
        registerButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in/registered successfully
                    user.sendEmailVerification();
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    // User is signed out
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        Log.i("Linus", "Stop " + this.getClass().getSimpleName());
    }

    /*
    ----------------------------
    ---- Register functions ----
    ----------------------------
     */

    private void register(String email, String password, String passwordAgain) {
        //TODO: Strip strings, Passwort Komplexität prüfen

        if (emailValid(email)) {
            if (passwordSecure(password)) {
                if (password.equals(passwordAgain)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            Toast.makeText(RegisterActivity.this, R.string.error_register_password_weak, Toast.LENGTH_SHORT).show();
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            Toast.makeText(RegisterActivity.this, R.string.error_register_invalid_email, Toast.LENGTH_SHORT).show();
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            Toast.makeText(RegisterActivity.this, R.string.error_register_user_already_exists, Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e("RegisterError", e.getMessage());
                                        }
                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, R.string.error_password_match, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.error_password_security, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.error_register_invalid_characters, Toast.LENGTH_LONG).show();
        }


    }

    /**
     * Check password for security criteria
     *
     * @param input
     * @return
     */
    private boolean passwordSecure(String input) {
        if (input.length() < 8) {
            return false;
        }

        //Check for security criteria
        String hasUpperCase = ".*[A-Z].*";
        String hasLowerCase = ".*[a-z].*";
        String hasNumbers = ".*\\d.*";

        if (input.matches(hasUpperCase) && input.matches(hasLowerCase) && input.matches(hasNumbers)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if only allowed characters are in the email string. Only for user comfort. More complex test is done by Firebase itself
     *
     * @param input
     * @return
     */
    private boolean emailValid(String input) {
        String pattern = "^[a-zA-Z0-9.@-]*$";
        return input.matches(pattern);
    }

    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerSubmitButton) {
            String sEmail = email.getText().toString();
            String sPassword = password.getText().toString();
            String sPasswordAgain = passwordAgain.getText().toString();

            //Check if a field is empty
            if (sEmail.length() > 0 && sPassword.length() > 0 && sPasswordAgain.length() > 0) {
                register(sEmail, sPassword, sPasswordAgain);
            } else {
                Toast.makeText(this, R.string.error_register_empty, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
