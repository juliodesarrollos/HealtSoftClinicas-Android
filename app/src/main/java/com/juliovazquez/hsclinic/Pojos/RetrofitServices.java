package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitServices {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("servicio")
    @Expose
    private String servicio;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("costo")
    @Expose
    private Integer costo;

    private static List<RetrofitServices> services;

    public static void crear_instancia (List<RetrofitServices> services) {
        RetrofitServices.services = services;
    }

    public static List<RetrofitServices> recuperar_instancia () {
        return RetrofitServices.services;
    }

    public static void borrar_instancia () {
        RetrofitServices.services = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCosto() {
        return costo;
    }

    public void setCosto(Integer costo) {
        this.costo = costo;
    }

}