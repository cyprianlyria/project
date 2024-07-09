package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class login extends AppCompatActivity {
    TextView editEmail, editPassword;
    ProgressBar setprogressBar;
    Button btnLogin, btnToAdmin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnSignin);
        btnToAdmin = findViewById(R.id.btnToAdmin);
        setprogressBar = findViewById(R.id.PBprogress);
        editPassword = findViewById(R.id.editPassword);
        editEmail = findViewById(R.id.editEmail);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        btnToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(login.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(login.this, "Enter valid password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If email and password match admin credentials, navigate to AdminPanel
                if (!TextUtils.isEmpty(email) && email.equals("admin@gmail.com") && password.equals("brian2372")) {
                    Intent intent = new Intent(getApplicationContext(), AdminPanelActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(login.this, "Admin Not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void LoginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar while signing in the user
        setprogressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setprogressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Navigate to ChatActivity after successful login
                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                            intent.putExtra("recipientEmail", "recipient@example.com"); // Replace with the recipient's email
                            startActivity(intent);
                            Toast.makeText(login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = "";
                            String errorCode = null;
                            if (task.getException() != null) {
                                errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                switch (errorCode) {
                                    case "ERROR_INVALID_EMAIL":
                                        errorMessage = "Invalid email address";
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        errorMessage = "Incorrect password";
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        errorMessage = "User not found. Please register first.";
                                        break;
                                    default:
                                        errorMessage = "Error logging in. Please try again.";
                                        break;
                                }
                            }
                            Toast.makeText(login.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setprogressBar.setVisibility(View.GONE);
                        Toast.makeText(login.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
