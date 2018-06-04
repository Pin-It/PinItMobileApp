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
    private static final String BASE_URL = "https://pin-it-app.herokuapp.com/";
    private static final String API_URL = BASE_URL + "api/";
    private static final String PINS_URL = API_URL + Pin.API_ENDPOINT + "/";
    private static final String TOKEN_AUTH_URL = BASE_URL + "api-token-auth/";

    private static final String TOKEN_FIELD = "token";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    private String token;

    public PinItAPI() {
        this(null);
    }

    public PinItAPI(String token) {
        this.token = token;
    }

    public void login(RequestQueue requestQueue, String email, String password, final LoginListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put(USERNAME_FIELD, email);
            json.put(PASSWORD_FIELD, password);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        NetworkUtils.postJSONObject(requestQueue, TOKEN_AUTH_URL, json, new NetworkListener<JSONObject>() {
            @Override
            public void onReceive(JSONObject response) {
                if (response.has(TOKEN_FIELD)) {
                    try {
                        token = response.getString(TOKEN_FIELD);
                        listener.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                } else {
                    // Wrong email/password combination
                    listener.onCredentialsError();
                }
            }

            @Override
            public void onError(VolleyError error) {
                listener.onNetworkError(error);
            }
        });
    }

    /**
     * Gets a list of all pins from the server.
     * @param requestQueue Volley request queue
     * @param listener this listener is needed to return the result of the request (a list of pins)
     */
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

    /**
     * Uploads a new pin to the server
     * @param requestQueue Volley request queue
     * @param pin the pin to be uploaded
     * @param listener (optional) listener for the result of the POST request
     */
    public static void uploadNewPin(RequestQueue requestQueue, Pin pin, NetworkListener<JSONObject> listener) {
        NetworkUtils.postJSONObject(requestQueue, PINS_URL, pin.toJSONObject(), listener);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
