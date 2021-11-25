package com.juliovazquez.hsclinic.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.juliovazquez.hsclinic.Pojos.RetrofitEstudios;
import com.juliovazquez.hsclinic.Pojos.RetrofitHCPX;
import com.juliovazquez.hsclinic.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_Estudios extends RecyclerView.Adapter<Adapter_Estudios.ViewHolder> {

    List<RetrofitEstudios> estudios = new ArrayList<>();
    OnEstudiosListener onEstudiosListener;
    Context context;

    public Adapter_Estudios(Context context, List<RetrofitEstudios> estudios, OnEstudiosListener onEstudiosListener) {
        this.context = context;
        this.onEstudiosListener = onEstudiosListener;
        this.estudios = estudios;
    }

    @NonNull
    @Override
    public Adapter_Estudios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historias, parent, false);
        Adapter_Estudios.ViewHolder viewHolder = new Adapter_Estudios.ViewHolder(itemView, onEstudiosListener);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull Adapter_Estudios.ViewHolder holder, int position) {
        holder.txtDescripcion.setText(estudios.get(position).getDescripcion());
        holder.btnHistorias.setText(estudios.get(position).getCreatedAt().substring(0,10));
        holder.imgIcon.setImageDrawable(context.getDrawable(R.drawable.ic_estudios_icon));
        holder.btnVer.setText("Ver estudio");
        holder.btnHistorias.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return estudios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Button btnHistorias;
        Button btnVer;
        TextView txtDescripcion;
        ImageView imgIcon;
        OnEstudiosListener onEstudiosListener;

        public ViewHolder(@NonNull View item, OnEstudiosListener onEstudiosListener) {
            super(item);
            btnHistorias = item.findViewById(R.id.btnHistoria);
            btnVer  = item.findViewById(R.id.btnVer);
            txtDescripcion = item.findViewById(R.id.txtDescripcionH);
            imgIcon = item.findViewById(R.id.imgIcon);
            btnHistorias.setOnClickListener(this::onClick);
            btnVer.setOnClickListener(this::onClick);
            this.onEstudiosListener = onEstudiosListener;
        }

        @Override
        public void onClick(View v) {
            onEstudiosListener.onEstudioClick(getAdapterPosition(), v.getId());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public interface OnEstudiosListener {
        void onEstudioClick(int position, int id);
    }
}
