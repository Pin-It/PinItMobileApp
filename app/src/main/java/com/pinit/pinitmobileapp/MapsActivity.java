package com.pinit.pinitmobileapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pinit.api.NetworkListener;
import com.pinit.api.PinItAPI;
import com.pinit.api.models.Pin;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String USER_TOKEN = "userToken";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private List<LatLng> lstLatLng = new ArrayList<LatLng>();
    private SwitchCompat mSwitch;
    private SwitchCompat pSwitch;

    FloatingActionButton PinsMenu, circlepin, extraflagpin, flagpin, starpin, wallpin, checkpin;
    private List<AppCompatButton> pinsList = new ArrayList<>();
    private List<Integer> colours = new ArrayList<>();
    private List<Integer> icons = new ArrayList<>();
    private List<Pin> allPins = new ArrayList<>();

    int pincolor = -1;
    int pinshape = -1;
    private Pin.Type pinType;
    private PinItAPI api;
    public PinMode currentMode = PinMode.ICON;

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

        icons.add(R.drawable.circle);
        icons.add(R.drawable.checkpin);
        icons.add(R.drawable.extraflagpin);
        icons.add(R.drawable.flagpin);
        icons.add(R.drawable.starpin);
        icons.add(R.drawable.wallpin);

        setAllPinsVisibility(false, null);

        mSwitch = (SwitchCompat) findViewById(R.id.switch_maps);
        mSwitch.setChecked(true);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mSwitch.isChecked()) {
                    Intent newIntent = new Intent(MapsActivity.this, GeneralySafetyMapActivity.class);
                    startActivity(newIntent);
                    finish();
                }
            }
        });

        PinsMenu = (FloatingActionButton) findViewById(R.id.switchPinButton);

        pSwitch = (SwitchCompat) findViewById(R.id.switch_pins);
        pSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    setMode(PinMode.COLOUR);
                    setAllPinsMode(PinMode.COLOUR);
                    PinsMenu.setImageResource(R.drawable.pinuno);
                } else {
                    setMode(PinMode.ICON);
                    setAllPinsMode(PinMode.ICON);
                    PinsMenu.setImageResource(R.drawable.wallpin);
                }
            }
        });


        PinsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPinsVisible()) {
                    setAllPinsVisibility(false,  null);
                    if (currentMode == PinMode.ICON) {
                        PinsMenu.setImageResource(R.drawable.wallpin);
                    } else {
                        PinsMenu.setImageResource(R.drawable.pinuno);
                    }
                } else {
                    setAllPinsVisibility(true,  null);
                    PinsMenu.setImageResource(R.drawable.cancel);
                }
            }
        });

        int i = 0;

        for (AppCompatButton bttn : pinsList) {
            bttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> list = currentMode == PinMode.COLOUR ? colours : icons;
                    pincolor = list.get(pinsList.indexOf((AppCompatButton) v));
                    pinType = Pin.Type.values()[pinsList.indexOf((AppCompatButton) v)];
                }
            });
        }
    }

    private void setMode(PinMode colour) {
        this.currentMode = colour;
    }

