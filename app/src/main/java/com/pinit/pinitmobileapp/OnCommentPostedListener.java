package com.pinit.pinitmobileapp;

import android.content.Context;
import android.widget.Toast;
import com.pinit.api.NetworkListener;
import com.pinit.api.errors.APIError;
import org.json.JSONObject;

public abstract class OnCommentPostedListener implements NetworkListener<JSONObject> {
    private Context context;

    public OnCommentPostedListener(Context context) {
        this.context = context;
    }

    protected abstract void onCommentPosted();

    @Override
    public void onReceive(JSONObject response) {
        onCommentPosted();
    }

    @Override
    public void onError(APIError error) {
        String message = "Network error occured while posting comment, try again later";
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
