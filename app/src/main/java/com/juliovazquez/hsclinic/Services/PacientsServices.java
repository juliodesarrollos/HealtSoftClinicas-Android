package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitLogin;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PacientsServices {
    @POST("API/AClinic/read/pacientesAdmin.php")
    @FormUrlEncoded
    Call<List<RetrofitPacient>> getAllPacients(@Field("clinic") String clinic);

    @POST("API/AClinic/read/pacientesUser.php")
    @FormUrlEncoded
    Call<List<RetrofitPacient>> getPacientsInd(@Field("clinic") String clinic,
                                               @Field("id") String id);

    @Multipart
    @POST("API/AClinic/update/paciente.php")
    Call<RetrofitMessage> editPacient(@Part("clinic") RequestBody clinic,
                                     @Part("idPaciente") RequestBody idPaciente,
                                     @Part("nombre") RequestBody nombre,
                                     @Part("correo") RequestBody correo,
                                     @Part("telefono") RequestBody telefono,
                                     @Part("fechaNacimiento") RequestBody fechaNacimiento,
                                     @Part MultipartBody.Part file);

    @Multipart
    @POST("API/AClinic/create/paciente.php")
    Call<RetrofitMessage> addPacient(@Part("clinic") RequestBody clinic,
                                     @Part("idEspecialista") RequestBody idEspecialista,
                                     @Part("nombre") RequestBody nombre,
                                     @Part("correo") RequestBody correo,
                                     @Part("telefono") RequestBody telefono,
                                     @Part("fechaNacimiento") RequestBody fechaNacimiento,
                                     @Part MultipartBody.Part file);
}
