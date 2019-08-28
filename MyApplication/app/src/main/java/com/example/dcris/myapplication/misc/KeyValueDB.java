package com.example.dcris.myapplication.misc;

import android.content.Context;
import android.content.SharedPreferences;

public class KeyValueDB {
    private SharedPreferences sharedPreferences;
    private static String PREF_NAME = "prefs";

    public KeyValueDB() {
        // Blank
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getUsername(Context context) {
        return getPrefs(context).getString("username_key", "default_username");
    }

    public static String getUsertype(Context context) {
        return getPrefs(context).getString("usertype_key", "default_usertype");
    }

    public static void setUsertype(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("usertype_key", input);
        editor.apply();
    }

    public static void setUsername(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("username_key", input);
        editor.apply();
    }
}
