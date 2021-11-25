package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitSignos;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SignosServices {
    @POST("API/AClinic/read/signos.php")
    @FormUrlEncoded
    Call<List<RetrofitSignos>> getSignos(@Field("clinic") String clinic,
                                        @Field("id") String id);

    @POST("API/AClinic/create/signos.php")
    @FormUrlEncoded
    Call<RetrofitMessage> addSignos(@Field("clinic") String clinic,
                                  @Field("temperatura") String temperatura,
                                  @Field("pulso") String pulso,
                                  @Field("glucosa") String so2,
                                    @Field("idPaciente") String idPaciente,
                                    @Field("presionSistolica") String ps,
                                    @Field("presionDiastolica") String pd,
                                  @Field("frecuenciaRespiratoria") String fr);
}
