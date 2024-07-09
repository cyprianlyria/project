package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdminPanelActivity extends AppCompatActivity {
    Button btnaddnewfarmer,btnaddnewvet,btnupdateusers;
    TextView TVback,TVlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        btnaddnewvet=findViewById(R.id.btnRegisterVet);
        btnaddnewfarmer=findViewById(R.id.btnRegisterPatient);
        btnupdateusers=findViewById(R.id.btnUpdateusers);
        TVback=findViewById(R.id.TVback);
        TVlogout=findViewById(R.id.TVLogout);

        // onclicklisteners
        TVlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), login.class);
                startActivity(intent);
            }
        });
        //
        TVback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), login.class);
                startActivity(intent);
            }
        });

        btnaddnewfarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });

//        btnaddnewvet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(), patientActivity.class);
//                startActivity(intent);
//            }
//        });

    }





}