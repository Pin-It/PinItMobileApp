package com.pinit.api.errors;

public class ForbiddenError extends HTTPClientError {
    public static final int STATUS_CODE = 403;

    public ForbiddenError(String message) {
        super(message);
    }
}
