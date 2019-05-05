package com.example.realmapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String PRE_LOAD = "preLoad";
    private static final String PREFS_NAME = "prefs";
    private static Preferences instance;
    private final SharedPreferences sharedPreferences;

    public Preferences(Context context) {

        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static Preferences with(Context context) {

        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public void setPreLoad(boolean totalTime) {

        sharedPreferences
                .edit()
                .putBoolean(PRE_LOAD, totalTime)
                .apply();
    }

    public boolean getPreLoad(){
        return sharedPreferences.getBoolean(PRE_LOAD, false);
    }


}
