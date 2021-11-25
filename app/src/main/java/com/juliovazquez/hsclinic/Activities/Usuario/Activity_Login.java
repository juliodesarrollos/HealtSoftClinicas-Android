package com.juliovazquez.hsclinic.Activities.Usuario;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.juliovazquez.hsclinic.Activities.Historias.Activity_Preguntas;
import com.juliovazquez.hsclinic.Pojos.RetrofitCitasCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitDashboard;
import com.juliovazquez.hsclinic.Pojos.RetrofitEvents;
import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
import com.juliovazquez.hsclinic.Pojos.RetrofitIngresosCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitLogin;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacientesCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.Pojos.RetrofitUser;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.EventsServices;
import com.juliovazquez.hsclinic.Services.GraficasServices;
import com.juliovazquez.hsclinic.Services.HistoriasService;
import com.juliovazquez.hsclinic.Services.LoginService;
import com.juliovazquez.hsclinic.Services.PacientsServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.ServicesServices;
import com.juliovazquez.hsclinic.Services.UsersServices;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
import com.juliovazquez.hsclinic.Tools.Tool_Sweet_Alert;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Login extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    EditText txtCorreo;
    EditText txtPassword;
    EditText txtClinic;
    CheckBox chkSesion;
    String password;
    String correo;
    String clinica = "", idUser = "", rol = "Admin";
    Context context;
    Spinner spClinicas;
    Tool_Progress_Dialog progress_dialog;

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("");

        progress_dialog = new Tool_Progress_Dialog(Activity_Login.this);
        context = getApplicationContext();
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);
        txtClinic = findViewById(R.id.txtClinic);
        btnLogin = findViewById(R.id.btnLogin);
        chkSesion = findViewById(R.id.chkSesion);
        spClinicas = findViewById(R.id.spClinica);
        btnLogin.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                btnLogin.setEnabled(false);
                if (verificar_campos()) {
                    correo = txtCorreo.getText().toString();
                    password = txtPassword.getText().toString();
                    clinica = txtClinic.getText().toString();
                    internetLogin();
                } else {
                    btnLogin.setEnabled(true);
                    progress_dialog.dismissDialog();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, getString(R.string.presionar_otra_vez), Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }

    private boolean verificar_campos() {
        progress_dialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtCorreo.getText())) {
            txtCorreo.setError(getString(R.string.campo_requerido));
            txtCorreo.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(txtPassword.getText())) {
            txtPassword.setError(getString(R.string.campo_requerido));
            txtClinic.requestFocus();
            return false;
        }
        return true;
    }

    public void goMainScreen() {
        progress_dialog.dismissDialog();
        Intent intent = new Intent(this, Activity_Menu_Principal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void internetLogin() {
        if (Tool_Internet.isOnlineNet()) {
            progress_dialog.dismissDialog();
            loginService();
        } else {
            progress_dialog.dismissDialog();
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Login.this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        internetLogin();
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
    }

    public void loginService() {
        progress_dialog.ShowProgressDialog();
        LoginService service = RetrofitClientInstance.getRetrofitInstance().create(LoginService.class);
        Call<List<RetrofitLogin>> call = service.login(clinica, correo, password);
        call.enqueue(new Callback<List<RetrofitLogin>>() {
            @Override
            public void onResponse(Call<List<RetrofitLogin>> call, Response<List<RetrofitLogin>> response) {
                btnLogin.setEnabled(true);
                if (response.code() == 200){
                    Tools_Users_Defaults.borrar_sesion(Activity_Login.this);
                    Tools_Users_Defaults.guardar_sesion(Activity_Login.this, response.body().get(0), clinica);
                    idUser = String.valueOf(Tools_Users_Defaults.user_id(Activity_Login.this));
                    if (Tools_Users_Defaults.user_rol(Activity_Login.this).equals("Individual")) rol = "Otro";
                    pacientsService();
                } else if (response.code() == 404){
                    Toasty.error(Activity_Login.this, getResources().getString(R.string.verificar_correo), Toasty.LENGTH_LONG).show();
                    txtCorreo.setError(getResources().getString(R.string.verificar_correo));
                    progress_dialog.dismissDialog();
                }else if (response.code() == 405){
                    Toasty.error(Activity_Login.this, getResources().getString(R.string.verificar_contraseña), Toasty.LENGTH_LONG).show();
                    txtPassword.setError(getResources().getString(R.string.verificar_contraseña));
                    progress_dialog.dismissDialog();
                }else if (response.code() == 666){
                    Toasty.error(Activity_Login.this, getResources().getString(R.string.verificar_clinica), Toasty.LENGTH_LONG).show();
                    txtClinic.setError(getResources().getString(R.string.verificar_clinica));
                    progress_dialog.dismissDialog();
                }else if (response.code() == 505){
                    Toasty.error(Activity_Login.this, getResources().getString(R.string.cuenta_inactiva), Toasty.LENGTH_LONG).show();
                    progress_dialog.dismissDialog();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitLogin>> call, Throwable t) {
                btnLogin.setEnabled(true);
                progress_dialog.dismissDialog();
                Tool_Sweet_Alert.ERROR_TYPE(getApplicationContext(), "Ups! Algo salió mal");
            }
        });
    }

    public void pacientsService() {
        PacientsServices service = RetrofitClientInstance.getRetrofitInstance().create(PacientsServices.class);
        Call<List<RetrofitPacient>> call;
        if (Tools_Users_Defaults.user_rol(Activity_Login.this).equals("Individual")) {
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
        if (Tools_Users_Defaults.user_rol(Activity_Login.this).equals("Admin") || Tools_Users_Defaults.user_rol(Activity_Login.this).equals("Secretaria")) {
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
        Call<List<RetrofitHistorias>> call = service.getHistorias(clinica, idUser, rol);
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
}