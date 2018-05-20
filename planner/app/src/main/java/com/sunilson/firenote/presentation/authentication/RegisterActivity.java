//package com.sunilson.firenote.presentation.authentication;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
//import com.google.firebase.auth.FirebaseAuthUserCollisionException;
//import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.sunilson.firenote.R;
//
//public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private EditText email, password, passwordAgain;
//    private Button registerButton;
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//
//    /*
//    ------------------------
//    ---- Android Events ----
//    ------------------------
//     */
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_register);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        email = (EditText) findViewById(R.id.registerEmail);
//        password = (EditText) findViewById(R.id.registerPassword);
//        passwordAgain = (EditText) findViewById(R.id.registerPasswordAgain);
//        registerButton = (Button) findViewById(R.id.registerSubmitButton);
//        registerButton.setOnClickListener(this);
//
//        passwordAgain.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
//                    registerButton.performClick();
//                }
//                return false;
//            }
//        });
//
//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    //User is signed in/registered successfully
//                    loadDefaultData(user);
//                } else {
//                    // User is signed out
//                }
//            }
//        };
//    }
//
//    private void loadDefaultData(final FirebaseUser user) {
//        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
//
//        //Add Elements
//        Element firstChecklist = new Element("checklist", getString(R.string.example_checklist));
//        Element firstNote = new Element("note", getString(R.string.example_note));
//        Element firstBundle = new Element("bundle", getString(R.string.example_bundle));
//
//        DatabaseReference dRef = mReference.child("elements").child("main").push();
//        firstChecklist.setElementID(dRef.getKey());
//        firstChecklist.setCategoryID("general");
//        firstChecklist.setColor(ContextCompat.getColor(this, R.color.note_color_1));
//        firstChecklist.setCategoryName(getString(R.string.category_general));
//        dRef.setValue(firstChecklist);
//
//        dRef = mReference.child("elements").child("main").push();
//        firstNote.setElementID(dRef.getKey());
//        firstNote.setCategoryID("general");
//        firstNote.setColor(ContextCompat.getColor(this, R.color.note_color_4));
//        firstNote.setCategoryName(getString(R.string.category_general));
//        dRef.setValue(firstNote);
//
//        dRef = mReference.child("elements").child("main").push();
//        firstBundle.setElementID(dRef.getKey());
//        firstBundle.setCategoryID("general");
//        firstBundle.setColor(ContextCompat.getColor(this, R.color.note_color_7));
//        firstBundle.setCategoryName(getString(R.string.category_general));
//        dRef.setValue(firstBundle);
//
//        //Add Checklist Elements
//        ChecklistElement checklistElement = new ChecklistElement(getString(R.string.example_checklist_element));
//
//        dRef = mReference.child("contents").child(firstChecklist.getElementID()).child("elements").push();
//        checklistElement.setElementID(dRef.getKey());
//        dRef.setValue(checklistElement);
//
//        dRef = mReference.child("contents").child(firstChecklist.getElementID()).child("elements").push();
//        checklistElement.setElementID(dRef.getKey());
//        dRef.setValue(checklistElement);
//
//        dRef = mReference.child("contents").child(firstChecklist.getElementID()).child("elements").push();
//        checklistElement.setElementID(dRef.getKey());
//        dRef.setValue(checklistElement);
//
//        //Add Bundle Items
//        Element bundleChecklist = new Element("checklist", getString(R.string.example_checklist));
//        Element bundleNote = new Element("note", getString(R.string.example_note));
//
//        dRef = mReference.child("elements").child("bundles").child(firstBundle.getElementID()).push();
//        bundleChecklist.setElementID(dRef.getKey());
//        bundleChecklist.setCategoryID("general");
//        bundleChecklist.setColor(ContextCompat.getColor(this, R.color.note_color_2));
//        bundleChecklist.setCategoryName(getString(R.string.category_general));
//        dRef.setValue(bundleChecklist);
//
//        dRef = mReference.child("elements").child("bundles").child(firstBundle.getElementID()).push();
//        bundleNote.setElementID(dRef.getKey());
//        bundleNote.setCategoryID("general");
//        bundleNote.setColor(ContextCompat.getColor(this, R.color.note_color_6));
//        bundleNote.setCategoryName(getString(R.string.category_general));
//        dRef.setValue(bundleNote);
//
//        //Set Registered
//        mReference.child("settings").child("registered").setValue(true);
//
//        //Add Note Content
//        mReference.child("contents").child(firstNote.getElementID()).child("text").setValue(getString(R.string.example_note_text)).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                user.sendEmailVerification();
//                mAuth.signOut();
//                Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_LONG).show();
//                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(i);
//            }
//        });
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
//
//    /*
//    ----------------------------
//    ---- Register functions ----
//    ----------------------------
//     */
//
//    private void register(String email, String password, String passwordAgain) {
//        if (password.equals(passwordAgain)) {
//            mAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            // If sign in fails, display a message to the user. If sign in succeeds
//                            // the auth state listener will be notified and logic to handle the
//                            // signed in user can be handled in the listener.
//                            if (!task.isSuccessful()) {
//                                try {
//                                    throw task.getException();
//                                } catch (FirebaseAuthWeakPasswordException e) {
//                                    Toast.makeText(RegisterActivity.this, R.string.error_register_password_weak, Toast.LENGTH_SHORT).show();
//                                    registerButton.setText(getString(R.string.register_button));
//                                } catch (FirebaseAuthInvalidCredentialsException e) {
//                                    Toast.makeText(RegisterActivity.this, R.string.error_register_invalid_email, Toast.LENGTH_SHORT).show();
//                                    registerButton.setText(getString(R.string.register_button));
//                                } catch (FirebaseAuthUserCollisionException e) {
//                                    Toast.makeText(RegisterActivity.this, R.string.error_register_user_already_exists, Toast.LENGTH_SHORT).show();
//                                    registerButton.setText(getString(R.string.register_button));
//                                } catch (Exception e) {
//                                    Log.e("RegisterError", e.getMessage());
//                                    registerButton.setText(getString(R.string.register_button));
//                                }
//                            }
//                        }
//                    });
//        } else {
//            Toast.makeText(this, R.string.error_password_match, Toast.LENGTH_SHORT).show();
//            registerButton.setText(getString(R.string.register_button));
//        }
//    }
//
//
//    /*
//    -----------------------------
//    --- Listener Initializing ---
//    -----------------------------
//     */
//
//    @Override
//    public void onClick(View v) {
//        if (((BaseApplication) getApplicationContext()).getInternetConnected()) {
//            if (v.getId() == R.id.registerSubmitButton) {
//                registerButton.setText(getString(R.string.register_loading));
//                String sEmail = email.getText().toString();
//                String sPassword = password.getText().toString();
//                String sPasswordAgain = passwordAgain.getText().toString();
//
//                //Check if a field is empty
//                if (sEmail.length() > 0 && sPassword.length() > 0 && sPasswordAgain.length() > 0) {
//                    register(sEmail, sPassword, sPasswordAgain);
//                } else {
//                    Toast.makeText(this, R.string.error_register_empty, Toast.LENGTH_SHORT).show();
//                    registerButton.setText(getString(R.string.register_button));
//                }
//            }
//        } else {
//            Toast.makeText(this, R.string.need_connection, Toast.LENGTH_SHORT).show();
//        }
//    }
//}
