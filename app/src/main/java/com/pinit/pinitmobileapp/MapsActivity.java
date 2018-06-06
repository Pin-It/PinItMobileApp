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
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.pinit.api.errors.APIError;
import com.pinit.api.NetworkListener;
import com.pinit.api.PinItAPI;
import com.pinit.api.models.Pin;
import org.json.JSONObject;

import java.util.*;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String USER_TOKEN = "userToken";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private List<LatLng> lstLatLng = new ArrayList<LatLng>();
    private Switch mSwitch;

    public List<ImageButton> imgButtonList = new ArrayList<>();
    private Map<FloatingActionButton, TextView> pinsToText = new HashMap<>();
    FloatingActionButton pinsMenu, pincinco, pincuatro, pindos, pinseis, pintres, pinuno;
    int pincolor = -1;
    Pin.Type pinType;

    private List<Pin> allPins = new ArrayList<>();
    private List<Marker> allMarkers = new ArrayList<>();
    private PinItAPI api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame_work);

        Bundle extras = getIntent().getExtras();
        String token = null;
        if (extras != null) {
            token = extras.getString(USER_TOKEN, null);
        }
        api = new PinItAPI(Volley.newRequestQueue(this), token);
  
        pincolor = R.drawable.pinuno;

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
                        pinType = Pin.Type.PICKPOCKET;
                    } else if (bttn.getId() == R.id.pindosBttn) {
                        pincolor = R.drawable.pindos;
                        pinType = Pin.Type.DRUNK;
                    } else if (bttn.equals((FloatingActionButton) findViewById(R.id.pintresBttn))) {
                        pincolor = R.drawable.pintres;
                        pinType = Pin.Type.ROBBERY;
                    } else if (bttn.equals((FloatingActionButton) findViewById(R.id.pincuatroBttn))) {
                        pincolor = R.drawable.pincuatro;
                        pinType = Pin.Type.SCAM;
                    } else if (bttn.equals((FloatingActionButton) findViewById(R.id.pincincoBttn))) {
                        pincolor = R.drawable.pincinco;
                        pinType = Pin.Type.HARASSMENT;
                    } else {
                        pincolor = R.drawable.pinseis;
                        pinType = Pin.Type.OTHERS;
                    }

//                    boolean visibilityText = true;
//
//                    if (isTextVisibile(bttn)) {
//                        visibilityText = false;
//                    }
//
//                    setAllPinsVisibility(true, visibilityText, bttn);
//                    showCommentDialogueBox();

                }
            });
        }

        mSwitch = (Switch) findViewById(R.id.switch_maps);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSwitch.isChecked()) {
                    showHeatMap();
                    hideAllPins();
                } else {
                    hideHeatMap();
                    showAllPins();
                }
            }
        });
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
        Log.d("MapReady", "ready");
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng point) {
                if (pincolor == -1) return;
                lstLatLng.add(point);
                Pin pin = new Pin(pinType, point.latitude, point.longitude);
                api.uploadNewPin(pin, new NetworkListener<JSONObject>() {
                    @Override
                    public void onReceive(JSONObject response) {
                        addNewMarker(point, pincolor, "Newly added");
                        showCommentDialogueBox();
                    }

                    @Override
                    public void onError(APIError error) {
                        Toast.makeText(getApplication(), "You're not logged in :(", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        api.getAllPins(new NetworkListener<List<Pin>>() {
            @Override
            public void onReceive(List<Pin> pins) {
                allPins = pins;
                for (Pin pin : allPins) {
                    LatLng point = new LatLng(pin.getLatitude(), pin.getLongitude());
                    Pin.Type type = pin.getType();
                    int color;
                    switch (type.toInt()) {
                        case 1:
                            color = R.drawable.pinuno;
                            break;
                        case 2:
                            color = R.drawable.pindos;
                            break;
                        case 3:
                            color = R.drawable.pintres;
                            break;
                        case 4:
                            color = R.drawable.pincuatro;
                            break;
                        case 5:
                            color = R.drawable.pincinco;
                            break;
                        default:
                            color = R.drawable.pinseis;
                            break;
                    }
                    addNewMarker(point, color, type.toString());
                }
            }

            @Override
            public void onError(APIError error) {

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, AddCommentActivity.class);
                startActivity(intent);
                finish();
                showCommentDialogueBox();
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

    private void showHeatMap() {
        if (mOverlay == null) {
            List<LatLng> list = getPinsLatLngs(allPins);
            mProvider = new HeatmapTileProvider.Builder()
                    .data(list)
                    .opacity(0.6)
                    .build();
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        } else {
            mOverlay.setVisible(true);
        }
    }

    private void hideHeatMap() {
        if (mOverlay != null) {
            mOverlay.setVisible(false);
        }
    }

    private List<LatLng> getPinsLatLngs(List<Pin> pins) {
        List<LatLng> latLngs = new ArrayList<>();
        for (Pin pin : pins) {
            latLngs.add(new LatLng(pin.getLatitude(), pin.getLongitude()));
        }
        return latLngs;
    }

    private void addNewMarker(LatLng point, int color, String title) {
        MarkerOptions options = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(color)).title(title);
        Marker marker = mMap.addMarker(options);
        allMarkers.add(marker);
    }

    private void hideAllPins() {
        for (Marker marker : allMarkers) {
            marker.setVisible(false);
        }
    }

    private void showAllPins() {
        for (Marker marker : allMarkers) {
            marker.setVisible(true);
        }
    }
}
