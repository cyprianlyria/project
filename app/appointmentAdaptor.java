package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class appointmentAdapter extends ArrayAdapter<Appointment> {

    private Context mContext;
    private List<Appointment> appointmentList;

    public appointmentAdapter(@NonNull Context context, @NonNull List<Appointment> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.appointmentList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_appointments, parent, false);
        }

        Appointment appointment = appointmentList.get(position);

        TextView appointmentDate = convertView.findViewById(R.id.appointmentDate);
        TextView appointmentTime = convertView.findViewById(R.id.appointmentTime);
        TextView appointmentStatus = convertView.findViewById(R.id.appointmentStatus);
        Button btnMarkBooked = convertView.findViewById(R.id.btnMarkBooked);

        appointmentDate.setText(appointment.getDate());
        appointmentTime.setText(appointment.getTime());
        appointmentStatus.setText(appointment.isBooked() ? "Booked" : "Available");

        btnMarkBooked.setVisibility(appointment.isBooked() ? View.GONE : View.VISIBLE);

        btnMarkBooked.setOnClickListener(v -> {
            if (mContext instanceof ViewAppointmentActivity) {
                ((ViewAppointmentActivity) mContext).markAppointmentAsBooked(appointment.getId());
            }
        });

        return convertView;
    }
}