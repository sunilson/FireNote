package com.sunilson.firenote.presentation.authentication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sunilson.firenote.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button confirm;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        confirm = (Button) findViewById(R.id.confirmResetPassword);
        email = (EditText) findViewById(R.id.resetPasswordEmail);

        mAuth = FirebaseAuth.getInstance();

        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    confirm.performClick();
                }
                return false;
            }
        });

        //Send password request
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText() != null && !email.getText().toString().isEmpty()) {
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(ResetPasswordActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                finish();
                                Toast.makeText(ResetPasswordActivity.this, R.string.reset_success, Toast.LENGTH_LONG).show();
                            } else {
                                finish();
                                Toast.makeText(ResetPasswordActivity.this, R.string.reset_failed, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ResetPasswordActivity.this, R.string.error_register_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
