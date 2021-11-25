package com.juliovazquez.hsclinic.Activities.Usuario;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.juliovazquez.hsclinic.Pojos.RetrofitCitasCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitDashboard;
import com.juliovazquez.hsclinic.Pojos.RetrofitEvents;
import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
import com.juliovazquez.hsclinic.Pojos.RetrofitIngresosCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacientesCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.Pojos.RetrofitUser;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.EventsServices;
import com.juliovazquez.hsclinic.Services.GraficasServices;
import com.juliovazquez.hsclinic.Services.HistoriasService;
import com.juliovazquez.hsclinic.Services.PacientsServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.ServicesServices;
import com.juliovazquez.hsclinic.Services.UsersServices;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Splash_Screen extends AppCompatActivity {

    String idEvent = null;
    TextView txtVersion;
    String clinica = "", rolUser = "", idUser = "", rol = "Admin";
    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        clinica = Tools_Users_Defaults.user_clinic(Activity_Splash_Screen.this);
        rolUser = Tools_Users_Defaults.user_name_rol(Activity_Splash_Screen.this);
        idUser = String.valueOf(Tools_Users_Defaults.user_id(Activity_Splash_Screen.this));
        if (Tools_Users_Defaults.user_rol(Activity_Splash_Screen.this).equals("Individual")) rol = "Otro";
        Tools_Users_Defaults.setEventFlag(Activity_Splash_Screen.this, false);
        animationView = findViewById(R.id.animationView);
        txtVersion = findViewById(R.id.txtVersion);
        if (getIntent().hasExtra("ID")) {
            idEvent = getIntent().getStringExtra("ID");
        }
        checkPreferences();
    }

    public void checkPreferences() {
        if (Tools_Users_Defaults.is_user_logged_in(Activity_Splash_Screen.this)) {
            if (Tool_Internet.isOnlineNet()) {
                pacientsService();
            } else {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Splash_Screen.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        checkPreferences();
                    }
                })
                .setCancelButton("Salir", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        System.exit(0 );
                    }
                }).show();
            }
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() { @Override public void run() {
                goLoginScreen();
            } }, 1000);
        }
    }

    public void pacientsService() {
        PacientsServices service = RetrofitClientInstance.getRetrofitInstance().create(PacientsServices.class);
        Call<List<RetrofitPacient>> call;
        if (Tools_Users_Defaults.user_rol(Activity_Splash_Screen.this).equals("Individual")) {
            call = service.getPacientsInd(clinica, idUser);
        }else {
            call = service.getAllPacients(clinica);
        }
        call.enqueue(new Callback<List<RetrofitPacient>>() {
            @Override
            public void onResponse(Call<List<RetrofitPacient>> call, Response<List<RetrofitPacient>> response) {
                if (response.code() == 200){
                    RetrofitPacient.borrar_instancia();
                    RetrofitPacient.crear_instancia(response.body());
                }
                userService();
            }
            @Override
            public void onFailure(Call<List<RetrofitPacient>> call, Throwable t) {
                userService();
            }
        });
    }

    public void userService() {
        UsersServices service = RetrofitClientInstance.getRetrofitInstance().create(UsersServices.class);
        Call<List<RetrofitUser>> call = service.getUsers(clinica);
        call.enqueue(new Callback<List<RetrofitUser>>() {
            @Override
            public void onResponse(Call<List<RetrofitUser>> call, Response<List<RetrofitUser>> response) {
                if (response.code() == 200){
                    RetrofitUser.borrar_instancia();
                    RetrofitUser.crear_instancia(response.body());
                }
                servicesService();
            }
            @Override
            public void onFailure(Call<List<RetrofitUser>> call, Throwable t) {
                servicesService();
            }
        });
    }

    public void servicesService() {
        ServicesServices service = RetrofitClientInstance.getRetrofitInstance().create(ServicesServices.class);
        Call<List<RetrofitServices>> call = service.getAllServices(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitServices>>() {
            @Override
            public void onResponse(Call<List<RetrofitServices>> call, Response<List<RetrofitServices>> response) {
                if (response.code() == 200){
                    RetrofitServices.borrar_instancia();
                    RetrofitServices.crear_instancia(response.body());
                }
                getEvents();
            }
            @Override
            public void onFailure(Call<List<RetrofitServices>> call, Throwable t) {
                getEvents();
            }
        });
    }

    public void getEvents() {
        EventsServices service = RetrofitClientInstance.getRetrofitInstance().create(EventsServices.class);
        Call<List<RetrofitEvents>> call;
        if (Tools_Users_Defaults.user_rol(Activity_Splash_Screen.this).equals("Admin") || Tools_Users_Defaults.user_rol(Activity_Splash_Screen.this).equals("Secretaria")) {
            call = service.getAllEvents(clinica);
        } else {
            call = service.getEventsEspecialista(clinica, String.valueOf(idUser));
        }
        call.enqueue(new Callback<List<RetrofitEvents>>() {
            @Override
            public void onResponse(Call<List<RetrofitEvents>> call, Response<List<RetrofitEvents>> response) {
                if (response.code() == 200){
                    RetrofitEvents.borrar_instancia();
                    RetrofitEvents.crear_instancia(response.body());
                } else {
                    RetrofitEvents.borrar_instancia();
                }
                getDashboard();
            }
            @Override
            public void onFailure(Call<List<RetrofitEvents>> call, Throwable t) {
                getDashboard();
            }
        });
    }

    public void getDashboard() {
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitDashboard>> call = service.getDashboard(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitDashboard>>() {
            @Override
            public void onResponse(Call<List<RetrofitDashboard>> call, Response<List<RetrofitDashboard>> response) {
                if (response.code() == 200){
                    RetrofitDashboard.borrar_instancia();
                    RetrofitDashboard.crear_instancia(response.body());
                } else {
                    RetrofitEvents.borrar_instancia();
                }
                getHistorias();
            }
            @Override
            public void onFailure(Call<List<RetrofitDashboard>> call, Throwable t) {
                getHistorias();
            }
        });
    }

    public void getHistorias() {
        HistoriasService service = RetrofitClientInstance.getRetrofitInstance().create(HistoriasService.class);
        Call<List<RetrofitHistorias>> call = service.getHistorias(clinica, idUser, rolUser);
        call.enqueue(new Callback<List<RetrofitHistorias>>() {
            @Override
            public void onResponse(Call<List<RetrofitHistorias>> call, Response<List<RetrofitHistorias>> response) {
                if (response.code() == 200){
                    RetrofitHistorias.borrar_instancia();
                    RetrofitHistorias.crear_instancia(response.body());
                } else {
                    RetrofitEvents.borrar_instancia();
                }
                getChartsPacientes();
            }
            @Override
            public void onFailure(Call<List<RetrofitHistorias>> call, Throwable t) {
                getChartsPacientes();
            }
        });
    }

    public void getChartsPacientes() {
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitPacientesCharts>> call = service.getChartsPacients(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitPacientesCharts>>() {
            @Override
            public void onResponse(Call<List<RetrofitPacientesCharts>> call, Response<List<RetrofitPacientesCharts>> response) {
                if (response.code() == 200){
                    RetrofitPacientesCharts.borrar_instancia();
                    RetrofitPacientesCharts.crear_instancia(response.body());
                } else {
                    RetrofitPacientesCharts.borrar_instancia();
                }
                getChartsCitas();
            }
            @Override
            public void onFailure(Call<List<RetrofitPacientesCharts>> call, Throwable t) {
                getChartsCitas();
            }
        });
    }

    public void getChartsCitas() {
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitCitasCharts>> call = service.getChartsCitas(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitCitasCharts>>() {
            @Override
            public void onResponse(Call<List<RetrofitCitasCharts>> call, Response<List<RetrofitCitasCharts>> response) {
                if (response.code() == 200){
                    RetrofitCitasCharts.borrar_instancia();
                    RetrofitCitasCharts.crear_instancia(response.body());
                } else {
                    RetrofitCitasCharts.borrar_instancia();
                }
                getChartsIngresos();
            }
            @Override
            public void onFailure(Call<List<RetrofitCitasCharts>> call, Throwable t) {
                getChartsIngresos();
            }
        });
    }

    public void getChartsIngresos() {
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitIngresosCharts>> call = service.getChartsIngresos(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitIngresosCharts>>() {
            @Override
            public void onResponse(Call<List<RetrofitIngresosCharts>> call, Response<List<RetrofitIngresosCharts>> response) {
                if (response.code() == 200){
                    RetrofitIngresosCharts.borrar_instancia();
                    RetrofitIngresosCharts.crear_instancia(response.body());
                } else {
                    RetrofitIngresosCharts.borrar_instancia();
                }
                goMainScreen();
            }
            @Override
            public void onFailure(Call<List<RetrofitIngresosCharts>> call, Throwable t) {
                goMainScreen();
            }
        });
    }

    public void goLoginScreen() {
        Intent intent = new Intent(this, Activity_Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void goMainScreen() {
        Intent intent = new Intent(this, Activity_Menu_Principal.class);
        intent.putExtra("ID_EVENT", idEvent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}