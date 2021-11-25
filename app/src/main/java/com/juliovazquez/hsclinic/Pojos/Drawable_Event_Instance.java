package com.juliovazquez.hsclinic.Pojos;


import com.juliovazquez.hsclinic.Activities.Agenda.DrawableCalendarEvent;

import java.util.List;

public class Drawable_Event_Instance {
    public static List <DrawableCalendarEvent> eventosDra;

    public static void crear_instancia(List<DrawableCalendarEvent> eventos) {
        if (eventosDra == null) {
            eventosDra = eventos;
        }
    }

    public static List <DrawableCalendarEvent> recuperar_instancia() {
        return eventosDra;
    }

    public static void borar_instancia() {
        eventosDra = null;
    }

}
