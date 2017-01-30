package com.sunilson.firenote.Interfaces;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by linus_000 on 01.12.2016.
 */

public interface SettingsInterface {

    boolean getConnected();

    DatabaseReference getSettingsReference();

}
