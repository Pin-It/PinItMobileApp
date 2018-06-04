package com.pinit.api;

import com.android.volley.VolleyError;

public interface LoginListener {
    void onSuccess(String token);

    void onNetworkError(VolleyError error);

    void onCredentialsError();
}
