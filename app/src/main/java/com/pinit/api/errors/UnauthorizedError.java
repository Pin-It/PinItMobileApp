package com.pinit.api.errors;

public class UnauthorizedError extends HTTPClientError {
    public static final int STATUS_CODE = 401;

    public UnauthorizedError(String message) {
        super(message);
    }
}
