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

import com.juliovazquez.hsclinic.Activities.Paciente.Activity_Menu_Paciente;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Tools.Tool_Images;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Paciente extends RecyclerView.Adapter<Adapter_Paciente.ViewHolder> {

    List<RetrofitPacient> pacientesList = new ArrayList<>();
    Context context;

    public Adapter_Paciente(Context context, List<RetrofitPacient> pacientesList) {
        this.context = context;
        this.pacientesList = pacientesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paciente, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(context)
                .load(pacientesList.get(position).getFotoUrl())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .resize(100, 100)
                .centerCrop()
                .transform(new Tool_Images())
                .into(holder.imgPAciente);
        holder.txtNombre.setText(pacientesList.get(position).getNombre());
        holder.txtSexo.setText(pacientesList.get(position).getSexo());
        holder.txtCorreo.setText(pacientesList.get(position).getCorreo());
        holder.txtTelefono.setText(pacientesList.get(position).getTelefono().toString());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent i = new Intent(context, Activity_Menu_Paciente.class);
                i.putExtra("IDPACIENTE", pacientesList.get(position).getId());
                i.putExtra("NOMBREPACIENTE", pacientesList.get(position).getNombre());
                i.putExtra("CORREOPACIENTE", pacientesList.get(position).getCorreo());
                i.putExtra("TELEFONOPACIENTE", pacientesList.get(position).getTelefono());
                i.putExtra("EDADPACIENTE", pacientesList.get(position).getFechaNacimiento());
                i.putExtra("URLPACIENTE", pacientesList.get(position).getFotoUrl());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pacientesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imgPAciente;
        TextView txtNombre;
        TextView txtSexo;
        TextView txtEdad;
        TextView txtCorreo;
        TextView txtTelefono;
        LinearLayout layoutPaciente;
        private ItemClickListener clickListener;

        public ViewHolder(@NonNull View item) {
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
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
