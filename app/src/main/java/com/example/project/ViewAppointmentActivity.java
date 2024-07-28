package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

public class ViewAppointmentActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private DatabaseReference databaseReference;

    private ListView appointmentsListView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();
        // Reference to the database
        databaseReference = FirebaseDatabase.getInstance().getReference("AvailableSlots");

        // Initialize ListView and Appointment list
        appointmentsListView = findViewById(R.id.appointmentsListView);
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(this, appointmentList);
        appointmentsListView.setAdapter(appointmentAdapter);

        fetchAppointments();
    }

    private void fetchAppointments() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String doctorId = currentUser.getUid();
            databaseReference.child(doctorId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    appointmentList.clear();
                    for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                        Appointment appointment = slotSnapshot.getValue(Appointment.class);
                        if (appointment != null) {
                            appointment.setId(slotSnapshot.getKey());
                            appointmentList.add(appointment);
                        }
                    }
                    appointmentAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewAppointmentActivity.this, "Failed to fetch appointments", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void markAppointmentAsBooked(String slotId) {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String doctorId = currentUser.getUid();
            databaseReference.child(doctorId).child(slotId).child("isBooked").setValue(true)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ViewAppointmentActivity.this, "Appointment marked as booked", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ViewAppointmentActivity.this, "Failed to mark appointment as booked", Toast.LENGTH_SHORT).show());
        }
    }

}