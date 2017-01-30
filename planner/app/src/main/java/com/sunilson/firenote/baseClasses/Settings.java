package com.sunilson.firenote.baseClasses;

/**
 * Created by linus_000 on 05.11.2016.
 */

public class Settings {

    private String defaultColor;

    public Settings() {
        defaultColor = "#FFFFA5";
    }

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    private void saveSettingsToDisk() {

    }
}
