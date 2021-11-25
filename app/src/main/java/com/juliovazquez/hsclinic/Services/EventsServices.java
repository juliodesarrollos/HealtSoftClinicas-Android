package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitEvents;
import com.juliovazquez.hsclinic.Pojos.RetrofitFullEvent;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface EventsServices {
    @POST("API/AClinic/create/evento.php")
    @FormUrlEncoded
    Call <RetrofitMessage> addEvent(@Field("clinic") String clinic,
                                    @Field("start") String start,
                                    @Field("title") String idServicio,
                                    @Field("description") String comentario,
                                    @Field("color") String color,
                                    @Field("end") String end,
                                    @Field("idPaciente") String idPaciente,
                                    @Field("idEspecialista") String idEspecialista);

    @POST("API/AClinic/delete/evento.php")
    @FormUrlEncoded
    Call<RetrofitMessage> deleteEvent(@Field("clinic") String clinic,
                                      @Field("id") String id);

    @POST("API/AClinic/read/evento_id.php")
    @FormUrlEncoded
    Call<List<RetrofitFullEvent>> getEvent(@Field("clinic") String clinic,
                                           @Field("id") String id);

    @POST("API/AClinic/read/eventosAdmin.php")
    @FormUrlEncoded
    Call<List<RetrofitEvents>> getAllEvents(@Field("clinic") String clinic);

    @POST("API/AClinic/read/eventosUser.php")
    @FormUrlEncoded
    Call<List<RetrofitEvents>> getEventsEspecialista(@Field("clinic") String clinic,
                                                     @Field("id") String id);

    @POST("API/AClinic/update/status_e.php")
    @FormUrlEncoded
    Call<RetrofitMessage> updateStatus(@Field("clinic") String clinic,
                                       @Field("estatus") String estatus,
                                       @Field("id") String id);

    @POST("API/AClinic/update/evento.php")
    @FormUrlEncoded
    Call<RetrofitMessage> updateEvent(@Field("clinic") String clinic,
                                      @Field("start") String start,
                                      @Field("title") String title,
                                      @Field("description") String description,
                                      @Field("color") String color,
                                      @Field("end") String end,
                                      @Field("idPaciente") String idPaciente,
                                      @Field("idEspecialista") String idEspecialista,
                                      @Field("id") String id);
}