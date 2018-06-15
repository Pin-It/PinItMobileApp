package com.pinit.api.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

public class PinTest {
    private JSONObject validJSON;
    private JSONObject invalidJSON;
    private JSONObject negativeIdJSON;
    private JSONObject nullJSON;

    @Before
    public void setUp() throws JSONException {
        validJSON = new JSONObject();
        validJSON.put("id", 10);
        validJSON.put("pin_type", 1);
        validJSON.put("latitude", 123.456);
        validJSON.put("longitude", 0.65432);
        validJSON.put("comments", new JSONArray(new String[]{"comment 1", "comment 2"}));
        validJSON.put("likes", 5);
        validJSON.put("created_at", "2018-06-06T16:26:57.141474Z");

        invalidJSON = new JSONObject();
        invalidJSON.put("wrong key", "wrong value");

        negativeIdJSON = new JSONObject(validJSON.toString());
        negativeIdJSON.put("id", -10);

        nullJSON = null;
    }

    @Test
    public void canConstructWithJSONObject() {
        Pin pin = new Pin(validJSON);
        assertThat(pin.getId(), is(10));
        assertThat(pin.getType(), is(Pin.Type.PICKPOCKET));
        assertThat(pin.getLatitude(), is(123.456));
        assertThat(pin.getLongitude(), is(0.65432));
        assertThat(pin.getCommentCount(), is(2));
        assertThat(pin.getComments(), is(Arrays.asList("comment 1", "comment 2")));
        assertThat(pin.getLikes(), is(5));

        Calendar createdAt = pin.getCreatedAt();
        createdAt.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertThat(createdAt.get(Calendar.YEAR), is(2018));
        assertThat(createdAt.get(Calendar.MONTH), is(Calendar.JUNE));
        assertThat(createdAt.get(Calendar.DAY_OF_MONTH), is(Calendar.JUNE));
        assertThat(createdAt.get(Calendar.HOUR_OF_DAY), is(16));
        assertThat(createdAt.get(Calendar.MINUTE), is(26));
    }

    @Test
    public void throwsExceptionWhenConstructingPinWithInvalidJSONObject() {
        try {
            new Pin(invalidJSON);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // As expected
        }
    }

    @Test
    public void throwsExceptionWhenConstructingPinWithNull() {
        try {
            new Pin(nullJSON);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // As expected
        }
    }

    @Test
    public void idIsNotValidIfIdNeverSpecified() {
        Pin pin = new Pin(Pin.Type.PICKPOCKET, 1.2345, 2.22222);
        assertFalse(pin.idIsValid());
    }

    @Test
    public void nonNegativeIdsAreValid() {
        Pin validPin = new Pin(validJSON);
        assertThat(validPin.getId(), greaterThanOrEqualTo(0));
        assertTrue(validPin.idIsValid());

        Pin invalidPin = new Pin(negativeIdJSON);
        assertThat(invalidPin.getId(), lessThan(0));
        assertFalse(invalidPin.idIsValid());
    }
}
