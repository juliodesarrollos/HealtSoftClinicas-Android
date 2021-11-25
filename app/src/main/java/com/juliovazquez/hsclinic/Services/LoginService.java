package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitLogin;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginService {
        @POST("API/AClinic/read/login.php")
        @FormUrlEncoded
        Call<List<RetrofitLogin>> login(@Field("clinic") String clinic,
                                        @Field("mail") String mail,
                                        @Field("pass") String pass);
}

