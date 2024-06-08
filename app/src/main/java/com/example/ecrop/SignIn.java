package com.example.ecrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    TextInputEditText emailTxt, passwordTxt;
    TextView signUp, signUpLink;
    Button signIn;

    String email, password;

    // Firebase declaration
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailTxt = findViewById(R.id.et_email);
        passwordTxt = findViewById(R.id.et_password);
        signUp = findViewById(R.id.tv_sign_up);
        signUpLink = findViewById(R.id.tv_sign_up_link);
        signIn = findViewById(R.id.btn_sign_in);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set decor fits system windows
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green));

        // Dialog initialization
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setTitle("Please wait")
                .setMessage("Signing in...")
                .create();

        signUp.setOnClickListener(v -> {
            // Move to sign up activity
            startActivity(new Intent(SignIn.this, SignUp.class));
        });

        signUpLink.setOnClickListener(v -> {
            // Move to sign up activity
            startActivity(new Intent(SignIn.this, SignUp.class));
        });

        signIn.setOnClickListener(v -> {
            // Get user input from EditText and validate
            email = emailTxt.getText().toString();
            password = passwordTxt.getText().toString();

            if (validateInput(email, password)) {
                dialog.show();
                signInUser();
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailTxt.setError("Email is required");
            emailTxt.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Please enter a valid email address");
            emailTxt.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordTxt.setError("Password is required");
            passwordTxt.requestFocus();
            return false;
        }

        return true;
    }

    private void signInUser() {
        // Call the Firebase method to sign in user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        startActivity(new Intent(SignIn.this, Homepage.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(SignIn.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
    }
}
