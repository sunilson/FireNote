package com.sunilson.firenote.presentation.elements.bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sunilson.firenote.Interfaces.BundleInterface;
import com.sunilson.firenote.Interfaces.ConfirmDialogResult;
import com.sunilson.firenote.R;
import com.sunilson.firenote.presentation.bin.BinActivity;
import com.sunilson.firenote.presentation.elements.checklist.ChecklistActivity;
import com.sunilson.firenote.presentation.shared.adapters.elementList.ElementRecyclerAdapter;
import com.sunilson.firenote.presentation.addElementDialog.AddElementDialog;
import com.sunilson.firenote.presentation.dialogs.ListAlertDialog;
import com.sunilson.firenote.presentation.dialogs.PasswordDialog;
import com.sunilson.firenote.presentation.shared.base.element.activities.ElementActivity;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

import static com.sunilson.firenote.R.id.swipeContainerBundle;

public class BundleActivity extends ElementActivity implements BundleInterface, ConfirmDialogResult {

    private RecyclerView bundleList;
    private DatabaseReference mElementsReference, mBinReference;
    private ChildEventListener mElementsListener;
    private ElementRecyclerAdapter bundleRecyclerAdapter;
    private View.OnClickListener recycleOnClickListener;
    private CoordinatorLayout coordinatorLayout;
    private View.OnLongClickListener recycleOnLongClickListener;
    private boolean started, deletedElement;
    private LinearLayoutManager linearLayoutManager;
    private String restoredElement = "";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_bundle);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(swipeContainerBundle);

        if (mElementReference != null) {
            //Recyclerview Initialization
            bundleList = (RecyclerView) findViewById(R.id.bundleList);
            bundleList.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(this);
            bundleList.setLayoutManager(linearLayoutManager);
            initializeRecyclerOnClickListener();
            initializeRecyclerOnLongClickListener();

            bundleRecyclerAdapter = new ElementRecyclerAdapter(this, recycleOnClickListener, recycleOnLongClickListener, bundleList);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallbackMain(bundleRecyclerAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(bundleList);
            AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(bundleRecyclerAdapter);
            alphaInAnimationAdapter.setFirstOnly(false);
            alphaInAnimationAdapter.setDuration(200);
            bundleList.setAdapter(alphaInAnimationAdapter);
            bundleList.setItemAnimator(new ScaleInAnimator(new OvershootInterpolator(1f)));
            bundleList.getItemAnimator().setAddDuration(400);
            initializeElementsListener();

            mElementsReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("elements").child("bundles").child(elementID);
            mBinReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("bin").child("bundles").child(elementID);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!((BaseApplication) getApplicationContext()).getInternetConnected()) {
                        Toast.makeText(BundleActivity.this, R.string.listener_no_connection, Toast.LENGTH_LONG).show();
                    }
                    mElementsReference.removeEventListener(mElementsListener);
                    bundleRecyclerAdapter.clear();
                    mElementsReference.addChildEventListener(mElementsListener);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.add_Element_Title), "addElementBundle", null, null);
                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!started) {
            started = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mElementsReference.addChildEventListener(mElementsListener);
                }
            }, 200);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bundleRecyclerAdapter.clear();

        if (mElementsListener != null) {
            mElementsReference.removeEventListener(mElementsListener);
        }
    }

    /**
     * Firebase Listener on elements reference
     */
    private void initializeElementsListener() {
        mElementsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Add new Element to Bundle if valid
                Element element = dataSnapshot.getValue(Element.class);
                element.setElementID(dataSnapshot.getKey());
                if (element.getNoteType() != null) {
                    int position = bundleRecyclerAdapter.add(element);
                    if (element.getElementID().equals(restoredElement)) {
                        bundleList.smoothScrollToPosition(position);
                        restoredElement = "";
                    }
                } else {
                    //not valid --> Delete
                    mElementsReference.child(dataSnapshot.getKey()).removeValue();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Element element = dataSnapshot.getValue(Element.class);
                element.setElementID(dataSnapshot.getKey());
                if (element.getNoteType() != null) {
                    bundleRecyclerAdapter.update(element, element.getElementID());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final Element element = dataSnapshot.getValue(Element.class);
                element.setElementID(dataSnapshot.getKey());
                if (element.getNoteType() != null) {
                    element.setElementID(dataSnapshot.getKey());
                    bundleRecyclerAdapter.remove(element.getElementID());
                    mBinReference.child(element.getElementID()).setValue(element);

                    if (deletedElement) {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, R.string.moved_to_bin, 4000)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, R.string.element_restored, Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                        mBinReference.child(element.getElementID()).removeValue();
                                        mElementsReference.child(element.getElementID()).setValue(element);
                                        restoredElement = element.getElementID();
                                    }
                                });

                        snackbar.show();
                    }
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

    /**
     * Open element on click
     */
    private void initializeRecyclerOnClickListener() {
        recycleOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieve element at position of click from list adapter
                int itemPosition = bundleList.getChildLayoutPosition(v);
                Element element = bundleRecyclerAdapter.getItem(itemPosition);

                //If element is locked, ask for password
                if (element.getLocked()) {
                    DialogFragment dialogFragment = PasswordDialog.newInstance("passwordOpenElement", element.getNoteType(), element.getElementID(), element.getTitle(), element.getColor());
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                } else {
                    //Start the correct activity
                    Intent i = null;
                    switch (element.getNoteType()) {
                        case "checklist":
                            i = new Intent(BundleActivity.this, ChecklistActivity.class);
                            break;
                        case "note":
                            i = new Intent(BundleActivity.this, NoteActivity.class);
                            break;
                        case "bundle":
                            i = new Intent(BundleActivity.this, BundleActivity.class);
                            break;
                    }

                    //Put element values into intent and then start it
                    if (i != null) {
                        i.putExtra("elementID", element.getElementID());
                        i.putExtra("elementTitle", element.getTitle());
                        i.putExtra("elementColor", element.getColor());
                        i.putExtra("elementType", element.getNoteType());
                        i.putExtra("parentID", elementID);
                        i.putExtra("categoryID", element.getCategoryID());
                        startActivity(i);
                    }
                }
            }
        };
    }

    /**
     * Display edit dialog on long click
     */
    private void initializeRecyclerOnLongClickListener() {
        recycleOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int itemPosition = bundleList.getChildLayoutPosition(v);
                Element element = bundleRecyclerAdapter.getItem(itemPosition);

                if (element.getLocked()) {
                    DialogFragment dialogFragment = PasswordDialog.newInstance("passwordEditElement", "", "", "", itemPosition);
                    dialogFragment.show(getSupportFragmentManager(), "dialog");
                } else {
                    DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.edit_element_title), "editElement", element.getElementID(), element.getNoteType());
                    dialog.show(getSupportFragmentManager(), "dialog");
                }
                return true;
            }
        };
    }

    @Override
    public DatabaseReference getElementsReference() {
        return mElementsReference;
    }

    @Override
    public ElementRecyclerAdapter getElementAdapter() {
        return bundleRecyclerAdapter;
    }

    @Override
    public TextView getSortTextView() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bundle, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.bundle_menu_bin) {
            Intent i = new Intent(BundleActivity.this, BinActivity.class);
            i.putExtra("elementID", elementID);
            i.putExtra("elementName", elementTitle);
            startActivity(i);
        } else if (id == R.id.menu_reminder) {
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setData(CalendarContract.Events.CONTENT_URI);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE, elementTitle + " - " + getString(R.string.app_name));
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, getString(R.string.reminder) + elementType + "!");
            startActivityForResult(calIntent, 123);
        } else if (id == R.id.menu_share) {

            String shareBody = getString(R.string.element_bundle) + " \"" + elementTitle + "\" " + getString(R.string.from_app) + ": " + "\n" + bundleRecyclerAdapter.toString();

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, elementTitle);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, ""));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void confirmDialogResult(boolean bool, String type, Bundle args) {
        super.confirmDialogResult(bool, type, args);

        switch (type) {
            case "passwordOpenElement":
                if (bool) {
                    Intent i = null;
                    if (args.getString("elementType").equals("checklist")) {
                        i = new Intent(BundleActivity.this, ChecklistActivity.class);
                    } else if (args.getString("elementType").equals("note")) {
                        i = new Intent(BundleActivity.this, NoteActivity.class);
                    }

                    if (i != null) {
                        i.putExtra("elementID", args.getString("elementID"));
                        i.putExtra("elementTitle", args.getString("elementTitle"));
                        i.putExtra("elementColor", args.getInt("elementColor"));
                        i.putExtra("elementType", args.getString("elementType"));
                        i.putExtra("parentID", elementID);
                        i.putExtra("categoryID", categoryID);
                        startActivity(i);
                    }

                } else {
                    Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                }
                break;
            case "passwordEditElement":
                if (bool) {
                    DialogFragment dialog = ListAlertDialog.newInstance(getResources().getString(R.string.edit_element_title), "editElement", null, null);
                    dialog.show(getSupportFragmentManager(), "dialog");
                } else {
                    Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
                }
                break;
            case "delete":
                if (bool) {
                    if (mElementsListener != null) {
                        mElementsReference.removeEventListener(mElementsListener);
                    }
                    mElementReference.removeValue();
                    finish();
                }
                break;
            case "addElement":
                if (bool) {
                    addEditDialog = AddElementDialog.newInstance(getString(R.string.add_Element_Title), args.getString("elementType"));
                    addEditDialog.show(getSupportFragmentManager(), "dialog");
                }
                break;
        }
    }

    @Override
    protected void showTutorial() {
        ShowcaseView.Builder showCaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(new ViewTarget(R.id.fab, this))
                .setContentTitle(getString(R.string.welcome_to_bundle))
                .setContentText(getString(R.string.tutorial_add_bundle_element))
                .hideOnTouchOutside()
                .setStyle(R.style.ShowCaseViewStyle)
                .singleShot(3)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        findViewById(R.id.fab).setEnabled(true);
                        if (findViewById(R.id.elementList_icon_holder) != null) {
                            new ShowcaseView.Builder(BundleActivity.this)
                                    .withMaterialShowcase()
                                    .setTarget(new ViewTarget(R.id.elementList_icon_holder, BundleActivity.this))
                                    .setContentTitle(getString(R.string.tutorial_bundle_element_title))
                                    .setContentText(getString(R.string.tutorial_bundle_element))
                                    .hideOnTouchOutside()
                                    .setStyle(R.style.ShowCaseViewStyle)
                                    .build();
                        }
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                        findViewById(R.id.fab).setEnabled(false);
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                });

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        showCaseView.build().setButtonPosition(lps);
    }

    @Override
    public void setDeletedElement(boolean value) {
        deletedElement = value;
    }
}
