package com.pinit.api;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class NetworkUtils {
  public static void requestJSONObject(RequestQueue requestQueue, int method, String url, JSONObject jsonRequest, final NetworkListener<JSONObject> listener) {
    Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        if (listener != null) listener.onReceive(response);
      }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        if (listener != null) listener.onError(error);
      }
    };

    JsonObjectRequest request = new JsonObjectRequest(method, url, jsonRequest, responseListener, errorListener);
    requestQueue.add(request);
  }

  public static void requestJSONArray(RequestQueue requestQueue, String url, final NetworkListener<JSONArray> listener) {
    Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        if (listener != null) listener.onReceive(response);
      }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        if (listener != null) listener.onError(error);
      }
    };

    JsonArrayRequest request = new JsonArrayRequest(url, responseListener, errorListener);
    requestQueue.add(request);
  }

  public static void getJSONObject(RequestQueue requestQueue, String url, final NetworkListener<JSONObject> listener) {
    requestJSONObject(requestQueue, Request.Method.GET, url, null, listener);
  }

  public static void postJSONObject(RequestQueue requestQueue, String url, JSONObject jsonRequest, final NetworkListener<JSONObject> listener) {
    requestJSONObject(requestQueue, Request.Method.POST, url, jsonRequest, listener);
  }

  public static void postJSONObject(RequestQueue requestQueue, String url, JSONObject jsonRequest) {
    postJSONObject(requestQueue, url, jsonRequest, null);
  }
}
