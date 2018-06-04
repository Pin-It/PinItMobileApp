package com.pinit.api;

import com.android.volley.VolleyError;

public interface LoginListener {
    void onSuccess();

    void onNetworkError(VolleyError error);

    void onCredentialsError();
}
