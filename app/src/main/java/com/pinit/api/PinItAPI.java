package com.pinit.api;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.pinit.api.models.Pin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PinItAPI {
    private static final String API_URL = "https://pin-it-app.herokuapp.com/api/";
    private static final String PINS_URL = API_URL + Pin.API_ENDPOINT + "/";

    public static void getAllPins(RequestQueue requestQueue, final NetworkListener<List<Pin>> listener) {
        NetworkUtils.requestJSONArray(requestQueue, PINS_URL, new NetworkListener<JSONArray>() {
            @Override
            public void onReceive(JSONArray response) {
                List<Pin> pins = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        pins.add(new Pin(obj));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onReceive(pins);
            }

            @Override
            public void onError(VolleyError error) {
                listener.onError(error);
            }
        });
    }

    public static void uploadNewPin(RequestQueue requestQueue, Pin pin) {
        uploadNewPin(requestQueue, pin, null);
    }

    public static void uploadNewPin(RequestQueue requestQueue, Pin pin, NetworkListener<JSONObject> listener) {
        NetworkUtils.postJSONObject(requestQueue, PINS_URL, pin.toJSONObject(), listener);
    }
}
