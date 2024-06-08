package com.example.ecrop;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textView.setText("Welcome to SmartCrop Advisor.");

        getStarted = findViewById(R.id.button);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
            }
        });


       /* //splash screen
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, SignIn.class);
                        startActivity(intent);
                    }
                },
                3000);*/

    }
}