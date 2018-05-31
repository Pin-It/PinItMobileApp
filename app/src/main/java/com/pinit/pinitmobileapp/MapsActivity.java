package com.pinit.pinitmobileapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private List<LatLng> lstLatLng = new ArrayList<LatLng>();

    public List<ImageButton> imgButtonList = new ArrayList<>();

    int pincolor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame_work);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        imgButtonList.add((ImageButton)findViewById(R.id.pinunoButton));
        imgButtonList.add((ImageButton)findViewById(R.id.pindosButton));
        imgButtonList.add((ImageButton)findViewById(R.id.pintresButton));
        imgButtonList.add((ImageButton)findViewById(R.id.pincuatroButton));
        imgButtonList.add((ImageButton)findViewById(R.id.pincincoButton));
        imgButtonList.add((ImageButton)findViewById(R.id.pinseisButton));


        for(int i = 0; i < imgButtonList.size(); i++) {
            addListenerButton(i);
        }
    }

    public void addListenerButton(int num) {
        final ImageButton imgButton = imgButtonList.get(num);

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgButton.equals((ImageButton)findViewById(R.id.pinunoButton))) {
                    pincolor = R.drawable.pinuno;
                } else if (imgButton.equals((ImageButton)findViewById(R.id.pindosButton))) {
                    pincolor = R.drawable.pindos;
                } else if (imgButton.equals((ImageButton)findViewById(R.id.pintresButton))) {
                    pincolor = R.drawable.pintres;
                } else if (imgButton.equals((ImageButton)findViewById(R.id.pincuatroButton))) {
                    pincolor = R.drawable.pincuatro;
                } else if (imgButton.equals((ImageButton)findViewById(R.id.pincincoButton))) {
                    pincolor = R.drawable.pincinco;
                } else {
                    pincolor = R.drawable.pinseis;
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                lstLatLng.add(point);
                googleMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(pincolor)));
            }
        });
    }

    private void enableMyLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            PermissionUtility.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null){
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if(PermissionUtility.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtility.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
}
