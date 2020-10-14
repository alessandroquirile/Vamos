package com.quiriletelese.troppadvisorproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.quiriletelese.troppadvisorproject.interfaces.Constants;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class UserSharedPreferences implements Constants {

    private SharedPreferences sharedPreferences;

    public UserSharedPreferences(@NotNull Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void putStringSharedPreferences(String name, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public void putBooleanSharedPreferences(String name, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public String getStringSharedPreferences(String preference) {
        return getSharedPreferences().getString(preference, "");
    }

    public boolean getBooleanSharedPreferences(String preference) {
        return getSharedPreferences().getBoolean(preference, true);
    }

}
