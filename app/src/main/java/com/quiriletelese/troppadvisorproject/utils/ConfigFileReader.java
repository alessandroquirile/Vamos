package com.quiriletelese.troppadvisorproject.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class ConfigFileReader {

    public static String getProperty(String key, @NotNull Context context) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("config.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }
}