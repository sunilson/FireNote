package com.pro3.planner.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro3.planner.BaseApplication;
import com.pro3.planner.Interfaces.ConfirmDialogResult;
import com.pro3.planner.Interfaces.MainActivityInterface;
import com.pro3.planner.ItemTouchHelper.SimpleItemTouchHelperCallbackMain;
import com.pro3.planner.LocalSettingsManager;
import com.pro3.planner.R;
import com.pro3.planner.adapters.CategoryAdapter;
import com.pro3.planner.adapters.ElementRecyclerAdapter;
import com.pro3.planner.adapters.SpinnerAdapter;
import com.pro3.planner.baseClasses.Category;
import com.pro3.planner.baseClasses.Checklist;
import com.pro3.planner.baseClasses.Element;
import com.pro3.planner.dialogs.ListAlertDialog;
import com.pro3.planner.dialogs.PasswordDialog;
import com.pro3.planner.dialogs.VisibilityDialog;

public class MainActivity extends BaseActivity implements MainActivityInterface, ConfirmDialogResult {

    private DatabaseReference mReference, mElementsReference, mCategoryReference, mBinReference;
    private ChildEventListener mChildEventListener, mCategoryListener;
    private ElementRecyclerAdapter elementRecyclerAdapter;
    private SpinnerAdapter spinnerCategoryAdapter;
    private CategoryAdapter listCategoryAdapter;
    private RecyclerView recyclerView;
    private View.OnClickListener recycleOnClickListener;
    private View.OnLongClickListener recycleOnLongClickListener;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseUser user;
    private SharedPreferences prefs;
    private TextView currentSortingMethod;

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

        //Set Main Context of BaseApplication, so we can use it in the other Activities
        ((BaseApplication)getApplicationContext()).mainContext = this;

