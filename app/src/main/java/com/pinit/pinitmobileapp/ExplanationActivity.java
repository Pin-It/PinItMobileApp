package com.pinit.pinitmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ExplanationActivity extends AppCompatActivity {

    private FloatingActionButton fabuno;
    private FloatingActionButton fabdos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String token = getIntent().getExtras().getString(MapsActivity.USER_TOKEN, null);

        if (!isFirstTime()) {
            Intent nextIntent = new Intent(ExplanationActivity.this, MapsActivity.class);
            nextIntent.putExtra(MapsActivity.USER_TOKEN, token);
            startActivity(nextIntent);
            finish();
        }
        setContentView(R.layout.activity_explanation);

        fabuno = findViewById(R.id.fab1);
        fabdos = findViewById(R.id.fab2);

        fabuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(ExplanationActivity.this, MapsActivity.class);
                nextIntent.putExtra(MapsActivity.USER_TOKEN, token);
                startActivity(nextIntent);
                finish();
            }
        });

        fabdos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secIntent = new Intent(ExplanationActivity.this, MapsActivity.class);
                secIntent.putExtra(MapsActivity.USER_TOKEN, token);
                startActivity(secIntent);
                finish();
            }
        });
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore",true);
            editor.commit();
        }
        return !ranBefore;
    }
}
