package com.juliovazquez.hsclinic.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_Servicio extends RecyclerView.Adapter<Adapter_Servicio.ViewHolder> {

    List<RetrofitServices> services = new ArrayList<>();
    private OnServiceListener onServiceListener;

    public Adapter_Servicio(List<RetrofitServices> services, OnServiceListener onServiceListener) {
        this.services = services;
        this.onServiceListener = onServiceListener;
    }

    @NonNull
    @Override
    public Adapter_Servicio.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
        Adapter_Servicio.ViewHolder viewHolder = new Adapter_Servicio.ViewHolder(itemView, onServiceListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Servicio.ViewHolder holder, int position) {
        holder.txtCosto.setText("$"+services.get(position).getCosto().toString()+".00 MXN");
        holder.txtDescripcion.setText(services.get(position).getDescripcion());
        holder.btnServicio.setText(services.get(position).getServicio());
        holder.btnServicio.setBackgroundColor(Color.WHITE  );
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Button btnServicio;
        TextView txtDescripcion;
        TextView txtCosto;
        OnServiceListener onServiceListener;

        public ViewHolder(@NonNull View item, OnServiceListener onServiceListener) {
            super(item);
            btnServicio = item.findViewById(R.id.btnServicio);
            txtDescripcion = item.findViewById(R.id.txtComentario);
            txtCosto = item.findViewById(R.id.txtCosto);
            btnServicio.setOnClickListener(this);
            this.onServiceListener = onServiceListener;
        }

        @Override
        public void onClick(View v) {
            onServiceListener.onServiceClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public interface OnServiceListener {
        void onServiceClick(int position);
    }
}
