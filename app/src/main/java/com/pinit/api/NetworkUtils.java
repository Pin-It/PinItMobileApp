package com.pinit.api;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class NetworkUtils {
  public static void requestJSONObject(RequestQueue requestQueue, String url, final NetworkListener<JSONObject> listener) {
    Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        listener.onReceive(response);
      }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        listener.onError(error);
      }
    };

    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);
    requestQueue.add(request);
  }

  public static void requestJSONArray(RequestQueue requestQueue, String url, final NetworkListener<JSONArray> listener) {
    Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        listener.onReceive(response);
      }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        listener.onError(error);
      }
    };

    JsonArrayRequest request = new JsonArrayRequest(url, responseListener, errorListener);
    requestQueue.add(request);
  }
}
