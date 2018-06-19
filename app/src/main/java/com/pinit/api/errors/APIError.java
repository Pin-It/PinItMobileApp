package com.pinit.api.errors;

import com.android.volley.*;

public abstract class APIError extends Exception {
    public APIError(Throwable cause) {
        super(cause);
    }

    public APIError(String message) {
        super(message);
    }

    public static APIError fromVolleyError(VolleyError error) {
        if (error instanceof ClientError || error instanceof AuthFailureError) {
            NetworkResponse response = error.networkResponse;
            switch (response.statusCode) {
                case BadRequestError.STATUS_CODE:
                    return new BadRequestError(error, error.networkResponse);
                case UnauthorizedError.STATUS_CODE:
                    return new UnauthorizedError(error, error.networkResponse);
                case ForbiddenError.STATUS_CODE:
                    return new ForbiddenError(error, error.networkResponse);
                case NotFoundError.STATUS_CODE:
                    return new NotFoundError(error, error.networkResponse);
                default:
                    return new HTTPClientError(error, error.networkResponse);
            }
        } else if (error instanceof NoConnectionError) {
            return new NotConnectedError(error);
        }
        return new UnknownError(error);
    }
}
