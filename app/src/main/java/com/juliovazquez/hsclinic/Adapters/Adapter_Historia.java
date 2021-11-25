package com.juliovazquez.hsclinic.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
import com.juliovazquez.hsclinic.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Historia extends RecyclerView.Adapter<Adapter_Historia.ViewHolder> {

    List<RetrofitHistorias> historias = new ArrayList<>();
    OnHistoriaListener onHistoriaListener;

    public Adapter_Historia(List<RetrofitHistorias> historias, OnHistoriaListener onHistoriaListener) {
        this.historias = historias;
        this.onHistoriaListener = onHistoriaListener;
    }

    @NonNull
    @Override
    public Adapter_Historia.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historias, parent, false);
        Adapter_Historia.ViewHolder viewHolder = new Adapter_Historia.ViewHolder(itemView, onHistoriaListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Historia.ViewHolder holder, int position) {
        holder.txtDescripcion.setText("Ultima actualización: "+ historias.get(position).getCreatedAt().substring(0, 10) +
                "\n"+historias.get(position).getPreguntas() + " preguntas registradas");
        holder.btnHistorias.setText(historias.get(position).getNombre());
        holder.btnHistorias.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return historias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Button btnHistorias, btnVer;
        TextView txtDescripcion;
        OnHistoriaListener onHistoriaListener;

        public ViewHolder(@NonNull View item, OnHistoriaListener onHistoriaListener) {
            super(item);
            btnHistorias = item.findViewById(R.id.btnHistoria);
            txtDescripcion = item.findViewById(R.id.txtDescripcionH);
            btnVer = item.findViewById(R.id.btnVer);
            btnVer.setOnClickListener(this::onClick);
            btnHistorias.setOnClickListener(this::onClick);
            this.onHistoriaListener = onHistoriaListener;
        }

        @Override
        public void onClick(View v) {
            onHistoriaListener.onHistoriaClick(getAdapterPosition(), v.getId());
        }
    }

    public interface OnHistoriaListener {
        void onHistoriaClick(int position, int id);
    }
}
