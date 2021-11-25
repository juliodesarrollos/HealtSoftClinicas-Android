package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitRespuesta {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("pregunta")
    @Expose
    private String pregunta;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("respuesta")
    @Expose
    private String respuesta;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    private static List<RetrofitRespuesta> respuestas;

    public static void crear_instancia (List<RetrofitRespuesta> respuestas) {
        RetrofitRespuesta.respuestas = respuestas;
    }

    public static List<RetrofitRespuesta> recuperar_instancia () {
        return RetrofitRespuesta.respuestas;
    }

    public static void borrar_instancia () {
        RetrofitRespuesta.respuestas = null;
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

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}