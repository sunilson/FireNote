package com.sunilson.firenote.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sunilson.firenote.BaseApplication;
import com.sunilson.firenote.Interfaces.ConfirmDialogResult;
import com.sunilson.firenote.Interfaces.MainActivityInterface;
import com.sunilson.firenote.Interfaces.SettingsInterface;
import com.sunilson.firenote.R;
import com.sunilson.firenote.dialogs.ConfirmDialog;
import com.sunilson.firenote.dialogs.MasterPasswordDialog;

public class SettingsActivity extends BaseActivity implements SettingsInterface, View.OnClickListener, ConfirmDialogResult {

    private LinearLayout masterPassword, about, changePassword, deleteAccount;
    private TextView loggedInAs;
    private DatabaseReference mReference;
    private FirebaseUser user;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        masterPassword = (LinearLayout) findViewById(R.id.master_password);
        about = (LinearLayout) findViewById(R.id.about);
        changePassword = (LinearLayout) findViewById(R.id.change_password);
        loggedInAs = (TextView) findViewById(R.id.username);
        deleteAccount = (LinearLayout) findViewById(R.id.delete_account);

        about.setOnClickListener(this);
        masterPassword.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);

        //Initialize the Firebase Auth System and the User
        user = mAuth.getCurrentUser();

        //Get the users Database Reference, if user exists
        if (user != null) {
            loggedInAs.setText(getString(R.string.logged_in_as) + " \"" + user.getEmail() + "\"");
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        }
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
        return connected && ((BaseApplication) getApplicationContext()).getInternetConnected();
    }

    @Override
    public DatabaseReference getSettingsReference() {
        return mSettingsReference;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.about:
                aboutDialog();
                break;
            case R.id.master_password:
                if (connected && ((BaseApplication) getApplicationContext()).getInternetConnected()) {
                    DialogFragment dialogFragment = MasterPasswordDialog.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.need_connection, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.change_password:
                if (connected && ((BaseApplication) getApplicationContext()).getInternetConnected()) {
                    authenticateDialog("change_password");
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.need_connection, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.delete_account:
                if (connected && ((BaseApplication) getApplicationContext()).getInternetConnected()) {
                    authenticateDialog("delete_account");
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.need_connection, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void aboutDialog() {
        AlertDialog.Builder builder = setupDialog(getString(R.string.about_title));

        View content = inflater.inflate(R.layout.alertdialog_body_about, null);
        builder.setView(content);
        builder.setPositiveButton(R.string.confirm_add_dialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.show();
    }

    @Override
    protected void reAuthenticated(String type) {
        if (type.equals("change_password")) {
            changePasswordDialog();
        } else if (type.equals("delete_account")) {
            DialogFragment dialogFragment = ConfirmDialog.newInstance(getString(R.string.delete_account), getString(R.string.confirm_delete_account), "delete_account", null);
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        }
    }

    private void changePasswordDialog() {
        AlertDialog.Builder builder = setupDialog(getString(R.string.enter_password));

        View content = inflater.inflate(R.layout.alertdialog_body_change_password, null);
        final EditText newPassword = (EditText) content.findViewById(R.id.new_password);
        final EditText newPasswordAgain = (EditText) content.findViewById(R.id.new_password_again);

        builder.setView(content);
        builder.setPositiveButton(R.string.confirm_add_dialog, null);
        builder.setNegativeButton(R.string.cancel_add_dialog, null);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPW = newPassword.getText().toString();
                        String newPW2 = newPasswordAgain.getText().toString();

                        if (newPW.equals(newPW2) && !newPW.equals("")) {
                            user.updatePassword(newPW).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, R.string.password_changed, Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            Toast.makeText(SettingsActivity.this, R.string.error_register_password_weak, Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e("RegisterError", e.getMessage());
                                        }
                                    }
                                }
                            });
                            dialog.dismiss();
                        } else {
                            Toast.makeText(SettingsActivity.this, R.string.password_not_equal_empty, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    @Override
    public void confirmDialogResult(boolean bool, String type, Bundle args) {
        if (bool) {
            if (type.equals("delete_account")) {
                MainActivityInterface mainActivityInterface = (MainActivityInterface) ((BaseApplication) getApplicationContext()).mainContext;
                mainActivityInterface.removeListeners();
                mSettingsReference.child("deleted").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mReference.child("bin").removeValue();
                        mReference.child("elements").removeValue();
                        mReference.child("contents").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete();
                                Toast.makeText(SettingsActivity.this, R.string.account_deleted, Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        });
                    }
                });
            }
        }
    }
}
