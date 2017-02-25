package com.sunilson.firenote.Interfaces;

import android.os.Bundle;

/**
 * @author Linus Weiss
 */

/**
 * Implemented by all Activities that open a Confirm Dialog
 */
public interface ConfirmDialogResult {
    public void confirmDialogResult(boolean bool, String type, Bundle args);
}
