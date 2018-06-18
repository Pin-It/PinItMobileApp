package com.pinit.api.errors;

import com.android.volley.NetworkResponse;

public class HTTPClientError extends APIError {
    private final NetworkResponse networkResponse;

    public HTTPClientError(Throwable cause, NetworkResponse networkResponse) {
        super(cause);
        this.networkResponse = networkResponse;
    }

    public NetworkResponse getNetworkResponse() {
        return networkResponse;
    }
}
