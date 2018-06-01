package com.pinit.pinitmobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCommentActivity extends AppCompatActivity {

    public EditText message;
    private Button submit_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        message = (EditText) findViewById(R.id.plain_text_input);
        submit_button = (Button) findViewById(R.id.submit_comment);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCommentActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public EditText getMessage() {
        return message;
    }
}
