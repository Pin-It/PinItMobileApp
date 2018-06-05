package com.pinit.api.errors;

public class NotFoundError extends HTTPClientError {
    public static final int STATUS_CODE = 404;

    public NotFoundError(String message) {
        super(message);
    }
}
