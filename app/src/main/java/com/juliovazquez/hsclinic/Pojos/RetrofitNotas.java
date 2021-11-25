package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitNotas {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nota")
    @Expose
    private String nota;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    private static List<RetrofitNotas> notas;

    public static void crear_instancia (List<RetrofitNotas> notas) {
        RetrofitNotas.notas = notas;
    }

    public static List<RetrofitNotas> recuperar_instancia () {
        return RetrofitNotas.notas;
    }

    public static void borrar_instancia () {
        RetrofitNotas.notas = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}