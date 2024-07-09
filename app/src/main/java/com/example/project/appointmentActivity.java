package com.example.project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class appointmentActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private CollectionReference appointmentsCollection;

    private EditText textViewUsername, editTextName, editTextPhone, editTextUserPhone;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button bookAppointmentButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        appointmentsCollection = fStore.collection("Appointments");

        textViewUsername = findViewById(R.id.editTextdocName);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextContact);
        editTextUserPhone = findViewById(R.id.editTextPhone);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);

        // Retrieve the doctor's name and phone number from the Intent
        String doctorName = getIntent().getStringExtra("doctorEmail");
        String doctorPhone = getIntent().getStringExtra("doctorPhoneNum");
        textViewUsername.setText(doctorName);

        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            if (userEmail != null && !userEmail.isEmpty()) {
                editTextPhone.setText(userEmail);
            }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }

        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointmentToFirestore();
            }
        });
    }

    private void saveAppointmentToFirestore() {
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String docName = textViewUsername.getText().toString();
        String name = editTextName.getText().toString();
        String phoneNumber = editTextUserPhone.getText().toString();
        String contact = editTextPhone.getText().toString();
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        if (docName.isEmpty() || name.isEmpty() || phoneNumber.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Date appointmentDate = new Date(year - 1900, month, day, hour, minute);
        Date currentDate = new Date();

        if (appointmentDate.before(currentDate)) {
            Toast.makeText(this, "You cannot book an appointment in the past. Please choose a future date and time.", Toast.LENGTH_SHORT).show();
            return;
        }

        Query existingAppointmentQuery = appointmentsCollection
                .whereEqualTo("doctorName", docName)
                .whereEqualTo("date", appointmentDate);

        existingAppointmentQuery.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Appointment already booked for this time. Please choose another time.", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> appointmentData = new HashMap<>();
                        appointmentData.put("doctorName", docName);
                        appointmentData.put("patientName", name);
                        appointmentData.put("contactInfo", contact);
                        appointmentData.put("date", appointmentDate);
                        appointmentData.put("patientPhoneNumber", phoneNumber);

                        appointmentsCollection.add(appointmentData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();
                                    clearInputFields();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to book appointment. Please try again.", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking appointment availability: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputFields() {
        editTextName.setText("");
        editTextUserPhone.setText("");
        editTextPhone.setText("");
    }
}
