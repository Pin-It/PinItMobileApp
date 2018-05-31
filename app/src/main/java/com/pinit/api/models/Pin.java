package com.pinit.api.models;

public class Pin {
  private Type type;
  private double latitude;
  private double longitude;

  public Pin(Type type, double latitude, double longitude) {
    this.type = type;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return type.toString() + "@(" + latitude + ", " + longitude + ")";
  }

  public enum Type {
    PICKPOCKET, DRUNK, ROBBERY, SCAM, HARASSMENT, OTHERS
  }
}
