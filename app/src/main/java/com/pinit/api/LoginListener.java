package com.pinit.api;

import com.pinit.api.errors.APIError;

public interface LoginListener {
    void onSuccess(String token);

    void onNetworkError(APIError error);

    void onCredentialsError();
}
