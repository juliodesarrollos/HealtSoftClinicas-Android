package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitDashboard {

    @SerializedName("pacientes")
    @Expose
    private Integer pacientes;
    @SerializedName("citas")
    @Expose
    private Integer citas;
    @SerializedName("citas2")
    @Expose
    private Integer citas2;
    @SerializedName("ingresos")
    @Expose
    private Integer ingresos;

    private static List<RetrofitDashboard> dashboards;

    public static void crear_instancia (List<RetrofitDashboard> nota) {
        RetrofitDashboard.dashboards = nota;
    }

    public static List<RetrofitDashboard> recuperar_instancia () {
        return RetrofitDashboard.dashboards;
    }

    public static void borrar_instancia () {
        RetrofitDashboard.dashboards = null;
    }

    public Integer getPacientes() {
        return pacientes;
    }

    public void setPacientes(Integer pacientes) {
        this.pacientes = pacientes;
    }

    public Integer getCitas() {
        return citas;
    }

    public void setCitas(Integer citas) {
        this.citas = citas;
    }

    public Integer getCitas2() {
        return citas2;
    }

    public void setCitas2(Integer citas2) {
        this.citas2 = citas2;
    }

    public Integer getIngresos() {
        return ingresos;
    }

    public void setIngresos(Integer ingresos) {
        this.ingresos = ingresos;
    }
}