package com.juliovazquez.hsclinic.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.juliovazquez.hsclinic.Pojos.RetrofitHCPX;
import com.juliovazquez.hsclinic.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_HCPX extends RecyclerView.Adapter<Adapter_HCPX.ViewHolder> {

    List<RetrofitHCPX> hcpxes = new ArrayList<>();
    OnHCPXListener onHCPXListener;
    Context context;

    public Adapter_HCPX(Context context, List<RetrofitHCPX> hcpxes, OnHCPXListener onHCPXListener) {
        this.context = context;
        this.hcpxes = hcpxes;
        this.onHCPXListener = onHCPXListener;
    }

    @NonNull
    @Override
    public Adapter_HCPX.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historias, parent, false);
        Adapter_HCPX.ViewHolder viewHolder = new Adapter_HCPX.ViewHolder(itemView, onHCPXListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_HCPX.ViewHolder holder, int position) {
        holder.txtDescripcion.setText("Registro: " + hcpxes.get(position).getName()+"\n"+hcpxes.get(position).getPreguntas() + " preguntas registradas");
        holder.btnHistorias.setText(hcpxes.get(position).getNombre());
        holder.btnVer.setText("Ver respuestas");
        holder.btnHistorias.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return hcpxes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Button btnHistorias;
        Button btnVer;
        TextView txtDescripcion;
        OnHCPXListener onHCPXListener;

        public ViewHolder(@NonNull View item, OnHCPXListener onHCPXListener) {
            super(item);
            btnHistorias = item.findViewById(R.id.btnHistoria);
            btnVer  = item.findViewById(R.id.btnVer);
            txtDescripcion = item.findViewById(R.id.txtDescripcionH);
            this.onHCPXListener = onHCPXListener;
            btnVer.setOnClickListener(this::onClick);
            btnHistorias.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            onHCPXListener.onHCPXClick(getAdapterPosition(), v.getId());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public interface OnHCPXListener {
        void onHCPXClick(int position, int id);
    }
}
