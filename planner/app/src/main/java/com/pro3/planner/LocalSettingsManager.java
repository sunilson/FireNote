package com.pro3.planner;

import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by linus_000 on 18.11.2016.
 */

public class LocalSettingsManager {

    private HashMap<String, Integer> categories = new HashMap<>();
    private HashMap<Integer, Integer> colors = new HashMap<>();
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
            categories.put(categoryName, 1);
            editor.commit();
        } else {
            categories.put(categoryName, category);
        }
    }

    public void setCategoryVisibility(String categoryName, int visibility) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(categoryName, visibility);
        categories.put(categoryName, visibility);
        editor.commit();
    }

    public int getCategoryVisibility(String categoryName) {
        Integer visibility = categories.get(categoryName);

        if (visibility == null) {
            return 0;
        }

        return visibility;
    }

    public void removeCategory(String categoryName) {
        categories.remove(categoryName);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(categoryName);
        editor.apply();
    }

    public void setColorVisibility(int color, int visibility) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Integer.toString(color), visibility);
        colors.put(color, visibility);
        editor.commit();
    }


    public int getColorVisibility(int color) {
        Integer visibility = colors.get(color);

        if (visibility == null) {
            return 1;
        }

        return visibility;
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
