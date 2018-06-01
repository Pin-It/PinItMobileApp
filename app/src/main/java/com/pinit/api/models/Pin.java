package com.pinit.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Pin {
  public static final String API_ENDPOINT = "pins";

  private static final String KEY_TYPE = "pin_type";
  private static final String KEY_LATITUDE = "latitude";
  private static final String KEY_LONGITUDE = "longitude";

  private Type type;
  private double latitude;
  private double longitude;

  public Pin(JSONObject json) {
    if (json == null) {
      throw new IllegalArgumentException("Cannot initialize Pin object from null");
    }

    try {
      this.type = Type.fromInt(json.getInt(KEY_TYPE));
      this.latitude = json.getDouble(KEY_LATITUDE);
      this.longitude = json.getDouble(KEY_LONGITUDE);
    } catch (JSONException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Malformed JSON, incompatible with Pin model");
    }
  }

  public Pin(Type type, double latitude, double longitude) {
    this.type = type;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public Type getType() {
    return type;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  @Override
  public String toString() {
    return type.toString() + "@(" + latitude + ", " + longitude + ")";
  }

  public enum Type {
    PICKPOCKET, DRUNK, ROBBERY, SCAM, HARASSMENT, OTHERS;

    public static Type fromInt(int i) {
      return Type.values()[i - 1];
    }
  }
}
