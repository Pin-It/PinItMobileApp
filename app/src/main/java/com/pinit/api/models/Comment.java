package com.pinit.api.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment implements Model {
    public static final String API_ENDPOINT = "comments";
    private int pinId;
    private String text;

    private static final String KEY_PIN_ID = "pin";
    private static final String KEY_TEXT = "text";

    public Comment(JSONObject json) {
        if (json == null) {
            throw new IllegalArgumentException("Cannot initialize Comment object from null");
        }

        try {
            this.text = json.getString(KEY_TEXT);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Malformed JSON, incompatible with Pin model");
        }
    }

    public Comment(int pinId, String text) {
        this.pinId = pinId;
        this.text = text;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put(KEY_PIN_ID, this.pinId);
            json.put(KEY_TEXT, this.text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        return "Comment on Pin " + pinId + ": " + text;
    }
}
