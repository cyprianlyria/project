package com.example.project;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private List<Slot> slotList;
    private OnSlotClickListener onSlotClickListener;

    public SlotAdapter(List<Slot> slotList, OnSlotClickListener onSlotClickListener) {
        this.slotList = slotList;
        this.onSlotClickListener = onSlotClickListener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Slot slot = slotList.get(position);
        holder.date.setText(slot.getDate());
        holder.time.setText(slot.getTime());
        holder.doctorName.setText(slot.getDoctorName());
        holder.status.setText(slot.isBooked() ? "Booked" : "Available");

        holder.bookButton.setVisibility(slot.isBooked() ? View.GONE : View.VISIBLE);

        holder.bookButton.setOnClickListener(v -> onSlotClickListener.onSlotClick(slot));
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView date, time, doctorName, status;
        Button bookButton;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.slotDate);
            time = itemView.findViewById(R.id.slotTime);
            doctorName = itemView.findViewById(R.id.slotDoctorName);
            status = itemView.findViewById(R.id.slotStatus);
            bookButton = itemView.findViewById(R.id.bookButton);
        }
    }

    public interface OnSlotClickListener {
        void onSlotClick(Slot slot);}
}