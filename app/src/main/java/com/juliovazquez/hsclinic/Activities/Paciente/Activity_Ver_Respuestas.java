package com.juliovazquez.hsclinic.Activities.Paciente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.juliovazquez.hsclinic.Adapters.Adapter_Respuestas;
import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
import com.juliovazquez.hsclinic.Pojos.RetrofitRespuesta;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.ArchivoServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Ver_Respuestas extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, Adapter_Respuestas.OnRespuestaListener {

    String clinica = "", idHCPX = "", historia = "";
    Adapter_Respuestas adapter_respuestas;
    RecyclerView rvRespuestas;
    List<RetrofitRespuesta> respuestas = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    Toolbar toolbar_Respuestas;
    TextView txtSinRespuestas, title;
    Tool_Progress_Dialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_respuestas);
        clinica = getIntent().getStringExtra("clinic");
        historia = getIntent().getStringExtra("historia");
        idHCPX = getIntent().getStringExtra("id");
        progress_dialog = new Tool_Progress_Dialog(Activity_Ver_Respuestas.this);
        txtSinRespuestas = findViewById(R.id.txtSinRespuestas);
        toolbar_Respuestas = findViewById(R.id.toolbar_respuestas);
        title  = toolbar_Respuestas.findViewById(R.id.title_respuestas);
        rvRespuestas = findViewById(R.id.rvRespuestas);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_Ver_Respuestas.this, 1);
        rvRespuestas.setLayoutManager(gridLayoutManager);
        refreshLayout = findViewById(R.id.refreshRespuestas);
        this.setSupportActionBar(toolbar_Respuestas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        title.setText(historia);
        setTitle("");
        refreshLayout.setOnRefreshListener(this::onRefresh);
        internetGet();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    public void mostrar_respuestas() {
        if (RetrofitHistorias.recuperar_instancia() != null   ) {
            respuestas.clear();
            respuestas = RetrofitRespuesta.recuperar_instancia();
            adapter_respuestas = new Adapter_Respuestas(respuestas, this);
            rvRespuestas.setAdapter(adapter_respuestas);
            rvRespuestas.setHasFixedSize(true);
            txtSinRespuestas.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinRespuestas.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            respuestas_service();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Ver_Respuestas.this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetGet();
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

    public void respuestas_service() {
        progress_dialog.ShowProgressDialog();
        ArchivoServices service = RetrofitClientInstance.getRetrofitInstance().create(ArchivoServices.class);
        Call<List<RetrofitRespuesta>> call = service.getRespuestas(clinica, idHCPX);
        call.enqueue(new Callback<List<RetrofitRespuesta>>() {
            @Override
            public void onResponse(Call<List<RetrofitRespuesta>> call, Response<List<RetrofitRespuesta>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitRespuesta.borrar_instancia();
                    RetrofitRespuesta.crear_instancia(response.body());
                    refreshLayout.setRefreshing(false);
                    mostrar_respuestas();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitRespuesta>> call, Throwable t) {
                progress_dialog.dismissDialog();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void edit_respuesta (int positionn) {
        if (!Tools_Users_Defaults.user_rol(Activity_Ver_Respuestas.this).equals("Practicante")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment_Editar_Respuesta d = new Fragment_Editar_Respuesta();
            d.pregunta = respuestas.get(positionn).getPregunta();
            d.respuesta = respuestas.get(positionn).getRespuesta();
            d.idExpediente = respuestas.get(positionn).getId().toString();
            d.clinica = clinica;
            d.show(fragmentManager, "tag");
            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    super.onFragmentViewDestroyed(fm, f);
                    if (d.anyRespuesta) respuestas_service();
                    fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                }
            }, false);
        }
    }

    @Override
    public void onRespuestaClick(int position) {
        edit_respuesta(position);
    }
}