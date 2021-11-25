package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitPreguntas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HistoriasService {
    @POST("API/AClinic/read/hc.php")
    @FormUrlEncoded
    Call<List<RetrofitHistorias>> getHistorias(@Field("clinic") String clinic,
                                               @Field("id") String id,
                                               @Field("rol") String rol);

    @POST("API/AClinic/create/HC.php")
    @FormUrlEncoded
    Call<RetrofitMessage> addHistoria(@Field("clinic") String clinic,
                                      @Field("id") String id,
                                      @Field("nombre") String nombre);

    @POST("API/AClinic/update/hc.php")
    @FormUrlEncoded
    Call<RetrofitMessage> editHistoria(@Field("clinic") String clinic,
                                      @Field("id") String id,
                                      @Field("nombre") String nombre);

    @POST("API/AClinic/read/hc_p.php")
    @FormUrlEncoded
    Call<List<RetrofitPreguntas>> getPreguntas(@Field("clinic") String clinic,
                                               @Field("id") String id);

    @POST("API/AClinic/create/pregunta.php")
    @FormUrlEncoded
    Call<RetrofitMessage> addPregunta(@Field("clinic") String clinic,
                                      @Field("idHC") String idHC,
                                      @Field("nombre") String nombre);

    @POST("API/AClinic/update/pregunta.php")
    @FormUrlEncoded
    Call<RetrofitMessage> editPregunta(@Field("clinic") String clinic,
                                       @Field("id") String id,
                                       @Field("pregunta") String pregunta);

    @POST("API/AClinic/update/status_p.php")
    @FormUrlEncoded
    Call<RetrofitMessage> editStatus(@Field("clinic") String clinic,
                                       @Field("id") String id,
                                       @Field("status") String status);
}
