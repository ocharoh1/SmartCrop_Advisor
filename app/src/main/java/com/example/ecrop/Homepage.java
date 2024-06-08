package com.example.ecrop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Homepage extends AppCompatActivity {

    TextView tvWelcome;
    Button btnInputData;

    // Firebase declaration
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        tvWelcome = findViewById(R.id.tv_welcome);
        btnInputData = findViewById(R.id.btn_inpt_data);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    tvWelcome.setText("Welcome, " + documentSnapshot.getString("name"));
                });

        // Button click listener
        btnInputData.setOnClickListener(v -> {
            // Inflate the dialog with custom view
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.activity_recommendationpage, null);

            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);

            // Set up the dialog
            AlertDialog dialog = builder.create();

            // Set up the close button
            ImageView closeButton = dialogView.findViewById(R.id.close_button);
            closeButton.setOnClickListener(view -> {
                dialog.dismiss();
            });

            // Set up the recommend button
            Button recommendButton = dialogView.findViewById(R.id.btn_recommend);
            recommendButton.setOnClickListener(view -> {
                // Handle the recommend button click
                // For example, you can fetch data from the input fields and process it
                dialog.dismiss();
            });

            dialog.show();
        });
    }
}
