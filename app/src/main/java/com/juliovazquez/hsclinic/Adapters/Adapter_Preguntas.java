package com.juliovazquez.hsclinic.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.juliovazquez.hsclinic.Pojos.RetrofitPreguntas;
import com.juliovazquez.hsclinic.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Preguntas extends RecyclerView.Adapter<Adapter_Preguntas.ViewHolder> {

    List<RetrofitPreguntas> preguntas = new ArrayList<>();
    OnPreguntaListener onPreguntaListener;

    public Adapter_Preguntas(List<RetrofitPreguntas> preguntas, OnPreguntaListener onPreguntaListener) {
        this.preguntas = preguntas;
        this.onPreguntaListener = onPreguntaListener;
    }

    @NonNull
    @Override
    public Adapter_Preguntas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pregunta, parent, false);
        Adapter_Preguntas.ViewHolder viewHolder = new Adapter_Preguntas.ViewHolder(itemView, onPreguntaListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Preguntas.ViewHolder holder, int position) {
        if (preguntas.get(position).getStatus().equals("Activa")) {
            holder.txtNumero.setText("Pregunta " + (position + 1) + ":");
        } else {
            holder.txtNumero.setText("Pregunta " + (position + 1) + ": (Pregunta inactiva)");
        }
        holder.txtPregunta.setText(preguntas.get(position).getPregunta());
    }

    @Override
    public int getItemCount() {
        return preguntas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtNumero, txtPregunta;
        OnPreguntaListener preguntaListener;

        public ViewHolder(@NonNull View item, OnPreguntaListener preguntaListener) {
            super(item);
            txtNumero = item.findViewById(R.id.txtNumero);
            txtPregunta = item.findViewById(R.id.txtPregunta);
            itemView.setOnClickListener(this::onClick);
            txtPregunta.setOnClickListener(this::onClick);
            txtNumero.setOnClickListener(this::onClick);
            this.preguntaListener = preguntaListener;
        }

        @Override
        public void onClick(View v) {
            preguntaListener.onPreguntaClick(getAdapterPosition());
        }
    }

    public interface OnPreguntaListener {
        void onPreguntaClick(int position);
    }
}