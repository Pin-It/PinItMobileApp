package com.pinit.api.errors;

import com.android.volley.NetworkResponse;

public class UnauthorizedError extends HTTPClientError {
    public static final int STATUS_CODE = 401;

    public UnauthorizedError(Throwable cause, NetworkResponse networkResponse) {
        super(cause, networkResponse);
    }
}
