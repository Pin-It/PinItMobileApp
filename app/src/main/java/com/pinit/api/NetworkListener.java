package com.pinit.api;

import com.pinit.api.errors.APIError;

public interface NetworkListener<T> {
    void onReceive(T response);

    void onError(APIError error);
}
