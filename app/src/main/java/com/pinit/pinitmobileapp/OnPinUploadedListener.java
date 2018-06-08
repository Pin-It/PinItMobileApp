package com.pinit.pinitmobileapp;

import android.content.Context;
import android.widget.Toast;
import com.pinit.api.NetworkListener;
import com.pinit.api.errors.APIError;
import com.pinit.api.models.Pin;
import org.json.JSONObject;

public abstract class OnPinUploadedListener implements NetworkListener<JSONObject> {
    private Context context;

    public OnPinUploadedListener(Context context) {
        this.context = context;
    }

    protected abstract void onPinUploaded(Pin uploadedPin);

    @Override
    public void onReceive(JSONObject response) {
        onPinUploaded(new Pin(response));
    }

    @Override
    public void onError(APIError error) {
        Toast.makeText(context, "You're not logged in :(", Toast.LENGTH_LONG).show();
    }
}
