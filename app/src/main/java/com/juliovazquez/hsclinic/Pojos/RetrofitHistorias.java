package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitHistorias {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("preguntas")
    @Expose
    private Integer preguntas;
    @SerializedName("name")
    @Expose
    private String name;

    private static List<RetrofitHistorias> historias;

    public static void crear_instancia (List<RetrofitHistorias> historias) {
        RetrofitHistorias.historias  = historias;
    }

    public static List<RetrofitHistorias> recuperar_instancia () {
        return RetrofitHistorias.historias;
    }

    public static void borrar_instancia () {
        RetrofitHistorias.historias = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(Integer preguntas) {
        this.preguntas = preguntas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}