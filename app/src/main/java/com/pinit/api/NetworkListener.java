package com.pinit.api;

import com.android.volley.VolleyError;

public interface NetworkListener<T> {
  void onReceive(T response);

  void onError(VolleyError error);
}
