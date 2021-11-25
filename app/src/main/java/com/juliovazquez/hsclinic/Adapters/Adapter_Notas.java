package com.juliovazquez.hsclinic.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.juliovazquez.hsclinic.Pojos.RetrofitNotas;
import com.juliovazquez.hsclinic.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_Notas extends RecyclerView.Adapter<Adapter_Notas.ViewHolder> {

    List<RetrofitNotas> notas = new ArrayList<>();
    Context context;

    public Adapter_Notas(Context context, List<RetrofitNotas> notas) {
        this.context = context;
        this.notas = notas;
    }

    @NonNull
    @Override
    public Adapter_Notas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notas, parent, false);
        Adapter_Notas.ViewHolder viewHolder = new Adapter_Notas.ViewHolder(itemView);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtDescripcion.setText(notas.get(position).getNota());
        holder.btnHistorias.setText(notas.get(position).getCreatedAt().substring(0,10));
        holder.btnVer.setVisibility(View.GONE);
        holder.txtDescripcion.setLines(4);
        holder.imgIcon.setImageDrawable(context.getDrawable(R.drawable.ic_notas_icon));
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Button btnHistorias;
        Button btnVer;
        TextView txtDescripcion;
        ImageView imgIcon;
        CardView globalView;
        LinearLayout brHistorias;

        public ViewHolder(@NonNull View item) {
            super(item);
            btnHistorias = item.findViewById(R.id.btnHistoria);
            btnVer  = item.findViewById(R.id.btnVer);
            txtDescripcion = item.findViewById(R.id.txtDescripcionH);
            imgIcon = item.findViewById(R.id.imgIcon);
            brHistorias = item.findViewById(R.id.brHistorias);
            globalView  = item.findViewById(R.id.globalView);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            //this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            //clickListener.onClick(v, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
