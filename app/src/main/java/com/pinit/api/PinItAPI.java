package com.pinit.api;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.pinit.api.errors.APIError;
import com.pinit.api.errors.BadRequestError;
import com.pinit.api.listeners.LoginListener;
import com.pinit.api.listeners.NetworkListener;
import com.pinit.api.listeners.PinLikedByMeListener;
import com.pinit.api.models.Comment;
import com.pinit.api.models.Like;
import com.pinit.api.models.Pin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pinit.api.JSONRequestBuilder.newJSONArrayRequest;
import static com.pinit.api.JSONRequestBuilder.newJSONObjectRequest;

public class PinItAPI {
    private static final String BASE_URL = "https://pin-it-app.herokuapp.com/";
    private static final String API_URL = BASE_URL + "api/";
    private static final String PINS_URL = API_URL + Pin.API_ENDPOINT + "/";
    private static final String COMMENTS_URL = API_URL + Comment.API_ENDPOINT + "/";
    private static final String LIKES_URL = API_URL + Like.API_ENDPOINT + "/";
    private static final String TOKEN_AUTH_URL = BASE_URL + "api-token-auth/";

    private static final String TOKEN_FIELD = "token";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    private static PinItAPI instance;

    private RequestQueue requestQueue;
    private String token;

    private PinItAPI() {
    }

