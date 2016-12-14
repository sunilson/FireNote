package com.pro3.planner.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro3.planner.Interfaces.SettingsInterface;
import com.pro3.planner.R;
import com.pro3.planner.dialogs.MasterPasswordDialog;

public class SettingsActivity extends BaseActivity implements SettingsInterface{

    private LinearLayout masterPassword;
    private DatabaseReference mReference;
    private FirebaseUser user;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().goOnline();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().goOffline();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        masterPassword = (LinearLayout) findViewById(R.id.master_password);
        initializeMasterPasswordClickListener();

        //Initialize the Firebase Auth System and the User
        user = mAuth.getCurrentUser();

        //Get the users Database Reference, if user exists
        if (user != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        }
    }

    private void initializeMasterPasswordClickListener() {
        masterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    DialogFragment dialogFragment = MasterPasswordDialog.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                }else {
                    Toast.makeText(SettingsActivity.this, R.string.need_connection, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean getConnected() {
        return connected;
    }

    @Override
    public DatabaseReference getSettingsReference() {
        return mSettingsReference;
    }
}
