package com.pinit.api.errors;

import com.android.volley.NetworkResponse;

public class UnauthorizedError extends HTTPClientError {
    public static final int STATUS_CODE = 401;

    public UnauthorizedError(String message, NetworkResponse networkResponse) {
        super(message, networkResponse);
    }
}
