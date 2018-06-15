package com.pinit.pinitmobileapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.*;

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

import java.io.IOException;
import java.util.*;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener {

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

    public static SharedPreferences sharedPreferences;

    FloatingActionButton pinsMenu, circlepin, extraflagpin, flagpin, starpin, wallpin, checkpin;
    private List<AppCompatButton> pinsList = new ArrayList<>();
    private List<Integer> colours = new ArrayList<>();
    private List<Integer> icons = new ArrayList<>();
    private Map<AppCompatButton, Integer> pinsDefaultColors = new HashMap<>();

    private List<Pin> allPins = new ArrayList<>();
    private List<Marker> allMarkers = new ArrayList<>();
    private PinItAPI api;

    int pincolor = -1;
    int pinshape = -1;
    private Pin.Type pinType = Pin.Type.OTHERS;
    public PinMode currentMode = PinMode.COLOUR;
    private boolean pinChosen = false;

    private AppCompatButton setting;

    private static final String SHOWCASE_ID = "SHOWCASE";


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

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        pinsList.add((AppCompatButton) findViewById(R.id.circlePinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.checkPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.extraFlagPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.flagPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.starPinBttn));
        pinsList.add((AppCompatButton) findViewById(R.id.wallPinBttn));

        colours.add(R.drawable.pick_pocket);
        colours.add(R.drawable.drunk);
        colours.add(R.drawable.robbery);
        colours.add(R.drawable.scam);
        colours.add(R.drawable.harrassment);
        colours.add(R.drawable.others);

        icons.add(R.drawable.circlepin);
        icons.add(R.drawable.checkpin);
        icons.add(R.drawable.extraflagpin);
        icons.add(R.drawable.flagpin);
        icons.add(R.drawable.starpin);
        icons.add(R.drawable.wallpin);

//        pinsDefaultColors.put(pinsList.get(0), 0xE55554);
//        pinsDefaultColors.put(pinsList.get(1), 0xA344E5);
//        pinsDefaultColors.put(pinsList.get(2), 0xE5710B);
//        pinsDefaultColors.put(pinsList.get(3), 0x318BE5);
//        pinsDefaultColors.put(pinsList.get(4), 0x14E576);
//        pinsDefaultColors.put(pinsList.get(5), 0xE0E51E);

//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setAllPinsVisibility(false, null);

        setting = (AppCompatButton) findViewById(R.id.settingsBttn);
        setting.setOnClickListener(new AppCompatButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

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
                        setAllPinsVisibility(false, null);
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
        setAllPinsMode(currentMode);
        pinsMenu.setImageResource(R.drawable.pick_pocket);

//        pSwitch = (SwitchCompat) findViewById(R.id.switch_pins);
//        pSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                int pinsMenuId = 0;
//
//                if (isPinsVisible()) {
//                    pinsMenuId = R.drawable.cancel;
//                }
//
//                if (compoundButton.isChecked()) {
//                    setMode(PinMode.COLOUR);
//                    setAllPinsMode(PinMode.COLOUR);
//                    if (!isPinsVisible()) {
//                        pinsMenuId = R.drawable.pinuno;
//                    }
//                    pSwitch.setThumbDrawable(MapsActivity.this.getResources().getDrawable(R.drawable.switch_thumb_wallpins));
//
//                } else {
//                    setMode(PinMode.ICON);
//                    setAllPinsMode(PinMode.ICON);
//                    if (!isPinsVisible()) {
//                        pinsMenuId = R.drawable.wallpin;
//                    }
//                    pSwitch.setThumbDrawable(MapsActivity.this.getResources().getDrawable(R.drawable.switch_thumb_pins));
//                }
//
//                for (Marker m : allMarkers) {
//                    Pin pin = (Pin) m.getTag();
//                    m.setIcon(BitmapDescriptorFactory.fromResource(pinTypeToResource(pin.getType())));
//                }
//                pinsMenu.setImageResource(pinsMenuId);
//            }
//        });


        pinsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPinsVisible()) {
                    setAllPinsVisibility(false, null);
//                    setToCorrespondingImage();
                    if (currentMode == PinMode.ICON) {
                        pinsMenu.setImageResource(R.drawable.wallpin);
                    } else {
                        pinsMenu.setImageResource(R.drawable.pinuno);
                    }
                    pinChosen = false;
                } else {
                    setAllPinsVisibility(true, null);
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

        Bundle resultIntent = getIntent().getExtras();
        if (resultIntent != null) {
            if (resultIntent.getInt("planning") == R.id.planning) {
                showTutorSequence(500);
            }
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

    private void showAllCommentsBox(final Pin pin) {
        View view = getLayoutInflater().inflate(R.layout.pin_comments, null);
        TextView title = view.findViewById(R.id.comments_list_title);
        TextView subtitle = view.findViewById(R.id.comments_list_subtitle);
        ListView list = view.findViewById(R.id.comments_list);
        Button addCommentButton = view.findViewById(R.id.comments_list_add);

        title.setText(pin.getType().toString());
        subtitle.setText(pin.getCommentCount() + " comments");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.comment_list_item, R.id.comment_text, pin.getComments());
        list.setAdapter(adapter);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialogueBox(pin, new OnCommentPostedListener(MapsActivity.this) {
                    @Override
                    protected void onCommentPosted() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        new AlertDialog.Builder(MapsActivity.this)
                .setView(view)
                .create()
                .show();
    }

    private void showCommentDialogueBox(final Pin pin) {
        showCommentDialogueBox(pin, null);
    }

    private void showCommentDialogueBox(final Pin pin, final OnCommentPostedListener listener) {
        final View commentView = getLayoutInflater().inflate(R.layout.activity_add_comment, null);
        final EditText commentInputText = commentView.findViewById(R.id.comment_text_input);
        final AppCompatButton submitButton = commentView.findViewById(R.id.submit_comment);
        final AppCompatButton cancelButton = commentView.findViewById(R.id.cancel_button);

        final AlertDialog commentDialogue = new AlertDialog.Builder(MapsActivity.this)
                .setView(commentView)
                .create();
        commentDialogue.show();
        commentDialogue.getWindow().setLayout(1000,600);

        String buttonText = pin.idIsValid() ? "comment" : "pin";
        cancelButton.setText(String.format(getString(R.string.comment_dialogue_cancel), buttonText));
        submitButton.setText(String.format(getString(R.string.comment_dialogue_submit), buttonText));
        commentInputText.setHint(pin.idIsValid() ? "" : "(Optional)");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentDialogue.dismiss();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pin.idIsValid()) {
                    // Adding comment to existing pin
                    if (getCommentText().isEmpty()) {
                        commentInputText.setError("Please enter a comment");
                    } else {
                        uploadComment(pin);
                    }
                } else {
                    // Add comment AND pin
                    api.uploadNewPin(pin, new OnPinUploadedListener(MapsActivity.this) {
                        @Override
                        protected void onPinUploaded(Pin uploadedPin) {
                            addNewMarker(uploadedPin);
                            uploadComment(uploadedPin);
                        }
                    });
                }
            }

            private String getCommentText() {
                return commentInputText.getText().toString();
            }

            private void uploadComment(final Pin pin) {
                final String commentText = getCommentText();
                if (commentText.isEmpty()) {
                    commentDialogue.dismiss();
                    return;
                }
                Comment comment = new Comment(pin, commentText);
                api.uploadNewComment(comment, new OnCommentPostedListener(MapsActivity.this) {
                    @Override
                    protected void onCommentPosted() {
                        pin.addComment(commentText);
                        commentDialogue.dismiss();
                        if (listener != null)
                            listener.onCommentPosted();
                    }
                });
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

//    @Override
//    protected void onResume() {
//        if (mMap != null ) {
//            readSharedPreference();
//        }
//        super.onResume();
//    }

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
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           enableMyLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

      
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

//        try {
//            View mapFragment = (View) findViewById(R.id.map);
//            assert mapFragment != null;
//            final ViewGroup parent = (ViewGroup) mapFragment.findViewWithTag("GoogleMapMyLocationButton").getParent();
//            parent.post(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        for (int i = 0, n = parent.getChildCount(); i < n; i++) {
//                            View view = parent.getChildAt(i);
//                            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) view.getLayoutParams();
//                            // position on right bottom
//                            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//                            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
//                            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                            rlp.rightMargin = rlp.leftMargin;
//                            rlp.bottomMargin = 25;
//                            view.requestLayout();
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        readSharedPreference();
    }

    @Override
    public void onMapClick(final LatLng point) {
        if (pincolor == -1) return;
        if (!pinChosen) return;
        lstLatLng.add(point);
        showCommentDialogueBox(new Pin(pinType, point.latitude, point.longitude));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.pin_info_window, null);
        TextView infoPinType = view.findViewById(R.id.info_pin_type);
        TextView infoComment = view.findViewById(R.id.info_comment);

        Pin pin = (Pin) marker.getTag();
        infoPinType.setText(pin.getType().toString());

        List<String> comments = pin.getComments();
        if (comments.isEmpty()) {
            infoComment.setVisibility(View.GONE);
        } else {
            infoComment.setVisibility(View.VISIBLE);
            infoComment.setText(comments.get(0));
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.circle)));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();


        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(17).bearing(90).tilt(40).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                } else {
                   mPermissionDenied = true;
                }
            }

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

    private void showTutorSequence(int millis) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(millis);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(mSwitch)
                        .setMaskColour(Color.argb(200, 252,98,98))
                        .setTitleTextColor(Color.WHITE)
                        .setContentTextColor(Color.WHITE)
                        .setTitleText("Switch Map Modes")
                        .setDismissText("GOT IT")
                        .setContentText("Use this switch to change between different map modes - pin mode and the general safety mode")
                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(pinsMenu)
                        .setMaskColour(Color.argb(200, 252,98,98))
                        .setTitleTextColor(Color.WHITE)
                        .setContentTextColor(Color.WHITE)
                        .setTitleText("Pin Menu")
                        .setDismissText("GOT IT")
                        .setContentText("Press on the menu to see different types of pins")
                        .withCircleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(pSwitch)
                        .setMaskColour(Color.argb(200, 252,98,98))
                        .setTitleTextColor(Color.WHITE)
                        .setContentTextColor(Color.WHITE)
                        .setTitleText("Switch Pin Modes")
                        .setDismissText("IM READY TO USE THIS APP")
                        .setContentText("Use this switch to switch between colored pins and shaped pins")
                        .withCircleShape()
                        .build()
        );

        sequence.start();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Pin pin = (Pin) marker.getTag();
        showAllCommentsBox(pin);
    }

    public void readSharedPreference() {
        int pinMode = sharedPreferences.getInt("ChangeIcons", 1);
        if (pinMode == SettingsActivity.COLOURS_MODE) {
            pinsMenu.setImageResource(R.drawable.pick_pocket);
            setAllPinsMode(PinMode.COLOUR);
            setMode(PinMode.COLOUR);
        } else {
            pinsMenu.setImageResource(R.drawable.wallpin);
            setAllPinsMode(PinMode.ICON);
            setMode(PinMode.ICON);
        }

//        int pickPocketColor = sharedPreferences.getInt("pick_pocket", 15029588);
//        DrawableCompat.setTint(MapsActivity.this.getResources().getDrawable(colours.get(0)), pickPocketColor);
//
//        int drunkColor = sharedPreferences.getInt("drunk", 15029588);
//        DrawableCompat.setTint(MapsActivity.this.getResources().getDrawable(colours.get(1)), drunkColor);
//
//        int robberyColor = sharedPreferences.getInt("robbery", 15029588);
//        DrawableCompat.setTint(MapsActivity.this.getResources().getDrawable(colours.get(2)), robberyColor);
//
//        int scamColor = sharedPreferences.getInt("scam", 15029588);
//        DrawableCompat.setTint(MapsActivity.this.getResources().getDrawable(colours.get(3)), scamColor);
//
//        int harrassmentColor = sharedPreferences.getInt("harrassment", 15029588);
//        DrawableCompat.setTint(MapsActivity.this.getResources().getDrawable(colours.get(4)), harrassmentColor);
//
//        int othersColor = sharedPreferences.getInt("others", 15029588);
//        DrawableCompat.setTint(MapsActivity.this.getResources().getDrawable(colours.get(5)), othersColor);
    }
}
