package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitHCPX;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitRespuesta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ArchivoServices {
    @POST("API/AClinic/read/hc_px.php")
    @FormUrlEncoded
    Call<List<RetrofitHCPX>> getHCPX(@Field("clinic") String clinic,
                                     @Field("id") String id);

    @POST("API/AClinic/create/hc_px.php")
    @FormUrlEncoded
    Call<RetrofitMessage> addHCPX(@Field("clinic") String clinic,
                                        @Field("idEspecialista") String idEspecialista,
                                        @Field("idPaciente") String idPaciente,
                                        @Field("idHC") String idHC);

    @POST("API/AClinic/read/hc_pxre.php")
    @FormUrlEncoded
    Call<List<RetrofitRespuesta>> getRespuestas(@Field("clinic") String clinic,
                                                @Field("id") String id);

    @POST("API/AClinic/update/respuesta.php")
    @FormUrlEncoded
    Call<RetrofitMessage> editRespuesta(@Field("clinic") String clinic,
                                        @Field("id") String id,
                                        @Field("respuesta") String respuesta);
}
