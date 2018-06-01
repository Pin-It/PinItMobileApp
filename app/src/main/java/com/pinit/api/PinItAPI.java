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
  public static final String API_URL = "https://pin-it-app.herokuapp.com/api/";

  public static void getAllPins(RequestQueue requestQueue, final NetworkListener<List<Pin>> listener) {
    String url = API_URL + Pin.API_ENDPOINT + "/";
    NetworkUtils.requestJSONArray(requestQueue, url, new NetworkListener<JSONArray>() {
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
}
