package com.quiriletelese.troppadvisorproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.quiriletelese.troppadvisorproject.model_helpers.Constants;

import org.jetbrains.annotations.NotNull;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class UserSharedPreferences {

    private final SharedPreferences sharedPreferences;

    public UserSharedPreferences(@NotNull Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.getSharedPreferences(), Context.MODE_PRIVATE);
    }

    public void putStringSharedPreferences(String name, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public void putLongSharedPreferences(String name, Long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(name, value);
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

    public boolean constains(String preference) {
        return getSharedPreferences().contains(preference);
    }

    public String getStringSharedPreferences(String preference) {
        return getSharedPreferences().getString(preference, "");
    }

    public Long getLongSharedPreferences(String preference) {
        return getSharedPreferences().getLong(preference, 0L);
    }

    public boolean getBooleanSharedPreferences(String preference) {
        return getSharedPreferences().getBoolean(preference, true);
    }

}
