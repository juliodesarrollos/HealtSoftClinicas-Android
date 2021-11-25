package com.juliovazquez.hsclinic.Pojos;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitPacientesCharts {

    @SerializedName("suma")
    @Expose
    private Integer suma;
    @SerializedName("mes")
    @Expose
    private String mes;

    private static List<RetrofitPacientesCharts> pacientesCharts;

    public static void crear_instancia (List<RetrofitPacientesCharts> pacientesCharts) {
        RetrofitPacientesCharts.pacientesCharts = pacientesCharts;
    }

    public static List<RetrofitPacientesCharts> recuperar_instancia () {
        return RetrofitPacientesCharts.pacientesCharts;
    }

    public static void borrar_instancia () {
        RetrofitPacientesCharts.pacientesCharts = null;
    }

    public Integer getSuma() {
        return suma;
    }

    public void setSuma(Integer suma) {
        this.suma = suma;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

}