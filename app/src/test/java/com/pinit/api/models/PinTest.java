package com.pinit.api.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class PinTest {
    @Test
    public void canConstructWithJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", 10);
        json.put("pin_type", 1);
        json.put("latitude", 123.456);
        json.put("longitude", 0.65432);

        Pin pin = new Pin(json);
        assertThat(pin.getId(), is(10));
        assertThat(pin.getType(), is(Pin.Type.PICKPOCKET));
        assertThat(pin.getLatitude(), is(123.456));
        assertThat(pin.getLongitude(), is(0.65432));
    }

    @Test
    public void throwsExceptionWhenConstructingPinWithInvalidJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("wrong key", "wrong value");

        try {
            new Pin(json);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // As expected
        }
    }

    @Test
    public void throwsExceptionWhenConstructingPinWithNull() {
        try {
            new Pin(null);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // As expected
        }
    }
}
