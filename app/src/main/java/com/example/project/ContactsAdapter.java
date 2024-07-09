package com.example.project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.data.UserDetails;

import java.time.temporal.Temporal;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<UserDetails> userList;
    UserDetails userinfo;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserDetails user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public ContactsAdapter(List<UserDetails> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_rv_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (userList != null) {
            UserDetails userInfo = userList.get(position);
            if (userInfo != null) {
                holder.bind(userInfo);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onItemClick(userInfo);
                            Intent intent = new Intent(view.getContext(), ChatActivity.class);
                            intent.putExtra("recipientEmail", userInfo.getUseremail());
                            view.getContext().startActivity(intent);
                        }
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.username);
        }

        public void bind(UserDetails userInfo) {
            textName.setText(userInfo.getUsername());
        }
    }
}
