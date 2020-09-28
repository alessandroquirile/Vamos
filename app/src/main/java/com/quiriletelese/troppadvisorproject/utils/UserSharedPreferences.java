package com.quiriletelese.troppadvisorproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.quiriletelese.troppadvisorproject.interfaces.Constants;

public class UserSharedPreferences implements Constants {

    private SharedPreferences sharedPreferences;
    private Context context;

    public UserSharedPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, context.MODE_PRIVATE);
    }

    public void putSharedPreferencesString(String name, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
