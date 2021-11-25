package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitEstudios {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("estudioUrl")
    @Expose
    private String estudioUrl;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    private static List<RetrofitEstudios> estudios;

    public static void crear_instancia (List<RetrofitEstudios> estudios) {
        RetrofitEstudios.estudios  = estudios;
    }

    public static List<RetrofitEstudios> recuperar_instancia () {
        return RetrofitEstudios.estudios;
    }

    public static void borrar_instancia () {
        RetrofitEstudios.estudios = null;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstudioUrl() {
        return estudioUrl;
    }

    public void setEstudioUrl(String estudioUrl) {
        this.estudioUrl = estudioUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}