package com.pro3.planner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity {

    private DatabaseReference mReference, mElementsReference, mSettingsReference;
    private ChildEventListener mChildEventListener, mSettingsListener;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ElementAdapter elementAdapter;
    private ListView listView;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SharedPreferences prefs;

    /*
    ------------------------
    ---- Android Events ----
    ------------------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        //Initialize the Firebase Auth System and the User
        mAuth = FirebaseAuth.getInstance();
        initializeAuthListener();
        user = mAuth.getCurrentUser();

        //Get the users Database Reference, if user exists
        if(user != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            initializeUserSettings();
        }

        //Set up the Adapter for the ListView Display
        elementAdapter = new ElementAdapter(this, R.layout.element_list_layout);
        listView = (ListView) findViewById(R.id.elementList);
        listView.setAdapter(elementAdapter);

        //Initialize all Click listeners for Listview
        setUpListView();

        //The Button used to add a new Element
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeAddNoteDialogue();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Add the authStateListener
        mAuth.addAuthStateListener(mAuthListener);

        //Initialize the Listener which detects changes in the note data
        initializeElementsListener();

        //Getting the Reference to the users elements
        //Adding Child Listener to the Database reference of the users elements
        if(mReference != null) {
            elementAdapter.clear();
            mElementsReference = mReference.child("elements");
            mElementsReference.addChildEventListener(mChildEventListener);
            mSettingsReference.addChildEventListener(mSettingsListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if(mChildEventListener != null) {
            mElementsReference.removeEventListener(mChildEventListener);
        }

        if(mSettingsListener != null) {
            mSettingsReference.removeEventListener(mSettingsListener);
        }
    }


    /*
    ----------------------
    ---- Options Menu ----
    ----------------------
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logOut) {
            mAuth.signOut();
            return true;
        } else if (id == R.id.action_sort) {
            mSettingsReference.child("mainElementSorting").setValue("dateAscending");
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    private void initializeElementsListener() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                elementAdapter.add(element);
                elementAdapter.sort(prefs.getString("mainElementSorting", "dateDescending"));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                elementAdapter.update(element, element.getNoteID());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Element element = dataSnapshot.getValue(Element.class);
                elementAdapter.remove(element.getNoteID());
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

    private void initializeSettingsListener() {
        mSettingsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(dataSnapshot.getKey(), value);
                editor.commit();

                if (dataSnapshot.getKey().equals("mainElementSorting")) {
                    elementAdapter.sort(value);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /*
    -----------------------------
    ---- Dialog Initializing ----
    -----------------------------
     */

    /**
     * Dialog used to let the user choose which type of Element he wants to add
     */
    private void initializeAddNoteDialogue() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle(R.string.add_Note_Title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getString(R.string.element_note));
        arrayAdapter.add(getString(R.string.element_checklist));

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get the user input
                        String strName = arrayAdapter.getItem(which);
                        Element element = null;
                        String defaultColor = prefs.getString("defaultColor", "empty");

                        //Initialize the correct element type
                        if(strName.equals(getString(R.string.element_checklist))) {
                            element = new Checklist(strName, getApplicationContext());
                        } else if (strName.equals(getString(R.string.element_note))){
                            element = new Note(strName, getApplicationContext());
                        }

                        element.setColor(defaultColor);

                        if(element != null) {
                            //Store the new element in the database
                            DatabaseReference dRef = mElementsReference.push();
                            element.setNoteID(dRef.getKey());
                            dRef.setValue(element, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError == null) {
                                        Toast.makeText(getApplicationContext(), R.string.element_sync_success, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
        builderSingle.show();
    }

    /**
     * Dialog used to edit Element on long click
     *
     * @param position
     */
    private void initializeEditElementDialog(final int position) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle(R.string.edit_Note_Title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.select_dialog_item);
        arrayAdapter.add("Delete");

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get the user input
                        String strName = arrayAdapter.getItem(which);

                        mElementsReference.child(((Element) elementAdapter.getItem(position)).getNoteID()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Toast.makeText(getApplicationContext(), "Successfully deleted and synced Element", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        builderSingle.show();
    }

    /*
    -------------------------------
    ---- Listview Initializing ----
    -------------------------------
     */

    /**
     * initializes the listeners for the listview
     */
    private void setUpListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ChecklistActivity.class);
                i.putExtra("elementID", ((Element) elementAdapter.getItem(position)).getNoteID());
                startActivity(i);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                initializeEditElementDialog(position);
                return true;
            }
        });
    }

    /*
    ---------------------------
    ---- User Initializing ----
    ---------------------------
     */

    /**
     * Adds the listener to the settings of the firebase database
     * Checks if all preferences are set and if not, replaces them with default values
     */
    private void initializeUserSettings() {
        initializeSettingsListener();
        prefs = this.getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        mSettingsReference = mReference.child("settings");
        mSettingsReference.addChildEventListener(mSettingsListener);


        //Default Color
        String defaultColor = prefs.getString("defaultColor", "empty");
        if (defaultColor == "empty") {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("defaultColor", "#FFFFA5");
            editor.commit();
            mSettingsReference.child("defaultColor").setValue("#FFFFA5");
        }

        //Main List Element sorting
        String mainElementSorting = prefs.getString("mainElementSorting", "empty");
        if(mainElementSorting == "empty") {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("mainElementSorting", "dateDescending");
            editor.commit();
            mSettingsReference.child("mainElementSorting").setValue("dateDescending");
        }
    }

    public FirebaseUser getUser() {
        return this.user;
    }
}
