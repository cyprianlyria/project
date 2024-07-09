package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private TextView textViewLogin;
    private ProgressBar progressBar;
    private Button btnModel, btnChat, btnAppointment, btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textViewLogin = findViewById(R.id.textViewLogin);
        progressBar = findViewById(R.id.PBprogress);
        btnModel = findViewById(R.id.btnModel);
        btnChat = findViewById(R.id.btnChat);
        btnAppointment = findViewById(R.id.btnAppointment);
        btnPayment = findViewById(R.id.btnPayment);

        btnModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity2.this, ModelActivity.class);
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity2.this,DisplayUsers.class);
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
            }
        });

        btnAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity2.this, appointmentActivity.class);
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the action for payment
            }
        });
    }
}