//    private void colorPinMode() {
////        pinsMenu = (FloatingActionButton) findViewById(R.id.pinListBttn);
////        pinsList.add((AppCompatButton) findViewById(R.id.pinunoBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.pincincoBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.pincuatroBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.pindosBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.pinseisBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.pintresBttn));
//
//        setAllPinsVisibility(false, null);
//        PinsMenu.setImageResource(R.drawable.pinuno);
//        setAllPinsMode(PinMode.COLOUR);
//
//        PinsMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isPinsVisible()) {
//                    setAllPinsVisibility(false, null);
//                    PinsMenu.setImageResource(R.drawable.pinuno);
//                } else {
//                    setAllPinsVisibility(true, null);
//                    PinsMenu.setImageResource(R.drawable.cancel);
//                }
//            }
//        });
//
//        for (final AppCompatButton bttn : pinsList) {
//            bttn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (bttn.getId() == R.id.pinunoBttn) {
//                        pincolor = R.drawable.pinuno;
//                        pinType = Pin.Type.PICKPOCKET;
//                    } else if (bttn.getId() == R.id.pindosBttn) {
//                        pincolor = R.drawable.pindos;
//                        pinType = Pin.Type.DRUNK;
//                    } else if (bttn.getId() == R.id.pintresBttn) {
//                        pincolor = R.drawable.pintres;
//                        pinType = Pin.Type.ROBBERY;
//                    } else if (bttn.getId() == R.id.pincuatroBttn) {
//                        pincolor = R.drawable.pincuatro;
//                        pinType = Pin.Type.SCAM;
//                    } else if (bttn.getId() == R.id.pincincoBttn) {
//                        pincolor = R.drawable.pincinco;
//                        pinType = Pin.Type.HARASSMENT;
//                    } else {
//                        pincolor = R.drawable.pinseis;
//                        pinType = Pin.Type.OTHERS;
//                    }
//
//                    setAllPinsVisibility(false, null);
//                    PinsMenu.setImageResource(R.drawable.pinuno);
//                }
//            });
//        }
//    }
//
//    private void shapePinMode() {
////        shapePinsMenu = (FloatingActionButton) findViewById(R.id.switchPinButton);
////        pinsList.add((AppCompatButton) findViewById(R.id.circlePinBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.checkPinBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.extraFlagPinBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.flagPinBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.starPinBttn));
////        pinsList.add((AppCompatButton) findViewById(R.id.wallPinBttn));
//
//        PinsMenu.setImageResource(R.drawable.pinuno);
//        setAllPinsMode(PinMode.ICON);
//        setAllPinsVisibility(false, null);
//
//        PinsMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isPinsVisible()) {
//                    setAllPinsVisibility(false,  null);
//                    PinsMenu.setImageResource(R.drawable.wallpin);
//                } else {
//                    setAllPinsVisibility(true,  null);
//                    PinsMenu.setImageResource(R.drawable.cancel);
//                }
//            }
//        });
//
//        for (final AppCompatButton bttn : pinsList) {
//            bttn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (bttn.getId() == R.id.circlePinBttn) {
//                        pincolor = R.drawable.circlepin;
//                        pinType = Pin.Type.PICKPOCKET;
//                    } else if (bttn.getId() == R.id.checkPinBttn) {
//                        pincolor = R.drawable.checkpin;
//                        pinType = Pin.Type.DRUNK;
//                    } else if (bttn.getId() == R.id.extraFlagPinBttn) {
//                        pincolor = R.drawable.extraflagpin;
//                        pinType = Pin.Type.ROBBERY;
//                    } else if (bttn.getId() == R.id.flagPinBttn) {
//                        pincolor = R.drawable.flagpin;
//                        pinType = Pin.Type.SCAM;
//                    } else if (bttn.getId() == R.id.starPinBttn) {
//                        pincolor = R.drawable.starpin;
//                        pinType = Pin.Type.HARASSMENT;
//                    } else {
//                        pincolor = R.drawable.wallpin;
//                        pinType = Pin.Type.OTHERS;
//                    }
//                }
//            });
//        }
//    }

//    private void showCommentDialogueBox() {
//        AlertDialog.Builder commentDialogueBuilder = new AlertDialog.Builder(MapsActivity.this);
//        View commentView = getLayoutInflater().inflate(R.layout.activity_add_comment, null);
//        LayoutInflater inflater = MapsActivity.this.getLayoutInflater();
//        TextView commentDialogueBoxTitle = commentView.findViewById(R.id.addComment);
//        EditText commentInputText = commentView.findViewById(R.id.comment_text_input);
//        AppCompatButton submitButton = commentView.findViewById(R.id.submit_comment);
//        AppCompatButton cancelButton = commentView.findViewById(R.id.cancel_button);
//
//        final AlertDialog commentDialogue = commentDialogueBuilder.create();
//
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                commentDialogue.dismiss();
//            }
//        });
//        commentDialogueBuilder.setView(inflater.inflate(R.layout.activity_add_comment, null));
//        commentDialogue.show();
//    }


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
                        mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(pincolor)).title("Newly added"));
                        Intent intent = new Intent(MapsActivity.this, AddCommentActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(VolleyError error) {
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
                    mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(color)).title(type.toString()));
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("MapReady", "click");
                Intent intent = new Intent(MapsActivity.this, AddCommentActivity.class);
                startActivity(intent);
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
}
