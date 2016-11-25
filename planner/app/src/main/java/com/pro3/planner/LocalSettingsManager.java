package com.pro3.planner;

import android.content.SharedPreferences;

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

        int category = prefs.getInt(categoryName, 0);

        if (category == 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(categoryName, 1);
            editor.commit();
        }
    }

    public void setCategoryVisibility(String categoryName, int visibility) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(categoryName, visibility);
        editor.commit();
    }

    public int getCategoryVisibility(String categoryName) {
        return prefs.getInt(categoryName, 1);
    }

    public void removeCategory(String categoryName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(categoryName);
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
            return prefs.getString("mainElementSorting", "nameAscending");
        }
    }
}
