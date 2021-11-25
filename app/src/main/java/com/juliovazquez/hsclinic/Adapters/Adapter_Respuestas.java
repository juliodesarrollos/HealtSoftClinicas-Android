package com.juliovazquez.hsclinic.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.juliovazquez.hsclinic.Pojos.RetrofitPreguntas;
import com.juliovazquez.hsclinic.Pojos.RetrofitRespuesta;
import com.juliovazquez.hsclinic.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Respuestas extends RecyclerView.Adapter<Adapter_Respuestas.ViewHolder> {

    List<RetrofitRespuesta> respuestas = new ArrayList<>();
    Adapter_Respuestas.OnRespuestaListener onRespuestaListener;

    public Adapter_Respuestas(List<RetrofitRespuesta> preguntas, Adapter_Respuestas.OnRespuestaListener onRespuestaListener) {
        this.respuestas = preguntas;
        this.onRespuestaListener = onRespuestaListener;
    }

    @NonNull
    @Override
    public Adapter_Respuestas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pregunta, parent, false);
        Adapter_Respuestas.ViewHolder viewHolder = new Adapter_Respuestas.ViewHolder(itemView, onRespuestaListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Respuestas.ViewHolder holder, int position) {
        holder.txtNumero.setText((position+1) + ".- " + respuestas.get(position).getPregunta());
        holder.txtPregunta.setText("Respuesta: " + respuestas.get(position).getRespuesta());
    }

    @Override
    public int getItemCount() {
        return respuestas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtNumero, txtPregunta;
        OnRespuestaListener respuestaListener;

        public ViewHolder(@NonNull View item, OnRespuestaListener respuestaListener) {
            super(item);
            txtNumero = item.findViewById(R.id.txtNumero);
            txtPregunta = item.findViewById(R.id.txtPregunta);
            itemView.setOnClickListener(this::onClick);
            txtPregunta.setOnClickListener(this::onClick);
            txtNumero.setOnClickListener(this::onClick);
            this.respuestaListener = respuestaListener;
        }

        @Override
        public void onClick(View v) {
            respuestaListener.onRespuestaClick(getAdapterPosition());
        }
    }

    public interface OnRespuestaListener {
        void onRespuestaClick(int position);
    }
}
