package com.quiriletelese.troppadvisorproject.controllers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.quiriletelese.troppadvisorproject.R;
import com.quiriletelese.troppadvisorproject.interfaces.Constants;
import com.quiriletelese.troppadvisorproject.views.HomePageActivity;

public class HomePageActivityController implements Constants {

    private HomePageActivity homePageActivity;

    public HomePageActivityController(HomePageActivity homePageActivity) {
        this.homePageActivity = homePageActivity;
    }

    public void checkPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(homePageActivity, permission)
                == PackageManager.PERMISSION_DENIED)
            requestPermissions(permission, requestCode);
    }

    public void requestPermissions(String permission, int requestCode) {
        ActivityCompat.requestPermissions(homePageActivity, new String[]{permission}, requestCode);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION:
                checkPermissionResult(grantResults);
                break;
        }
    }

    private void checkPermissionResult(@NonNull int[] grantResults) {
        if (!isPermissionGranted(grantResults))
            startActivityPermissionSettings();
    }

    private void startActivityPermissionSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", homePageActivity.getPackageName(), null);
        intent.setData(uri);
        homePageActivity.startActivity(intent);

    }

    private boolean isPermissionGranted(@NonNull int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

}
