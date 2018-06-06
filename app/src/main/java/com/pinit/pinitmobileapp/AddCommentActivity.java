package com.pinit.pinitmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCommentActivity extends AppCompatActivity {

    public EditText message;
    private AppCompatButton submit_button, cancel_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        getWindow().setLayout(1000,900);
        message = (EditText) findViewById(R.id.comment_text_input);
        submit_button = (AppCompatButton) findViewById(R.id.submit_comment);
        cancel_button = (AppCompatButton) findViewById(R.id.cancel_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public EditText getMessage() {
        return message;
    }
}
