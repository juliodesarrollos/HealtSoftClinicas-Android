package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ServicesServices {
    @POST("API/AClinic/read/servicios.php")
    @FormUrlEncoded
    Call<List<RetrofitServices>> getAllServices(@Field("clinic") String clinic,
                                                @Field("id") String id,
                                                @Field("rol") String rol);

    @POST("API/AClinic/create/servicio.php")
    @FormUrlEncoded
    Call<RetrofitMessage> addService(@Field("clinic") String clinic,
                                     @Field("servicio") String servicio,
                                     @Field("idEspecialista") String idEspecialista,
                                     @Field("descripcion") String descripcion,
                                     @Field("costo") String costo);

    @POST("API/AClinic/update/servicio.php")
    @FormUrlEncoded
    Call<RetrofitMessage> editService(@Field("clinic") String clinic,
                                      @Field("id") String id,
                                      @Field("servicio") String servicio,
                                      @Field("descripcion") String descripcion,
                                      @Field("costo") String costo);
}
