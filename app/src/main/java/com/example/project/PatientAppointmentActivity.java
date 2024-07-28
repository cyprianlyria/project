package com.example.project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private SlotAdapter slotAdapter;
    private List<Slot> slotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment);

        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("AvailableSlots");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        slotList = new ArrayList<>();
        slotAdapter = new SlotAdapter(slotList, slot -> bookSlot(slot));
        recyclerView.setAdapter(slotAdapter);

        loadAvailableSlots();
    }

    private void loadAvailableSlots() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                slotList.clear();
                for (DataSnapshot doctorSlots : dataSnapshot.getChildren()) {
                    for (DataSnapshot slotSnapshot : doctorSlots.getChildren()) {
                        Slot slot = slotSnapshot.getValue(Slot.class);
                        slotList.add(slot);
                    }
                }
                slotAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientAppointmentActivity.this, "Failed to load slots.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookSlot(Slot slot) {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference bookedSlotsRef = FirebaseDatabase.getInstance().getReference("BookedSlots");
        String bookingKey = bookedSlotsRef.push().getKey();
        if (bookingKey != null) {
            slot.setPatientId(currentUser.getUid());
            bookedSlotsRef.child(bookingKey).setValue(slot)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Slot booked successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to book slot. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}