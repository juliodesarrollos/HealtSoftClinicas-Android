package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitCitasCharts {

    @SerializedName("suma")
    @Expose
    private Integer suma;
    @SerializedName("suma2")
    @Expose
    private Integer suma2;
    @SerializedName("suma3")
    @Expose
    private Integer suma3;
    @SerializedName("mes")
    @Expose
    private String mes;

    private static List<RetrofitCitasCharts> citasCharts;

    public static void crear_instancia (List<RetrofitCitasCharts> citasCharts) {
        RetrofitCitasCharts.citasCharts = citasCharts;
    }

    public static List<RetrofitCitasCharts> recuperar_instancia () {
        return RetrofitCitasCharts.citasCharts;
    }

    public static void borrar_instancia () {
        RetrofitCitasCharts.citasCharts = null;
    }

    public Integer getSuma() {
        return suma;
    }

    public void setSuma(Integer suma) {
        this.suma = suma;
    }

    public Integer getSuma2() {
        return suma2;
    }

    public void setSuma2(Integer suma2) {
        this.suma2 = suma2;
    }

    public Integer getSuma3() {
        return suma3;
    }

    public void setSuma3(Integer suma3) {
        this.suma3 = suma3;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

}