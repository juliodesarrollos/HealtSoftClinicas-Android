package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitPreguntas {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("pregunta")
    @Expose
    private String pregunta;
    @SerializedName("status")
    @Expose
    private String status;

    private static List<RetrofitPreguntas> preguntas;

    public static void crear_instancia (List<RetrofitPreguntas> preguntas) {
        RetrofitPreguntas.preguntas = preguntas;
    }

    public static List<RetrofitPreguntas> recuperar_instancia () {
        return RetrofitPreguntas.preguntas;
    }

    public static void borrar_instancia () {
        RetrofitPreguntas.preguntas = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}