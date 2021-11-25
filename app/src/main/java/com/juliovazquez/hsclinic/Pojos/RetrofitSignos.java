package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitSignos {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("temperatura")
    @Expose
    private Double temperatura;
    @SerializedName("pulso")
    @Expose
    private Integer pulso;
    @SerializedName("frecuenciaRespiratoria")
    @Expose
    private Integer frecuenciaRespiratoria;
    @SerializedName("presionSistolica")
    @Expose
    private Integer presionSistolica;
    @SerializedName("presionDiastolica")
    @Expose
    private Integer presionDiastolica;
    @SerializedName("glucosa")
    @Expose
    private Integer glucosa;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    private static List<RetrofitSignos> signos;

    public static void crear_instancia (List<RetrofitSignos> signos) {
        RetrofitSignos.signos = signos;
    }

    public static List<RetrofitSignos> recuperar_instancia () {
        return RetrofitSignos.signos;
    }

    public static void borrar_instancia () {
        RetrofitSignos.signos = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getPulso() {
        return pulso;
    }

    public void setPulso(Integer pulso) {
        this.pulso = pulso;
    }

    public Integer getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public Integer getPresionSistolica() {
        return presionSistolica;
    }

    public void setPresionSistolica(Integer presionSistolica) {
        this.presionSistolica = presionSistolica;
    }

    public Integer getPresionDiastolica() {
        return presionDiastolica;
    }

    public void setPresionDiastolica(Integer presionDiastolica) {
        this.presionDiastolica = presionDiastolica;
    }

    public Integer getGlucosa() {
        return glucosa;
    }

    public void setGlucosa(Integer glucosa) {
        this.glucosa = glucosa;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}