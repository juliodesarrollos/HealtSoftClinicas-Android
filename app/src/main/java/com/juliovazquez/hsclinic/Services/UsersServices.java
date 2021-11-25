package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UsersServices {
    @POST("API/AClinic/create/usuario.php")
    @FormUrlEncoded
    Call<RetrofitMessage> addUser(@Field("clinic") String clinic,
                                   @Field("name") String name,
                                   @Field("rol") String rol,
                                   @Field("email") String email,
                                   @Field("password") String password);

    @POST("API/AClinic/read/usuarios.php")
    @FormUrlEncoded
    Call<List<RetrofitUser>> getUsers(@Field("clinic") String clinic);

    @POST("API/AClinic/update/usuario.php")
    @FormUrlEncoded
    Call<RetrofitMessage> editUser(@Field("clinic") String clinic,
                                   @Field("id") String id,
                                  @Field("name") String name,
                                  @Field("rol") String rol,
                                  @Field("email") String email,
                                  @Field("password") String password);
}
