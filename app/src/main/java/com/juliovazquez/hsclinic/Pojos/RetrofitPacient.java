package com.juliovazquez.hsclinic.Pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitPacient {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("fechaNacimiento")
    @Expose
    private String fechaNacimiento;
    @SerializedName("correo")
    @Expose
    private String correo;
    @SerializedName("telefono")
    @Expose
    private Long telefono;
    @SerializedName("fotoUrl")
    @Expose
    private String fotoUrl;
    @SerializedName("sexo")
    @Expose
    private String sexo;

    private static List<RetrofitPacient> pacientes;

    public static void crear_instancia (List<RetrofitPacient> pacientes) {
        RetrofitPacient.pacientes = pacientes;
    }

    public static List<RetrofitPacient> recuperar_instancia () {
        return RetrofitPacient.pacientes;
    }

    public static void borrar_instancia () {
        RetrofitPacient.pacientes = null;
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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
}