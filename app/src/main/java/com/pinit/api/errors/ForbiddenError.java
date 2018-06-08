package com.pinit.api.errors;

import com.android.volley.NetworkResponse;

public class ForbiddenError extends HTTPClientError {
    public static final int STATUS_CODE = 403;

    public ForbiddenError(String message, NetworkResponse networkResponse) {
        super(message, networkResponse);
    }
}