    public static PinItAPI getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("Must pass in request queue the first time calling getInstance");
        }
        return instance;
    }

    public static PinItAPI getInstance(RequestQueue requestQueue) {
        if (instance == null) {
            instance = new PinItAPI();
        }
        if (instance.requestQueue == null) {
            instance.requestQueue = requestQueue;
        }
        return instance;
    }

    public void login(String email, String password, final LoginListener listener) {
        login(email, password, true, listener);
    }

    public void login(String email, String password, boolean blocking, final LoginListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put(USERNAME_FIELD, email);
            json.put(PASSWORD_FIELD, password);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        getNewJSONObjectRequest()
                .withMethod(Request.Method.POST)
                .withURL(TOKEN_AUTH_URL)
                .withJSONData(json)
                .setBlocking(blocking)
                .withNetworkListener(new NetworkListener<JSONObject>() {
                    @Override
                    public void onReceive(JSONObject response) {
                        if (response.has(TOKEN_FIELD)) {
                            try {
                                token = response.getString(TOKEN_FIELD);
                                if (listener != null) listener.onSuccess(token);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        } else {
                            // Wrong email/password combination
                            if (listener != null) listener.onCredentialsError();
                        }
                    }

                    @Override
                    public void onError(APIError error) {
                        if (listener != null) {
                            if (error instanceof BadRequestError) {
                                listener.onCredentialsError();
                            } else {
                                listener.onNetworkError(error);
                            }
                        }
                    }
                })
                .send();
    }

    /**
     * Gets a list of all pins from the server.
     * @param listener this listener is needed to return the result of the request (a list of pins)
     */
    public void getAllPins(final NetworkListener<List<Pin>> listener) {
        getNewJSONArrayRequest()
                .withMethod(Request.Method.GET)
                .withURL(PINS_URL)
                .withNetworkListener(new NetworkListener<JSONArray>() {
                    @Override
                    public void onReceive(JSONArray response) {
                        List<Pin> pins = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                pins.add(new Pin(obj));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onReceive(pins);
                    }

                    @Override
                    public void onError(APIError error) {
                        listener.onError(error);
                    }
                })
                .send();
    }

    /**
     * Gets the pin with the specific id from the server.
     * @param id the pin id to lookup
     * @param listener this listener is needed to return the result of the request (a list of pins)
     */
    public void getPin(int id, final NetworkListener<Pin> listener) {
        getNewJSONObjectRequest()
                .withMethod(Request.Method.GET)
                .withURL(PINS_URL + id + "/")
                .withNetworkListener(
                    new NetworkListener<JSONObject>() {
                        @Override
                        public void onReceive(JSONObject response) {
                            listener.onReceive(new Pin(response));
                        }

                        @Override
                        public void onError(APIError error) {
                            error.printStackTrace();
                            listener.onError(error);
                        }
                    })
            .send();
    }

    /**
     * Uploads a new pin to the server
     * @param pin the pin to be uploaded
     * @param listener (optional) listener for the result of the POST request
     */
    public void uploadNewPin(Pin pin, NetworkListener<JSONObject> listener) {
        getNewJSONObjectRequest()
                .withMethod(Request.Method.POST)
                .withURL(PINS_URL)
                .withJSONData(pin.toJSONObject())
                .withNetworkListener(listener)
                .send();
    }

    public void uploadNewPin(Pin pin) {
        uploadNewPin(pin, null);
    }

    /**
     * Gets a list of all comments from the server
     * @param listener this listener is needed to return the result of the request (a list of pins)
     */
    public void getAllComments(final NetworkListener<List<Comment>> listener) {
        getNewJSONArrayRequest()
                .withMethod(Request.Method.GET)
                .withURL(COMMENTS_URL)
                .withNetworkListener(new NetworkListener<JSONArray>() {
                    @Override
                    public void onReceive(JSONArray response) {
                        List<Comment> comments = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                comments.add(new Comment(obj));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        listener.onReceive(comments);
                    }

                    @Override
                    public void onError(APIError error) {
                        listener.onError(error);
                    }
                })
                .send();
    }

    /**
     * Uploads a new comment to the server
     * @param comment the comment to be uploaded
     * @param listener (optional) listener for the result of the POST request
     */
    public void uploadNewComment(Comment comment, NetworkListener<JSONObject> listener) {
        getNewJSONObjectRequest()
                .withMethod(Request.Method.POST)
                .withURL(COMMENTS_URL)
                .withJSONData(comment.toJSONObject())
                .withNetworkListener(listener)
                .send();
    }

    public void uploadNewComment(Comment comment) {
        uploadNewComment(comment, null);
    }

    /**
     * Uploads a new like to the server
     * @param like the comment to be uploaded
     * @param listener (optional) listener for the result of the POST request
     */
    public void uploadNewLike(Like like, NetworkListener<JSONObject> listener) {
        getNewJSONObjectRequest()
                .withMethod(Request.Method.POST)
                .withURL(LIKES_URL)
                .withJSONData(like.toJSONObject())
                .withNetworkListener(listener)
                .send();
    }

    public void uploadNewLike(Like like) {
        uploadNewLike(like, null);
    }

    /**
     * Remove the current user's like from pin
     * @param pin the pin to unlike
     */
    public void deleteLikeFromPin(Pin pin) {
        isPinLikedByMe(pin, new PinLikedByMeListener() {
            @Override
            public void isLikedByMe(Like like) {
                deleteLike(like);
            }

            @Override
            public void isNotLikedByMe() {
                Log.w("PinItAPI", "Pin is not liked by current user, cannot unlike.");
            }
        });
    }

    private void deleteLike(Like like) {
        getNewJSONObjectRequest()
                .withMethod(Request.Method.DELETE)
                .withURL(LIKES_URL + like.getId() + "/")
                .send();
    }

    /**
     * Asks the server if a specified pin is liked by the current user
     * @param pin the specified pin
     * @param listener PinLikedByMeListener for returning the result of the query
     */
    public void isPinLikedByMe(Pin pin, final PinLikedByMeListener listener) {
        getNewJSONArrayRequest()
                .withMethod(Request.Method.GET)
                .withURL(LIKES_URL)
                .withParam("pin", String.valueOf(pin.getId()))
                .withNetworkListener(new NetworkListener<JSONArray>() {
                    @Override
                    public void onReceive(JSONArray response) {
                        if (response.length() > 0) {
                            try {
                                listener.isLikedByMe(new Like(response.getJSONObject(0)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            listener.isNotLikedByMe();
                        }
                    }

                    @Override
                    public void onError(APIError error) {
                        error.printStackTrace();
                        listener.isNotLikedByMe();
                    }
                })
                .send();
    }

    private JSONRequestBuilder<JSONObject> getNewJSONObjectRequest() {
        return newJSONObjectRequest(requestQueue).withHeaders(getHeaders());
    }

    private JSONRequestBuilder<JSONArray> getNewJSONArrayRequest() {
        return newJSONArrayRequest(requestQueue).withHeaders(getHeaders());
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (token != null) {
            headers.put("Authorization", "Token " + token);
        }
        return headers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
