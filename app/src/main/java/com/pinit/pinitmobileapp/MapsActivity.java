package com.pinit.pinitmobileapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private List<LatLng> lstLatLng = new ArrayList<LatLng>();

    public List<ImageButton> imgButtonList = new ArrayList<>();
    private Map<FloatingActionButton, TextView> pinsToText = new HashMap<>();
    FloatingActionButton pinsMenu, pincinco, pincuatro, pindos, pinseis, pintres, pinuno;
    int pincolor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame_work);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pinsMenu = (FloatingActionButton) findViewById(R.id.pinListBttn);
        pinsToText.put((FloatingActionButton) findViewById(R.id.pincincoBttn), (TextView) findViewById(R.id.pincincoText));
        pinsToText.put((FloatingActionButton) findViewById(R.id.pincuatroBttn), (TextView) findViewById(R.id.pincuatroText));
        pinsToText.put((FloatingActionButton) findViewById(R.id.pindosBttn), (TextView) findViewById(R.id.pindosText));
        pinsToText.put((FloatingActionButton) findViewById(R.id.pinseisBttn), (TextView) findViewById(R.id.pinseisText));
        pinsToText.put((FloatingActionButton) findViewById(R.id.pintresBttn), (TextView) findViewById(R.id.pintresText));
        pinsToText.put((FloatingActionButton) findViewById(R.id.pinunoBttn), (TextView) findViewById(R.id.pinunoText));


        setAllPinsVisibility(false, false, null);
        final TextView purpletext = (TextView) findViewById(R.id.pincincoText);
        purpletext.setVisibility(View.GONE);

        pinsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPinsVisible()) {
                    setAllPinsVisibility(false, false, null);
                    pinsMenu.setImageResource(R.drawable.pinuno);
                } else {
                    setAllPinsVisibility(true, true, null);
                    pinsMenu.setImageResource(R.drawable.cancel);
                }
            }
        });

        for (final FloatingActionButton bttn : pinsToText.keySet()) {
            bttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (bttn.equals((FloatingActionButton) findViewById(R.id.pinunoBttn))) {
                        pincolor = R.drawable.pinuno;
                    } else if (bttn.getId() == R.id.pindosBttn) {
                        pincolor = R.drawable.pindos;
                    } else if (bttn.equals((FloatingActionButton) findViewById(R.id.pintresBttn))) {
                        pincolor = R.drawable.pintres;
                    } else if (bttn.equals((FloatingActionButton) findViewById(R.id.pincuatroBttn))) {
                        pincolor = R.drawable.pincuatro;
                    } else if (bttn.equals((FloatingActionButton) findViewById(R.id.pincincoBttn))) {
                        pincolor = R.drawable.pincinco;
                    } else {
                        pincolor = R.drawable.pinseis;
                    }

//                    boolean visibilityText = true;
//
//                    if (isTextVisibile(bttn)) {
//                        visibilityText = false;
//                    }
//
//                    setAllPinsVisibility(true, visibilityText, bttn);
                    showCommentDialogueBox();

                }
            });
        }


    }

    private void showCommentDialogueBox() {
        AlertDialog.Builder commentDialogueBuilder = new AlertDialog.Builder(MapsActivity.this);
        View commentView = getLayoutInflater().inflate(R.layout.activity_add_comment, null);
        TextView commentDialogueBoxTitle = commentView.findViewById(R.id.addComment);
        EditText commentInputText = commentView.findViewById(R.id.comment_text_input);
        Button submitButton = commentView.findViewById(R.id.submit_comment);
        Button skipButton = commentView.findViewById(R.id.skip_comment);

        commentDialogueBuilder.setView(commentView);
        final AlertDialog commentDialogue = commentDialogueBuilder.create();

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentDialogue.dismiss();
            }
        });

        commentDialogue.show();




    }

    private void setAllPinsVisibility(boolean pin, boolean text, FloatingActionButton bttn) {
        int visibilityPin = pin ? View.VISIBLE : View.GONE;
        int visibilityText = text ? View.VISIBLE : View.GONE;

        if (bttn == null) {
            for (Map.Entry<FloatingActionButton, TextView> entry : pinsToText.entrySet()) {
                entry.getKey().setVisibility(visibilityPin);
                entry.getValue().setVisibility(visibilityText);
            }
        } else {
            pinsToText.get(bttn).setVisibility(visibilityText);
        }

    }

    private boolean isPinsVisible() {
        boolean visible = true;
        for (FloatingActionButton bttn : pinsToText.keySet()) {
            if (bttn.getVisibility() == View.GONE) {
                visible = false;
                break;
            }
        }
        return visible;
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
        //   enableMyLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,false));
        if (location != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(17).bearing(90).tilt(40).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                lstLatLng.add(point);
                MarkerOptions markerOptions = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(pincolor));
                googleMap.addMarker(markerOptions);
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, AddCommentActivity.class);
                startActivity(intent);
                finish();

                return true;
            }
        });

    }

//    public void onMapSearch(View view) {
//        EditText locationSearch = (EditText) findViewById(R.id.editText);
//        String location = locationSearch.getText().toString();
//        List<Address> addressList = null;
//
//        if (location != null || !location.equals("")) {
//            Geocoder geocoder = new Geocoder(this);
//            try {
//                addressList = geocoder.getFromLocationName(location, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Address address = addressList.get(0);
//            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle)));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        }
//    }

//    private void enableMyLocation() {
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//        != PackageManager.PERMISSION_GRANTED) {
//            PermissionUtility.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
//        } else if (mMap != null){
//            mMap.setMyLocationEnabled(true);
//        }
//    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

  // @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            return;
//        }
//
//        if(PermissionUtility.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            enableMyLocation();
//        } else {
//            mPermissionDenied = true;
//        }
//    }

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

    public boolean isTextVisibile(FloatingActionButton bttn) {
        return pinsToText.get(bttn).getVisibility() == View.VISIBLE;
    }
}
