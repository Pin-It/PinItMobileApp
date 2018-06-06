package com.pinit.api.errors;

public class BadRequestError extends HTTPClientError {
    public static final int STATUS_CODE = 400;

    public BadRequestError(String message) {
        super(message);
    }
}
