package com.example.chenyijie.customframe.common;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

import lombok.Getter;

/**
 * 透過全部的provider來取得目前位置
 *
 * Created by chenyijie on 2017/5/22.
 */

public class LocationServiceManager {

    private static LocationServiceManager instance = null;

    private LocationManager locationManager;
    @Getter private Location location;

    public static LocationServiceManager getInstance(Context context){
        if(instance == null) instance = new LocationServiceManager(context);
        return instance;
    }

    private LocationServiceManager(Context context){
        initLocationService(context);
    }

    @TargetApi(23)
    private void initLocationService(Context context) {
        // 檢查權限
        if (Build.VERSION.SDK_INT >= 23
                && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            return;
        }

        try {
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                locationManager.requestLocationUpdates(provider, 1000, 0,
                        new LocationListener() {

                            public void onLocationChanged(Location location) {
                            }

                            public void onProviderDisabled(String provider) {
                            }

                            public void onProviderEnabled(String provider) {
                            }

                            public void onStatusChanged(String provider, int status,
                                                        Bundle extras) {
                            }
                        });
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) this.location = location;
            }
        } catch (Exception e) {
            Log.d("Test:::", "Get location fail " + e.getMessage());
        }
    }
}
