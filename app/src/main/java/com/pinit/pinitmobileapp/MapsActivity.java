package com.pinit.pinitmobileapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;

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
import com.pinit.api.models.Comment;
import com.pinit.api.models.Pin;
import org.json.JSONObject;

import java.util.*;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String USER_TOKEN = "userToken";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Used to anchor pins at a specific point, normalized to the range [0, 1]
     * This makes the pins not "float".
     */
    public static final float PIN_ANCHOR_X = 0.5f;
    public static final float PIN_ANCHOR_Y = 0.72f;

    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private List<LatLng> lstLatLng = new ArrayList<LatLng>();
    private SwitchCompat mSwitch;
    private SwitchCompat pSwitch;

    FloatingActionButton pinsMenu, circlepin, extraflagpin, flagpin, starpin, wallpin, checkpin;
    private List<AppCompatButton> pinsList = new ArrayList<>();
    private List<Integer> colours = new ArrayList<>();
    private List<Integer> icons = new ArrayList<>();

    private List<Pin> allPins = new ArrayList<>();
    private List<Marker> allMarkers = new ArrayList<>();
    private PinItAPI api;

    int pincolor = -1;
    int pinshape = -1;
    private Pin.Type pinType = Pin.Type.OTHERS;
    public PinMode currentMode = PinMode.ICON;
    private boolean pinChosen = false;

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
        pinshape = R.drawable.circlepin;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pinsList.add((AppCompatButton) findViewById(R.id.circlePinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.checkPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.extraFlagPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.flagPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.starPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.wallPinBttn));

        colours.add(R.drawable.pinuno);
        colours.add(R.drawable.pincinco);
        colours.add(R.drawable.pincuatro);
        colours.add(R.drawable.pindos);
        colours.add(R.drawable.pinseis);
        colours.add(R.drawable.pintres);

        icons.add(R.drawable.circlepin);
        icons.add(R.drawable.checkpin);
        icons.add(R.drawable.extraflagpin);
        icons.add(R.drawable.flagpin);
        icons.add(R.drawable.starpin);
        icons.add(R.drawable.wallpin);

        setAllPinsVisibility(false, null);

        mSwitch = (SwitchCompat) findViewById(R.id.switch_maps);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSwitch.isChecked()) {
                    // General safety mode
                    showHeatMap();
                    hideAllPins();
                    pinsMenu.hide();
                    if (isPinsVisible()) {
                        setAllPinsVisibility(false,null);
                    }
                } else {
                    // Pin mode
                    hideHeatMap();
                    showAllPins();
                    setToCorrespondingImage();
                    pinsMenu.show();
                }
            }
        });

        pinsMenu = (FloatingActionButton) findViewById(R.id.switchPinButton);
        pinsMenu.setImageResource(R.drawable.wallpin);

        pSwitch = (SwitchCompat) findViewById(R.id.switch_pins);
        pSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int pinsMenuId = 0;

                if (isPinsVisible()) {
                    pinsMenuId = R.drawable.cancel;
                }

                if (compoundButton.isChecked()) {
                    setMode(PinMode.COLOUR);
                    setAllPinsMode(PinMode.COLOUR);
                    if (!isPinsVisible()) {
                        pinsMenuId = R.drawable.pinuno;
                    }

                } else {
                    setMode(PinMode.ICON);
                    setAllPinsMode(PinMode.ICON);
                    if (!isPinsVisible()) {
                        pinsMenuId = R.drawable.wallpin;
                    }
                }

                for (Marker m : allMarkers) {
                    Pin pin = (Pin) m.getTag();
                    m.setIcon(BitmapDescriptorFactory.fromResource(pinTypeToResource(pin.getType())));
                }
                pinsMenu.setImageResource(pinsMenuId);
            }
        });


        pinsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPinsVisible()) {
                    setAllPinsVisibility(false,  null);
//                    setToCorrespondingImage();
                    if (currentMode == PinMode.ICON) {
                        pinsMenu.setImageResource(R.drawable.wallpin);
                    } else {
                        pinsMenu.setImageResource(R.drawable.pinuno);
                    }
                    pinChosen = false;
                } else {
                    setAllPinsVisibility(true,  null);
                    pinsMenu.setImageResource(R.drawable.cancel);
                }
            }
        });

        for (AppCompatButton bttn : pinsList) {
            bttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> list = currentMode == PinMode.COLOUR ? colours : icons;
                    pincolor = list.get(pinsList.indexOf((AppCompatButton) v));
                    pinType = Pin.Type.values()[pinsList.indexOf((AppCompatButton) v)];
                    pinChosen = true;
                }
            });
        }
    }

    private void setToCorrespondingImage() {
        if (currentMode == PinMode.ICON) {
            pinsMenu.setImageResource(R.drawable.wallpin);
        } else {
            pinsMenu.setImageResource(R.drawable.pinuno);
        }
    }

    private void setMode(PinMode colour) {
        this.currentMode = colour;
    }


    private void showCommentDialogueBox(final Pin pin) {
        AlertDialog.Builder commentDialogueBuilder = new AlertDialog.Builder(MapsActivity.this);
        LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
        View commentView = getLayoutInflater().inflate(R.layout.activity_add_comment, null);
        TextView commentDialogueBoxTitle = commentView.findViewById(R.id.addComment);
        final EditText commentInputText = commentView.findViewById(R.id.comment_text_input);
        AppCompatButton submitButton = commentView.findViewById(R.id.submit_comment);
        AppCompatButton cancelButton = commentView.findViewById(R.id.cancel_button);

        commentDialogueBuilder.setView(commentView);
        final AlertDialog commentDialogue = commentDialogueBuilder.create();
        commentDialogue.show();
        commentDialogue.getWindow().setLayout(1000,800);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentDialogue.dismiss();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentInputText.getText().toString();
                Comment comment = new Comment(pin, commentText);
                api.uploadNewComment(comment);
            }
        });
    }


    private void setAllPinsVisibility(boolean pin, AppCompatButton bttn) {
        int visibilityPin = pin ? View.VISIBLE : View.GONE;

        if (bttn == null) {
            for (AppCompatButton button : pinsList) {
                button.setVisibility(visibilityPin);
            }
        } else {
            bttn.setVisibility(visibilityPin);
        }

    }

    private boolean isPinsVisible() {
        boolean visible = true;
        for (AppCompatButton bttn : pinsList) {
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
            public void onMapClick(final LatLng point) {
                if (pincolor == -1) return;
                if (!pinChosen) return;
                lstLatLng.add(point);
                Pin pin = new Pin(pinType, point.latitude, point.longitude);
                api.uploadNewPin(pin, new NetworkListener<JSONObject>() {
                    @Override
                    public void onReceive(JSONObject response) {
                        Pin addedPin = new Pin(response);
                        addNewMarker(addedPin);
                        showCommentDialogueBox(addedPin);
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
                    addNewMarker(pin);
                }
            }


            @Override
            public void onError(APIError error) {

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("MapReady", "click");
//                Intent intent = new Intent(MapsActivity.this, AddCommentActivity.class);
//                startActivity(intent);
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

    public void setAllPinsMode(PinMode allPinsMode) {
        List<Integer> list = allPinsMode == PinMode.COLOUR ? colours : icons;

        for (int i = 0; i < pinsList.size(); i++) {
            Drawable img = MapsActivity.this.getResources().getDrawable(list.get(i));
            pinsList.get(i).setCompoundDrawablesWithIntrinsicBounds( null, null, img, null);
        }
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

    private void addNewMarker(Pin pin) {
        LatLng point = new LatLng(pin.getLatitude(), pin.getLongitude());
        String title = pin.getType().toString();
        int pinResource = pinTypeToResource(pin.getType());
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromResource(pinResource))
                .anchor(PIN_ANCHOR_X, PIN_ANCHOR_Y)
                .title(title);
        Marker marker = mMap.addMarker(options);
        marker.setTag(pin);
        allMarkers.add(marker);
    }

    private int pinTypeToResource(Pin.Type type) {
        List<Integer> list = currentMode == PinMode.COLOUR ? colours : icons;
        return list.get(type.ordinal());
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
