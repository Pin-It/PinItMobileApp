package com.pinit.api.errors;

public class UnknownError extends APIError {
    public UnknownError(Throwable cause) {
        super(cause);
    }
}
