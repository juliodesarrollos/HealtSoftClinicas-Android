package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitIngresosCharts {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("mes")
    @Expose
    private String mes;

    private static List<RetrofitIngresosCharts> ingresosCharts;

    public static void crear_instancia (List<RetrofitIngresosCharts> ingresosCharts) {
        RetrofitIngresosCharts.ingresosCharts = ingresosCharts;
    }

    public static List<RetrofitIngresosCharts> recuperar_instancia () {
        return RetrofitIngresosCharts.ingresosCharts;
    }

    public static void borrar_instancia () {
        RetrofitIngresosCharts.ingresosCharts = null;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

}