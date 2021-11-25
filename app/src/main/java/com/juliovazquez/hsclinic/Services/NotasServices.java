package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitHCPX;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitNotas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NotasServices {
    @POST("API/AClinic/read/notas.php")
    @FormUrlEncoded
    Call<List<RetrofitNotas>> getNotas(@Field("clinic") String clinic,
                                       @Field("id") String id);

    @POST("API/AClinic/create/nota.php")
    @FormUrlEncoded
    Call<RetrofitMessage> addNota(@Field("clinic") String clinic,
                                        @Field("nota") String nota,
                                        @Field("idPaciente") String idPaciente);
}
