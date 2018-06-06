package com.pinit.api.errors;

import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

public abstract class APIError extends Exception {
    public APIError(String message) {
        super(message);
    }

    public static APIError fromVolleyError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (error instanceof ClientError) {
            switch (response.statusCode) {
                case BadRequestError.STATUS_CODE:
                    return new BadRequestError(error.getMessage());
                case UnauthorizedError.STATUS_CODE:
                    return new UnauthorizedError(error.getMessage());
                case ForbiddenError.STATUS_CODE:
                    return new ForbiddenError(error.getMessage());
                case NotFoundError.STATUS_CODE:
                    return new NotFoundError(error.getMessage());
                default:
                    return new HTTPClientError(error.getMessage());
            }
        } else if (error instanceof NoConnectionError) {
            return new NotConnectedError(error.getMessage());
        }
        return new UnknownError(error.getMessage());
    }
}
