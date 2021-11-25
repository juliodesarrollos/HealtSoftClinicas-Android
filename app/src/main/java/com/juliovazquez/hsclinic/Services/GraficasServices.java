package com.juliovazquez.hsclinic.Services;

import com.juliovazquez.hsclinic.Pojos.RetrofitCitasCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitDashboard;
import com.juliovazquez.hsclinic.Pojos.RetrofitIngresosCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacientesCharts;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GraficasServices {
    @POST("API/AClinic/read/citas-graficas.php")
    @FormUrlEncoded
    Call<List<RetrofitCitasCharts>> getChartsCitas(@Field("clinic") String clinic,
                                                   @Field("id") String id,
                                                   @Field("rol") String rol);

    @POST("API/AClinic/read/ingresos-graficas.php")
    @FormUrlEncoded
    Call<List<RetrofitIngresosCharts>> getChartsIngresos(@Field("clinic") String clinic,
                                                         @Field("id") String id,
                                                         @Field("rol") String rol);

    @POST("API/AClinic/read/pacientes-graficas.php")
    @FormUrlEncoded
    Call<List<RetrofitPacientesCharts>> getChartsPacients(@Field("clinic") String clinic,
                                                          @Field("id") String id,
                                                          @Field("rol") String rol);

    @POST("API/AClinic/read/dashboard.php")
    @FormUrlEncoded
    Call<List<RetrofitDashboard>> getDashboard(@Field("clinic") String clinic,
                                               @Field("id") String id,
                                               @Field("rol") String rol);
}
