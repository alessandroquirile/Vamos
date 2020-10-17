package com.quiriletelese.troppadvisorproject.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class GPSTracker extends Service implements LocationListener {

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MINIMUM_TIME_BETWEEW_UPDATES = 1000 * 60; // 1 minute
    private LocationManager locationManager;
    private boolean canGetLocation = false;
    private Location location;
    private Double latitude;
    private Double longitude;
    private Context context;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        setCanGetLocation(true);
    }

    @Override
    public void onProviderDisabled(String s) {
        setCanGetLocation(false);
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = createLocationManager();
            assert locationManager != null;
           /* boolean isGPSEnabled = isGPSEnabled();
            boolean isNetworkEnabled = isNetworkEnabled();*/
            if (isNetworkProviderEnabled()) {
                setCanGetLocation(true);
                getLocationFromNetworkProvider();
                getLocationFromGPS();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private LocationManager createLocationManager() {
        return (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MINIMUM_TIME_BETWEEW_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private void getLocationFromNetworkProvider() {
        Log.d("LOCATION FROM NETWORK", "LOCATION FROM NETWORK");
        if (isNetworkEnabled()) {
            requestLocationUpdates();
            Log.d("Network", "Network");
            if (!isLocationManagerNull()) {
                location = getLastKnownLocation();
                if (!locationIsNull())
                    getCoordinatesFromLocation();
            }
        }
    }

    private void getLocationFromGPS() {
        Log.d("LOCATION FROM GPS", "LOCATION FROM GPS");
        if (isGPSEnabled()) {
            if (locationIsNull()) {
                requestLocationUpdates();
                Log.d("GPS Enabled", "GPS Enabled");
                if (!isLocationManagerNull()) {
                    location = getLastKnownLocation();
                    if (location != null)
                        getCoordinatesFromLocation();
                }
            }
        }
    }

    private void getCoordinatesFromLocation() {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private boolean isNetworkProviderEnabled() {
        return isGPSEnabled() && isNetworkEnabled();
    }

    private boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isNetworkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isLocationManagerNull() {
        return locationManager == null;
    }

    private boolean locationIsNull() {
        return location == null;
    }

    public double getLatitude() {
        if (location != null)
            latitude = location.getLatitude();
        return latitude == null ? 0 : latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude == null ? 0 : longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    private void setCanGetLocation(boolean canGetLocation) {
        this.canGetLocation = canGetLocation;
    }

}