        currentSortingMethod = (TextView) findViewById(R.id.current_sorting_method);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);

        //Initialize the Firebase Auth System and the User
        user = mAuth.getCurrentUser();

        //Get the users Database Reference, if user exists
        if(user != null) {
            mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            //Prefs initialization
            prefs = this.getSharedPreferences(user.getUid(), MODE_PRIVATE);

            if(mReference != null) {

                //Initialize the Listener which detects changes in the note data
                initializeElementsListener();

                //Register ChildEventListener here so it's not added every time we switch Activity
                mElementsReference = mReference.child("elements");
                mElementsReference.addChildEventListener(mChildEventListener);

                initializeCategoryListener();

                //Category Reference
                mCategoryReference = mReference.child("categories");
                mCategoryReference.addChildEventListener(mCategoryListener);

                //RecyclerView Initialization
                recyclerView = (RecyclerView) findViewById(R.id.elementList);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(linearLayoutManager);
                initializeRecyclerOnClickListener();
                initializeRecyclerOnLongClickListener();

                elementRecyclerAdapter = new ElementRecyclerAdapter(this, recycleOnClickListener, recycleOnLongClickListener);

                recyclerView.setAdapter(elementRecyclerAdapter);
                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallbackMain(elementRecyclerAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                itemTouchHelper.attachToRecyclerView(recyclerView);

                //Papierkorb Reference
                mBinReference = mReference.child("bin");

                currentSortingMethod.setText(getString(R.string.current_sorthing_method) + " " + LocalSettingsManager.getInstance().getSortingMethod());
                currentSortingMethod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.menu_sort), "sort", 0);
                        dialog.show(getSupportFragmentManager(), "dialog");
                    }
                });
            }
        }

        //The Button used to add a new Element
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.add_Element_Title), "addElement", 0);
                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mElementsReference != null) {
            mElementsReference.removeEventListener(mChildEventListener);
        }

        if (mCategoryReference != null) {
            mCategoryReference.removeEventListener(mCategoryListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
            DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.menu_sort), "sort", 0);
            dialog.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.main_element_sort) {
            DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.menu_sort), "sort", 0);
            dialog.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.main_element_visibility) {
            VisibilityDialog visibilityDialog = new VisibilityDialog();
            visibilityDialog.show(getSupportFragmentManager(), "dialog");
        } else if (id == R.id.action_delete_all) {
            if (mReference != null) {
                mReference.child("elements").removeValue();
            }
        } else if (id == R.id.action_bin) {
            Intent i = new Intent(MainActivity.this, BinActivity.class);
            startActivity(i);
        } else if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    /*
    -----------------------------
    --- Listener Initializing ---
    -----------------------------
     */

    private void initializeRecyclerOnClickListener() {
        recycleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                Element element = elementRecyclerAdapter.getItem(itemPosition);
                if (element.getLocked()) {
                    DialogFragment dialogFragment = PasswordDialog.newInstance("passwordOpenElement", element.getNoteType(), element.getNoteID(), element.getTitle(), element.getColor());
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                } else {
                    Intent i = null;
                    if (element.getNoteType().equals("checklist")) {
                        i = new Intent(MainActivity.this, ChecklistActivity.class);
                    } else if (element.getNoteType().equals("note")) {
                        i = new Intent(MainActivity.this, NoteActivity.class);
                    } else if (element.getNoteType().equals("bundle")) {
                        i = new Intent(MainActivity.this, BundleActivity.class);
                    }

                    i.putExtra("elementID", element.getNoteID());
                    i.putExtra("elementTitle", element.getTitle());
                    i.putExtra("elementColor", element.getColor());
                    i.putExtra("elementType", element.getNoteType());
                    startActivity(i);
                }
            }
        };
    }

    private void initializeRecyclerOnLongClickListener() {
        recycleOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                Element element = elementRecyclerAdapter.getItem(itemPosition);

                if (element.getLocked()) {
                    DialogFragment dialogFragment = PasswordDialog.newInstance("passwordEditElement", "", "", "", itemPosition);
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                } else {
                    DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.edit_element_title), "editElement", itemPosition);
                    dialog.show(getSupportFragmentManager(), "dialog");
                }
                return true;
            }
        };
    }

    private void initializeElementsListener() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                elementRecyclerAdapter.add(element);
                elementRecyclerAdapter.hideElements();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                elementRecyclerAdapter.update(element, element.getNoteID());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final DataSnapshot dataSnapshotValue = dataSnapshot;
                final Element element = dataSnapshot.getValue(Element.class);
                elementRecyclerAdapter.remove(element.getNoteID());
                final DatabaseReference dRef = mBinReference.push();
                if(element.getNoteType().equals("checklist")) {
                    Checklist checklist = dataSnapshot.getValue(Checklist.class);
                    checklist.setNoteID(dRef.getKey());
                    dRef.setValue(checklist);
                } else if (element.getNoteType().equals("bundle")) {
                    com.pro3.planner.baseClasses.Bundle bundle = dataSnapshot.getValue(com.pro3.planner.baseClasses.Bundle.class);
                    bundle.setNoteID(dRef.getKey());
                    dRef.setValue(bundle);
                } else {
                    element.setNoteID(dRef.getKey());
                    dRef.setValue(element);
                }

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, R.string.moved_to_bin, 6000)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(coordinatorLayout, R.string.element_restored, Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                                mBinReference.child(dRef.getKey()).removeValue();
                                Element element = dataSnapshotValue.getValue(Element.class);
                                DatabaseReference dRef = mElementsReference.push();
                                if(element.getNoteType().equals("checklist")) {
                                    Checklist checklist = dataSnapshotValue.getValue(Checklist.class);
                                    checklist.setNoteID(dRef.getKey());
                                    dRef.setValue(checklist);
                                } else if (element.getNoteType().equals("bundle")) {
                                    com.pro3.planner.baseClasses.Bundle bundle = dataSnapshotValue.getValue(com.pro3.planner.baseClasses.Bundle.class);
                                    bundle.setNoteID(dRef.getKey());
                                    dRef.setValue(bundle);
                                } else {
                                    element.setNoteID(dRef.getKey());
                                    dRef.setValue(element);
                                }
                            }
                        });

                snackbar.show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void initializeCategoryListener() {
        LocalSettingsManager.getInstance();
        LocalSettingsManager.getInstance().setPrefs(prefs);

        spinnerCategoryAdapter = new SpinnerAdapter(this, R.layout.spinner_item);
        spinnerCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listCategoryAdapter = new CategoryAdapter(this, R.layout.category_list_layout);

        mCategoryListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Category category = dataSnapshot.getValue(Category.class);
                spinnerCategoryAdapter.add(category);
                listCategoryAdapter.add(category);
                LocalSettingsManager.getInstance().addCategory(category.getCategoryName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Category category = dataSnapshot.getValue(Category.class);
                spinnerCategoryAdapter.update(category);
                listCategoryAdapter.update(category);
                LocalSettingsManager.getInstance().addCategory(category.getCategoryName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Category category = dataSnapshot.getValue(Category.class);
                spinnerCategoryAdapter.remove(dataSnapshot.getKey());
                listCategoryAdapter.remove(dataSnapshot.getKey());
                LocalSettingsManager.getInstance().removeCategory(category.getCategoryName());
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
    ---------------------------
    ---- Interface methods ----
    ---------------------------
     */

    @Override
    public ElementRecyclerAdapter getElementAdapter() {
        return elementRecyclerAdapter;
    }

    @Override
    public TextView getSortTextView() {
        return currentSortingMethod;
    }

    @Override
    public DatabaseReference getElementsReference() {
        return mElementsReference;
    }

    @Override
    public ArrayAdapter<CharSequence> getSpinnerCategoryAdapter() {
        return spinnerCategoryAdapter;
    }

    @Override
    public ArrayAdapter<Category> getListCategoryAdapter() {
        return listCategoryAdapter;
    }

    @Override
    public DatabaseReference getCategoryReference() {
        return mCategoryReference;
    }

    @Override
    public DatabaseReference getBinReference() {
        return mBinReference;
    }

    @Override
    public void confirmDialogResult(boolean bool, String type, Bundle args) {
        if (type.equals("passwordOpenElement")) {
            if (bool) {
                Intent i = null;
                if (args.getString("elementType").equals("checklist")) {
                    i = new Intent(MainActivity.this, ChecklistActivity.class);
                } else if (args.getString("elementType").equals("note")) {
                    i = new Intent(MainActivity.this, NoteActivity.class);
                } else if (args.getString("elementType").equals("bundle")) {
                    i = new Intent(MainActivity.this, BundleActivity.class);
                }

                i.putExtra("elementID", args.getString("elementID"));
                i.putExtra("elementTitle", args.getString("elementTitle"));
                i.putExtra("elementColor", args.getInt("elementColor"));
                i.putExtra("elementType", args.getString("elementType"));
                startActivity(i);
            } else {
                Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
            }
        } else if (type.equals("passwordEditElement")) {
            if(bool) {
                DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.edit_element_title), "editElement", args.getInt("elementColor"));
                dialog.show(getSupportFragmentManager(), "dialog");
            } else {
                Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
