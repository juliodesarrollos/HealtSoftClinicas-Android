package com.juliovazquez.hsclinic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.juliovazquez.hsclinic.Pojos.RetrofitUser;
import com.juliovazquez.hsclinic.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_User extends RecyclerView.Adapter<Adapter_User.ViewHolder> {

    List<RetrofitUser> users = new ArrayList<>();
    private OnUserListener onUserListener;

    public Adapter_User( List<RetrofitUser> users, OnUserListener onUserListener) {
        this.users = users;
        this.onUserListener = onUserListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paciente, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView, onUserListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtNombre.setText(users.get(position).getName());
        holder.txtSexo.setText(users.get(position).getRol());
        holder.txtCorreo.setText(users.get(position).getEmail());
        holder.txtTelefono.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imgPAciente;
        TextView txtNombre;
        TextView txtSexo;
        TextView txtEdad;
        TextView txtCorreo;
        TextView txtTelefono;
        LinearLayout layoutPaciente;
        OnUserListener onUserListener;

        public ViewHolder(@NonNull View item, OnUserListener onUserListener) {
            super(item);
            imgPAciente = item.findViewById(R.id.imgPaciente);
            txtCorreo = item.findViewById(R.id.txtCorreo);
            txtEdad = item.findViewById(R.id.txtEdad);
            txtNombre = item.findViewById(R.id.txtNombre);
            txtSexo = item.findViewById(R.id.txtSexo);
            txtTelefono = item.findViewById(R.id.txtTelefono);
            layoutPaciente = item.findViewById(R.id.layoutPaciente);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.onUserListener = onUserListener;
        }

        @Override
        public void onClick(View v) {
            onUserListener.onUserClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onUserListener.onLongUserClick(getAdapterPosition());
            return true;
        }
    }

    public interface OnUserListener {
        void onUserClick(int position);
        void onLongUserClick(int position);
    }
}
