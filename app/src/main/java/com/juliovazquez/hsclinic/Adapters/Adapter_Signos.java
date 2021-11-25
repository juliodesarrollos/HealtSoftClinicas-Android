package com.juliovazquez.hsclinic.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.juliovazquez.hsclinic.Pojos.RetrofitSignos;
import com.juliovazquez.hsclinic.R;
import java.util.ArrayList;
import java.util.List;

public class Adapter_Signos extends RecyclerView.Adapter<Adapter_Signos.ViewHolder> {

    List<RetrofitSignos> signos = new ArrayList<>();
    OnSignosListener onSignosListener;
    Context context;

    public Adapter_Signos(Context context, List<RetrofitSignos> signos, OnSignosListener onSignosListener) {
        this.context = context;
        this.onSignosListener = onSignosListener;
        this.signos = signos;
    }

    @NonNull
    @Override
    public Adapter_Signos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signos, parent, false);
        Adapter_Signos.ViewHolder viewHolder = new Adapter_Signos.ViewHolder(itemView, onSignosListener);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.globalView.setMinimumHeight(160);
        holder.btnSignos.setBackgroundColor(Color.WHITE);
        holder.btnSignos.setText(signos.get(position).getCreatedAt().substring(0,10));
        holder.txt1.setText(signos.get(position).getFrecuenciaRespiratoria() + " RPM");
        holder.txt2.setText(signos.get(position).getPresionSistolica()+"/" + signos.get(position).getPresionDiastolica());
        holder.txt3.setText(signos.get(position).getTemperatura() + "°C");
        holder.txt4.setText(signos.get(position).getGlucosa() + "%");
        holder.txt5.setText(signos.get(position).getPulso() + " LPM");
    }

    @Override
    public int getItemCount() {
        return signos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Button btnSignos;
        TextView txt1;
        TextView txt2;
        TextView txt3;
        TextView txt4;
        TextView txt5;
        ImageView imgIcon;
        CardView globalView;
        OnSignosListener onSignosListener;

        public ViewHolder(@NonNull View item, OnSignosListener onSignosListener) {
            super(item);
            btnSignos = item.findViewById(R.id.btnSignos);
            txt1 =  item.findViewById(R.id.txt1);
            txt2 =  item.findViewById(R.id.txt2);
            txt3 =  item.findViewById(R.id.txt3);
            txt4 =  item.findViewById(R.id.txt4);
            txt5 =  item.findViewById(R.id.txt5);
            imgIcon = item.findViewById(R.id.imgIcon);
            this.onSignosListener = onSignosListener;
            globalView  = item.findViewById(R.id.globalView);
            btnSignos.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            onSignosListener.onSignosClick(getAdapterPosition(), v.getId());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public interface OnSignosListener {
        void onSignosClick(int position, int id);
    }
}
