package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitEstudios;
import com.juliovazquez.hsclinic.Pojos.RetrofitHCPX;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface EstudiosServices {
    @POST("API/AClinic/read/estudios.php")
    @FormUrlEncoded
    Call<List<RetrofitEstudios>> getEstudios(@Field("clinic") String clinic,
                                         @Field("id") String id);

    @Multipart
    @POST("API/AClinic/create/estudio.php")
    Call<RetrofitMessage> addEstudio(@Part("clinic") RequestBody clinic,
                                     @Part("idPaciente") RequestBody idPaciente,
                                     @Part("nombre") RequestBody nombre,
                                     @Part("descripcion") RequestBody descripcion,
                                     @Part MultipartBody.Part file);
}
