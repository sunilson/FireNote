package com.pro3.planner.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Scroller;

import com.google.firebase.database.DatabaseReference;
import com.pro3.planner.Interfaces.CanBeEdited;
import com.pro3.planner.R;
import com.pro3.planner.dialogs.EditElementDialog;

public class NoteActivity extends AppCompatActivity implements CanBeEdited {

    private EditText notePad;
    private String noteTitle;

    private DatabaseReference mElementReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        noteTitle = i.getStringExtra("elementTitle");
        setTitle(noteTitle);

        notePad = (EditText) findViewById(R.id.notepad);

        notePad.setScroller(new Scroller(this));
        notePad.setVerticalScrollBarEnabled(true);
        notePad.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.note_menu_edit) {
            DialogFragment dialog = EditElementDialog.newInstance(getResources().getString(R.string.edit_checklist_title), "note");
            dialog.show(getFragmentManager(), "dialog");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public DatabaseReference getElementReference() {
        return mElementReference;
    }
}
