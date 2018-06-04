package com.pinit.api;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.pinit.api.models.Pin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pinit.api.JSONRequestBuilder.newJSONArrayRequest;
import static com.pinit.api.JSONRequestBuilder.newJSONObjectRequest;

public class PinItAPI {
    private static final String BASE_URL = "https://pin-it-app.herokuapp.com/";
    private static final String API_URL = BASE_URL + "api/";
    private static final String PINS_URL = API_URL + Pin.API_ENDPOINT + "/";
    private static final String TOKEN_AUTH_URL = BASE_URL + "api-token-auth/";

    private static final String TOKEN_FIELD = "token";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    private RequestQueue requestQueue;
    private String token;

    public PinItAPI(RequestQueue requestQueue) {
        this(requestQueue, null);
    }

    public PinItAPI(RequestQueue requestQueue, String token) {
        this.requestQueue = requestQueue;
        this.token = token;
    }

    public void login(String email, String password, final LoginListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put(USERNAME_FIELD, email);
            json.put(PASSWORD_FIELD, password);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        getNewJSONObjectRequest()
                .withMethod(Request.Method.POST)
                .withURL(TOKEN_AUTH_URL)
                .withJSONData(json)
                .withNetworkListener(new NetworkListener<JSONObject>() {
                    @Override
                    public void onReceive(JSONObject response) {
                        if (response.has(TOKEN_FIELD)) {
                            try {
                                token = response.getString(TOKEN_FIELD);
                                if (listener != null) listener.onSuccess();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        } else {
                            // Wrong email/password combination
                            if (listener != null) listener.onCredentialsError();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        if (listener != null) listener.onNetworkError(error);
                    }
                })
                .send();
    }

    /**
     * Gets a list of all pins from the server.
     * @param listener this listener is needed to return the result of the request (a list of pins)
     */
    public void getAllPins(final NetworkListener<List<Pin>> listener) {
        getNewJSONArrayRequest()
                .withMethod(Request.Method.GET)
                .withURL(PINS_URL)
                .withNetworkListener(new NetworkListener<JSONArray>() {
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
                })
                .send();
    }

    /**
     * Uploads a new pin to the server
     * @param pin the pin to be uploaded
     * @param listener (optional) listener for the result of the POST request
     */
    public void uploadNewPin(Pin pin, NetworkListener<JSONObject> listener) {
        getNewJSONObjectRequest()
                .withMethod(Request.Method.POST)
                .withURL(PINS_URL)
                .withJSONData(pin.toJSONObject())
                .withNetworkListener(listener)
                .send();
    }

    public void uploadNewPin(Pin pin) {
        uploadNewPin(pin, null);
    }

    private JSONRequestBuilder<JSONObject> getNewJSONObjectRequest() {
        return newJSONObjectRequest(requestQueue).withHeaders(getHeaders());
    }

    private JSONRequestBuilder<JSONArray> getNewJSONArrayRequest() {
        return newJSONArrayRequest(requestQueue).withHeaders(getHeaders());
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (token != null) {
            headers.put("Authorization", "Token " + token);
        }
        return headers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
