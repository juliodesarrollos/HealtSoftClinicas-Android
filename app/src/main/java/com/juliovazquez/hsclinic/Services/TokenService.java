package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenService {
    @POST("API/AClinic/create/token.php")
    @FormUrlEncoded
    Call<RetrofitMessage> tokenActions(@Field("clinic") String clinic,
                                       @Field("id") String id,
                                       @Field("device") String device,
                                       @Field("token") String token,
                                       @Field("so") String so);
}
