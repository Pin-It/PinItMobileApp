package com.pinit.api.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CommentTest {
    @Test
    public void canConstructWithJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("pin", 10);
        json.put("text", "test comment");

        Comment comment = new Comment(json);
        assertThat(comment.getPinId(), is(10));
        assertThat(comment.getText(), is("test comment"));
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
