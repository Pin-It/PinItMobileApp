package com.pinit.pinitmobileapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pinit.api.PinItAPI;

public class DeviceLocationService extends Service {
    private static final long LOCATION_INTERVAL = 5000;
    private static final float LOCATION_DISTANCE = 10;

    private PinItAPI api;
    private LocationManager mLocationManager;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("DeviceLocationService", location.toString());
            String deviceToken = FirebaseInstanceId.getInstance().getToken();
            api.uploadDeviceLocation(deviceToken, location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public DeviceLocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        api = PinItAPI.getInstance(Volley.newRequestQueue(getApplicationContext()));

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    listener
            );
        } catch (SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
