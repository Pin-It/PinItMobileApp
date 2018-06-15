package com.pinit.api.models;

import com.google.gson.internal.bind.util.ISO8601Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.*;

public class Pin implements Model {
    public static final String API_ENDPOINT = "pins";

    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "pin_type";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_LIKES = "likes";
    private static final String KEY_CREATED_AT = "created_at";

    private int id;
    private Type type;
    private double latitude;
    private double longitude;
    private List<String> comments = new ArrayList<>();
    private int likes;
    private Calendar createdAt;

    public Pin(JSONObject json) {
        if (json == null) {
            throw new IllegalArgumentException("Cannot initialize Pin object from null");
        }

        try {
            this.id = json.getInt(KEY_ID);
            this.type = Type.fromInt(json.getInt(KEY_TYPE));
            this.latitude = json.getDouble(KEY_LATITUDE);
            this.longitude = json.getDouble(KEY_LONGITUDE);
            this.likes = json.getInt(KEY_LIKES);
            this.createdAt = parseTime(json.getString(KEY_CREATED_AT));

            JSONArray commentsJSONArray = json.getJSONArray(KEY_COMMENTS);
            for (int i = 0; i < commentsJSONArray.length(); i++) {
                this.comments.add(commentsJSONArray.getString(i));
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Malformed JSON, incompatible with Pin model");
        }
    }

    private Calendar parseTime(String string) throws ParseException {
        Date date = ISO8601Utils.parse(string, new ParsePosition(0));
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
    }

    public Pin(Type type, double latitude, double longitude) {
        this.id = -1;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likes = 0;
        this.createdAt = (Calendar) Calendar.getInstance().clone();
    }

    public boolean idIsValid() {
        return id > -1;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        try {
            if (idIsValid()) json.put(KEY_ID, this.id);
            json.put(KEY_TYPE, this.type.toInt());
            json.put(KEY_LATITUDE, this.latitude);
            json.put(KEY_LONGITUDE, this.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        return type.toString() + "@(" + latitude + ", " + longitude + ")";
    }

    public int getId() {
        return id;
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

    public List<String> getComments() {
        return comments;
    }

    public int getLikes() {
        return likes;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void addComment(String commentText) {
        comments.add(commentText);
    }

    public int getCommentCount() {
        return comments.size();
    }

    public void incrementLikes() {
        likes++;
    }

    public void decrementLikes() {
        likes--;
    }

    public enum Type {
        PICKPOCKET, DRUNK, ROBBERY, SCAM, HARASSMENT, OTHERS;

        public static Type fromInt(int i) {
            return Type.values()[i - 1];
        }

        public int toInt() {
            return ordinal() + 1;
        }
    }
}
