package com.pinit.api.listeners;

import com.pinit.api.errors.APIError;

public interface LoginListener {
    void onSuccess(String token);

    void onNetworkError(APIError error);

    void onCredentialsError();
}
