package com.pinit.pinitmobileapp;

import android.content.Intent;
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
        setContentView(R.layout.activity_explanation);
        fabuno = findViewById(R.id.fab1);
        fabdos = findViewById(R.id.fab2);

        fabuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(ExplanationActivity.this, MapsActivity.class);
                startActivity(nextIntent);
                finish();
            }
        });

        fabdos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secIntent = new Intent(ExplanationActivity.this, GeneralySafetyMapActivity.class);
                startActivity(secIntent);
                finish();
            }
        });
    }
}
