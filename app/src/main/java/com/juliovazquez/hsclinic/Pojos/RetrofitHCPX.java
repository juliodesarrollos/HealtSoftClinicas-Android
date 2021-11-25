package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitHCPX {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("preguntas")
    @Expose
    private Integer preguntas;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("name")
    @Expose
    private String name;

    private static List<RetrofitHCPX> hcpxes;

    public static void crear_instancia (List<RetrofitHCPX> hcpxes) {
        RetrofitHCPX.hcpxes  = hcpxes;
    }

    public static List<RetrofitHCPX> recuperar_instancia () {
        return RetrofitHCPX.hcpxes;
    }

    public static void borrar_instancia () {
        RetrofitHCPX.hcpxes = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(Integer preguntas) {
        this.preguntas = preguntas;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}