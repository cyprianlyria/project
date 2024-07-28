package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.data.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText editName, editPassword, editEmail;
    TextView tvBackToLogin;
    Button btnRegister, btnRegisterClinician; // Added button for clinician registration

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.editTextRegEmail);
        editPassword = findViewById(R.id.editTextRegPassword);
        editName = findViewById(R.id.editTextRegName);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegisterClinician = findViewById(R.id.btnSignUp); // Initialize the new button

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("userDetails");

        // Set onclick listeners
        btnRegister.setOnClickListener(view -> register());

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
        });

        btnRegisterClinician.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterClinician.class);
            startActivity(intent);
        });
    }

    public void register() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String category = "patient"; // Assuming default category is "patient"

        if (name.isEmpty()) {
            editName.setError("Enter a valid name");
            editName.requestFocus();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email");
            editEmail.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            editPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // User registered successfully
                    // Save user data to the database
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference userRef = databaseReference.child(userId);
                    UserDetails user = new UserDetails(name, email, category); // Assuming "defaultCategory" as an example
                    userRef.setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                // Data saved successfully
                                Toast.makeText(Register.this, "User registered and data saved successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, login.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Error saving data
                                Toast.makeText(Register.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Error registering user
                    Toast.makeText(Register.this, "Failed to register user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
