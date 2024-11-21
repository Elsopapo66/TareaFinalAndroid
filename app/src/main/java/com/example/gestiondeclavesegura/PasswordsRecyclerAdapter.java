package com.example.gestiondeclavesegura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PasswordsRecyclerAdapter extends RecyclerView.Adapter<PasswordsRecyclerAdapter.PasswordViewHolder> {

    public interface OnPasswordActionListener {
        void onEdit(PasswordModel password);
        void onDelete(PasswordModel password);
    }

    private final List<PasswordModel> passwordList;
    private final OnPasswordActionListener listener;

    public PasswordsRecyclerAdapter(List<PasswordModel> passwordList, OnPasswordActionListener listener) {
        this.passwordList = passwordList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_password, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
        PasswordModel password = passwordList.get(position);
        holder.tvSiteName.setText(password.getSiteName());
        holder.tvUsername.setText(password.getUsername());
        holder.tvPassword.setText(password.getPassword());

        // Configurar botones de editar y eliminar
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(password));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(password));
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    public static class PasswordViewHolder extends RecyclerView.ViewHolder {
        TextView tvSiteName, tvUsername, tvPassword;
        ImageButton btnEdit, btnDelete;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSiteName = itemView.findViewById(R.id.tvSiteName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvPassword = itemView.findViewById(R.id.tvPassword);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
