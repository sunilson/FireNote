package com.sunilson.firenote;

import android.content.SharedPreferences;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Linus Weiss
 *
 */

/**
 * Used to store and retrieve data from the Shared Preferences of the Phone
 */
public class LocalSettingsManager {

    private String sortingMethod;
    private static LocalSettingsManager instance;
    private SharedPreferences prefs;

    /**
     * Creates new Instance or returns current one
     *
     * @return Current Instance of LocalSettingsManager
     */
    public static LocalSettingsManager getInstance() {
        if (instance == null) {
            instance = new LocalSettingsManager();
        }
        return instance;
    }

    /**
     * Initialize the access to the SharedPreferences of this Phone
     *
     * @param prefs
     */
    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }


    /**
     * Change visibility of a category in the SharedPreferences
     *
     * @param categoryID ID of the category that should be changed
     * @param visibility Visibility status: -1 = Invisible, 1 = Visible
     */
    public void setCategoryVisibility(String categoryID, int visibility) {
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(categoryID, visibility);
            editor.commit();
        }
    }

    /**
     * Retrieve current visibility of Category
     *
     * @param categoryID ID of the category
     * @return 1 if category should be visible or can't be found, -1 if category should be invisible
     */
    public int getCategoryVisibility(String categoryID) {
        if (prefs != null) {
            return prefs.getInt(categoryID, 1);
        } else {
            return 1;
        }
    }

    /**
     * Change visibility of a color in the SharedPreferences
     *
     * @param color ID of the color that should be changed
     * @param visibility Visibility status: -1 = Invisible, 1 = Visible
     */
    public void setColorVisibility(int color, int visibility) {
        if (prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Integer.toString(color), visibility);
            editor.commit();
        }
    }

    /**
     * Retrieve current visibility of color
     *
     * @param color ID of color
     * @return 1 if color should be visible or can't be found, -1 if color should be invisible
     */
    public int getColorVisibility(int color) {
        if (prefs != null) {
            return prefs.getInt(Integer.toString(color), 1);
        } else {
            return 1;
        }
    }

    /**
     * Set new active sorting method
     *
     * @param sortingMethod ShortName of sorting Method
     */
    public void setSortingMethod(String sortingMethod) {
        if (prefs != null) {
            SharedPreferences.Editor editor =  prefs.edit();
            editor.putString("mainElementSorting", sortingMethod);
            this.sortingMethod = sortingMethod;
            editor.commit();
        }
    }

    /**
     * Get currently selected sorting method
     *
     * @param sortingMethod Active sorting method
     */
    public String getSortingMethod() {
        if (prefs != null) {
            if (this.sortingMethod != null) {
                return this.sortingMethod;
            } else {
                return prefs.getString("mainElementSorting", null);
            }
        } else {
            return "";
        }
    }

    /**
     * Set current master password
     *
     * @param password SHA1 String of master password
     */
    public void setMasterPassword(String password){
        if (prefs != null) {
            SharedPreferences.Editor editor =  prefs.edit();
            editor.putString("masterPassword", password);
            editor.commit();
        }
    }

    /**
     * Get Current master password
     *
     * @return Master password or empty string if not set
     */
    public String getMasterPassword() {
        if (prefs != null) {
            return prefs.getString("masterPassword", "");
        } else {
            return "";
        }
    }

    /**
     * Encodes String to SHA1 Hash
     *
     * @param masterPasswordHash Unencoded String
     * @return Encoded String
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public String getSHA1Hash(String masterPasswordHash) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new String(Hex.encodeHex(DigestUtils.sha1(masterPasswordHash)));
    }
}
