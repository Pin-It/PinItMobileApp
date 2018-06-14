package com.pinit.api.listeners;

import com.pinit.api.errors.APIError;

public interface NetworkListener<T> {
    void onReceive(T response);

    void onError(APIError error);
}
