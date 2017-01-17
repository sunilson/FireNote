package com.pro3.planner;

import android.content.SharedPreferences;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by linus_000 on 18.11.2016.
 */

public class LocalSettingsManager {

    private String sortingMethod;
    private static LocalSettingsManager instance;
    private SharedPreferences prefs;

    public static LocalSettingsManager getInstance() {
        if (instance == null) {
            instance = new LocalSettingsManager();
        }
        return instance;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void addCategory(String categoryName) {
        int categoryVisibility = prefs.getInt(categoryName, 1);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(categoryName, categoryVisibility);
        editor.commit();
    }

    public void setCategoryVisibility(String categoryID, int visibility) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(categoryID, visibility);
        editor.commit();
    }

    public int getCategoryVisibility(String categoryID) {
        return prefs.getInt(categoryID, 1);
    }

    public void removeCategory(String categoryID) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(categoryID);
        editor.apply();
    }

    public void setColorVisibility(int color, int visibility) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Integer.toString(color), visibility);
        editor.commit();
    }


    public int getColorVisibility(int color) {
        return prefs.getInt(Integer.toString(color), 1);
    }

    public void setSortingMethod(String sortingMethod) {
        SharedPreferences.Editor editor =  prefs.edit();
        editor.putString("mainElementSorting", sortingMethod);
        this.sortingMethod = sortingMethod;
        editor.commit();
    }

    public String getSortingMethod() {
        if (this.sortingMethod != null) {
            return this.sortingMethod;
        } else {
            return prefs.getString("mainElementSorting", null);
        }
    }

    public void setMasterPassword(String password){
        SharedPreferences.Editor editor =  prefs.edit();
        editor.putString("masterPassword", password);
        editor.commit();
    }

    public String getMasterPassword() {
        return prefs.getString("masterPassword", "");
    }


    public String getSHA1Hash(String masterPasswordHash) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new String(Hex.encodeHex(DigestUtils.sha1(masterPasswordHash)));
    }
}
