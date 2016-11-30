package com.pro3.planner.baseClasses;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linus_000 on 02.11.2016.
 */

public class Checklist extends Element{

    private List elements;
    private Reminder reminder;

    public Checklist(String noteType, String title, Context context) {
        super(noteType, title, context);
        elements = new ArrayList();
    }

    public void addChecklistElement(String text) {
    }

}
