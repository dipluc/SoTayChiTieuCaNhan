package com.example.personalexpensemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ExpenseSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_DARK_MODE = "dark_mode";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createSession(int userId, boolean rememberMe) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
        editor.apply();
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public boolean isRemembered() {
        return pref.getBoolean(KEY_REMEMBER_ME, false);
    }

    public void clearSession() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_REMEMBER_ME);
        editor.apply();
    }

    public void setDarkMode(boolean isDarkMode) {
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();
    }

    public boolean isDarkMode() {
        return pref.getBoolean(KEY_DARK_MODE, false);
    }
}