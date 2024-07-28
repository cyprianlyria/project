package com.example.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DoctorAppointmentActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private DatabaseReference databaseReference;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button btnAddSlot;

    private String doctorName;
    private String doctorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        // Reference to the database
        databaseReference = FirebaseDatabase.getInstance().getReference("AvailableSlots");

        // Initialize views
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        btnAddSlot = findViewById(R.id.btnAddSlot);

        btnAddSlot.setOnClickListener(view -> saveAvailableSlot());

        fetchDoctorDetails();
    }

    private void fetchDoctorDetails() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(userId);

            doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        doctorName = dataSnapshot.child("name").getValue(String.class);
                        doctorEmail = dataSnapshot.child("email").getValue(String.class);
                    } else {
                        Toast.makeText(DoctorAppointmentActivity.this, "Doctor details not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DoctorAppointmentActivity.this, "Failed to fetch doctor details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAvailableSlot() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser == null || doctorName == null || doctorEmail == null) {
            Toast.makeText(this, "Failed to add slot. Missing doctor details.", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String slotKey = databaseReference.child(currentUser.getUid()).push().getKey();
        if (slotKey != null) {
            Map<String, Object> slotData = new HashMap<>();
            slotData.put("date", year + "-" + (month + 1) + "-" + day);
            slotData.put("time", hour + ":" + minute);
            slotData.put("doctorId", currentUser.getUid());
            slotData.put("doctorName", doctorName);
            slotData.put("doctorEmail", doctorEmail);

            databaseReference.child(currentUser.getUid()).child(slotKey).setValue(slotData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Slot added successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add slot. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}