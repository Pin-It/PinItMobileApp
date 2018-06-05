package com.pinit.api;

import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.pinit.api.errors.APIError;
import com.pinit.api.errors.UnknownError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JSONRequestBuilder<T> {
    private RequestQueue requestQueue;
    private String url;
    private Map<String, String> headers;
    private T jsonData;
    private NetworkListener<T> listener;
    private int method = Request.Method.GET;  // user GET method by default

    private final boolean returnsJSONObject;

    private Response.Listener<T> responseListener;
    private Response.ErrorListener errorListener;
    private boolean blocking = false;

    private JSONRequestBuilder(RequestQueue requestQueue, boolean returnsJSONObject) {
        this.requestQueue = requestQueue;
        this.returnsJSONObject = returnsJSONObject;

        responseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (listener != null) listener.onReceive(response);
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onError(APIError.fromVolleyError(error));
                }
            }
        };
    }

    public static JSONRequestBuilder<JSONObject> newJSONObjectRequest(RequestQueue requestQueue) {
        return new JSONRequestBuilder<>(requestQueue, true);
    }

    public static JSONRequestBuilder<JSONArray> newJSONArrayRequest(RequestQueue requestQueue) {
        return new JSONRequestBuilder<>(requestQueue, false);
    }

    public JSONRequestBuilder<T> withURL(String url) {
        this.url = url;
        return this;
    }

    public JSONRequestBuilder<T> withMethod(int method) {
        this.method = method;
        return this;
    }

    public JSONRequestBuilder<T> withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public JSONRequestBuilder<T> withJSONData(T jsonData) {
        this.jsonData = jsonData;
        return this;
    }

    public JSONRequestBuilder<T> withNetworkListener(NetworkListener<T> listener) {
        this.listener = listener;
        return this;
    }

    public JSONRequestBuilder<T> setBlocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }

    public void send() {
        check();
        if (returnsJSONObject) {
            sendJSONObjectRequest();
        } else {
            sendJSONArrayRequest();
        }
    }

    private void check() {
        if (requestQueue == null) {
            throw new IllegalArgumentException("Request queue must not be null");
        }
        if (url == null) {
            throw new IllegalArgumentException("URL must not be null");
        }
    }

    @SuppressWarnings("unchecked")
    private void sendJSONObjectRequest() {
        if (blocking) {
            createBlockingJSONObjectRequest();
        } else {
            sendNonBlockingJSONObjectRequest();
        }
    }

    @SuppressWarnings("unchecked")
    private void createBlockingJSONObjectRequest() {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(method, url, (JSONObject) jsonData, future, future);
        requestQueue.add(request);
        handleRequestFuture((RequestFuture<T>) future);
    }

    @SuppressWarnings("unchecked")
    private void sendNonBlockingJSONObjectRequest() {
        Response.Listener<JSONObject> jsonObjectListener = (Response.Listener<JSONObject>) responseListener;
        JsonObjectRequest request = new JsonObjectRequest(method, url, (JSONObject) jsonData, jsonObjectListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void sendJSONArrayRequest() {
        if (blocking) {
            sendBlockingJSONArrayRequest();
        } else {
            sendNonBlockingJSONArrayRequest();
        }
    }

    @SuppressWarnings("unchecked")
    private void sendBlockingJSONArrayRequest() {
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(method, url, (JSONArray) jsonData, future, future);
        requestQueue.add(request);
        handleRequestFuture((RequestFuture<T>) future);
    }

    @SuppressWarnings("unchecked")
    private void sendNonBlockingJSONArrayRequest() {
        Response.Listener<JSONArray> jsonArrayListener = (Response.Listener<JSONArray>) responseListener;
        Request<JSONArray> request = new JsonArrayRequest(method, url, (JSONArray) jsonData, jsonArrayListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void handleRequestFuture(RequestFuture<T> future) {
        try {
            future.get();
            listener.onReceive(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            if (e.getCause() instanceof VolleyError) {
                VolleyError volleyError = (VolleyError) e.getCause();
                listener.onError(APIError.fromVolleyError(volleyError));
            } else {
                listener.onError(new UnknownError(e.getMessage()));
            }
        }
    }
}
