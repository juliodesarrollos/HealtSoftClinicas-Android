package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitEvents {

    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("pacient")
    @Expose
    private String pacient;
    @SerializedName("doctor")
    @Expose
    private String doctor;

    private static List<RetrofitEvents> events;

    public static void crear_instancia (List<RetrofitEvents> events) {
        RetrofitEvents.events  = events;
    }

    public static List<RetrofitEvents> recuperar_instancia () {
        return RetrofitEvents.events;
    }

    public static void borrar_instancia () {
        RetrofitEvents.events = null;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPacient() {
        return pacient;
    }

    public void setPacient(String pacient) {
        this.pacient = pacient;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }
}