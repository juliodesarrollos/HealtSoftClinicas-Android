package com.juliovazquez.hsclinic.Activities.Agenda;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.tibolte.agendacalendarview.render.EventRenderer;
import com.juliovazquez.hsclinic.R;

import java.util.Calendar;
import java.util.Locale;

public class DrawableEventRenderer extends EventRenderer<DrawableCalendarEvent> {
    @Override
    public void render(View view, DrawableCalendarEvent event) {
        ImageView imgPaciente = view.findViewById(R.id.imgPaciente);
        ImageView imgLocation = view.findViewById(R.id.imgLocation);
        ImageView imgTerapeuta = view.findViewById(R.id.imgTerapeuta);
        ImageView imgHora = view.findViewById(R.id.imgHora);
        TextView txtTitle = view.findViewById(R.id.view_agenda_event_title);
        TextView txtPaciente = view.findViewById(R.id.txtEventPaciente);
        TextView txtDoctor = view.findViewById(R.id.txtEventDoctor);
        TextView txtLocation = view.findViewById(R.id.view_agenda_event_location);
        TextView txtHora = view.findViewById(R.id.view_agenda_event_hora);
        TextView txtHora2 = view.findViewById(R.id.txtHora);
        LinearLayout descriptionContainer = view.findViewById(R.id.view_agenda_event_description_container);
        LinearLayout locationContainer = view.findViewById(R.id.view_agenda_event_location_container);
        LinearLayout layoutSeparator = view.findViewById(R.id.layoutSeparator);
        descriptionContainer.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(event.getStartTime().getTimeInMillis());
        String date = DateFormat.format("HH:mm", cal).toString();
        String paciente = event.getLocation();
        String doctor = event.getDescription();
        String titulo = event.getTitle();
        txtPaciente.setText(paciente);
        txtDoctor.setText(doctor);
        txtTitle.setText(titulo);
        txtDoctor.setTextColor(Color.BLACK);
        txtPaciente.setTextColor(Color.BLACK);
        txtTitle.setTextColor(Color.BLACK);
        txtLocation.setText(R.string.consultorio_1);
        txtLocation.setVisibility(View.GONE);
        txtHora.setText(date);
        txtHora2.setText(date);
        //txtHora2.setBackgroundColor(event.getColor());
        txtHora2.setTextColor(Color.BLACK);
        txtHora2.getBackground().setAlpha(90);
        txtHora.setVisibility(View.GONE);
        locationContainer.setVisibility(View.GONE);
        imgPaciente.setColorFilter(Color.WHITE);
        imgHora.setVisibility(View.GONE);
        imgLocation.setVisibility(View.GONE);
        imgPaciente.setVisibility(View.GONE);
        imgTerapeuta.setVisibility(View.GONE);
        imgTerapeuta.setColorFilter(Color.WHITE);
        imgLocation.setColorFilter(Color.WHITE);
        imgHora.setColorFilter(Color.WHITE);
        locationContainer.setVisibility(View.VISIBLE);
        descriptionContainer.setBackgroundColor(event.getColor());
        descriptionContainer.getBackground().setAlpha(90);
        layoutSeparator.setBackgroundColor(event.getColor());
    }

    @Override
    public int getEventLayout() {
        return R.layout.item_event_calendar;
    }

    @Override
    public Class<DrawableCalendarEvent> getRenderType() {
        return super.getRenderType();
    }
}
