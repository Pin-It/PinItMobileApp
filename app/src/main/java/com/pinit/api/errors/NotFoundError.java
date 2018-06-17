package com.pinit.api.errors;

import com.android.volley.NetworkResponse;

public class NotFoundError extends HTTPClientError {
    public static final int STATUS_CODE = 404;

    public NotFoundError(Throwable cause, NetworkResponse networkResponse) {
        super(cause, networkResponse);
    }
}
