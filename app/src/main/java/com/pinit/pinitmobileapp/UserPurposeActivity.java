package com.pinit.pinitmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class UserPurposeActivity extends AppCompatActivity {

    private RadioButton planning;
    private RadioButton travelling;
    private RadioButton moving;
    private RadioButton yes;
    private RadioButton no;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_purpose);

        startButton = (Button)findViewById(R.id.gettingStarted);
        planning = (RadioButton) findViewById(R.id.planning);
        travelling = (RadioButton)findViewById(R.id.travelling);
        moving = (RadioButton) findViewById(R.id.moving);
        yes = (RadioButton)findViewById(R.id.yesButton);
        no = (RadioButton)findViewById(R.id.noButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserPurposeActivity.this, MapsActivity.class);
                if (yes.isChecked()) {
                    intent.putExtra("yes", R.id.yesButton);
                    if (planning.isChecked()) {
                        intent.putExtra("planning",R.id.planning);
                    }
                    if (travelling.isChecked()) {
                        intent.putExtra("travelling", R.id.travelling);
                    }
                    if (moving.isChecked()) {
                        intent.putExtra("moving", R.id.moving);
                    }
                }
                if (no.isChecked()) {
                    intent.putExtra("no", R.id.noButton);
                    if (planning.isChecked()) {
                        intent.putExtra("planning",R.id.planning);
                    }
                    if (travelling.isChecked()) {
                        intent.putExtra("travelling", R.id.travelling);
                    }
                    if (moving.isChecked()) {
                        intent.putExtra("moving", R.id.moving);
                    }
                }
                intent.putExtra(MapsActivity.USER_TOKEN, getIntent().getExtras().getString(MapsActivity.USER_TOKEN));
                startActivity(intent);
                finish();
            }
        });
    }
}
