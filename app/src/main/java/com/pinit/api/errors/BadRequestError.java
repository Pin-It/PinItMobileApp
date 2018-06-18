package com.pinit.api.errors;

import com.android.volley.NetworkResponse;

public class BadRequestError extends HTTPClientError {
    public static final int STATUS_CODE = 400;

    public BadRequestError(Throwable cause, NetworkResponse networkResponse) {
        super(cause, networkResponse);
    }
}
