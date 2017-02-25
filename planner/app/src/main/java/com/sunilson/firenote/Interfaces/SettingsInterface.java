package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;

/**
 * @author Linus Weiss
 */

public interface SettingsInterface {

    boolean getConnected();

    DatabaseReference getSettingsReference();

}
