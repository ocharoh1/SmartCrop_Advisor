package com.example.ecrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    TextInputEditText nameTxt, emailTxt, passwordTxt, confirmPasswordTxt;
    Button signUp;
    TextView login;
    String name, email, password, confirmPassword;

    // Firebase declaration
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameTxt = findViewById(R.id.et_name);
        emailTxt = findViewById(R.id.et_email);
        passwordTxt = findViewById(R.id.et_password);
        confirmPasswordTxt = findViewById(R.id.et_confirm_password);
        signUp = findViewById(R.id.btn_sign_up);
        login = findViewById(R.id.tv_sign_in);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green));

        // Dialog initialization
        dialog = new MaterialAlertDialogBuilder(this)
                .setView(new ProgressBar(this))
                .setCancelable(false)
                .setTitle("Please wait")
                .setMessage("Creating account...")
                .create();

        signUp.setOnClickListener(v -> {
            // Get user input from EditText and validate
            name = nameTxt.getText().toString();
            email = emailTxt.getText().toString();
            password = passwordTxt.getText().toString();
            confirmPassword = confirmPasswordTxt.getText().toString();

            if (validateInput(name, email, password, confirmPassword)) {
                dialog.show();
                createUser(name, email, password);
            }
        });

        login.setOnClickListener(v -> {
            // Open login activity
            startActivity(new Intent(SignUp.this, SignIn.class));
        });
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            nameTxt.setError("Name is required");
            nameTxt.requestFocus();
            return false;
        }

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

        if (confirmPassword.isEmpty()) {
            confirmPasswordTxt.setError("Confirm Password is required");
            confirmPasswordTxt.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordTxt.setError("Passwords do not match");
            confirmPasswordTxt.requestFocus();
            return false;
        }

        return true;
    }

    private void createUser(String name, String email, String password) {
        Log.d(TAG, "Creating user with email: " + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        saveUserToFirestore(name, email, password);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }

    private void saveUserToFirestore(String name, String email, String password) {
        User user = new User(name, email, password); // Store password (not recommended)
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User added to Firestore successfully");
                    startActivity(new Intent(SignUp.this, Homepage.class));
                    Toast.makeText(SignUp.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding user to Firestore", e);
                    Toast.makeText(SignUp.this, "Failed to save user info. Please try again.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
    }
}
