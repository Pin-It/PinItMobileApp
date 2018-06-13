package com.pinit.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Like implements Model {
    public static final String API_ENDPOINT = "likes";
    private int pinId;
    private String text;

    private static final String KEY_PIN_ID = "pin";

    public Like(JSONObject json) {
        if (json == null) {
            throw new IllegalArgumentException("Cannot initialize Like object from null");
        }

        try {
            this.pinId = json.getInt(KEY_PIN_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Malformed JSON, incompatible with Like model");
        }
    }

    public Like(Pin pin) {
        this(pin.getId());
    }

    public Like(int pinId) {
        this.pinId = pinId;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put(KEY_PIN_ID, this.pinId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        return "Like on Pin " + pinId;
    }

    public int getPinId() {
        return pinId;
    }

    public String getText() {
        return text;
    }
}
