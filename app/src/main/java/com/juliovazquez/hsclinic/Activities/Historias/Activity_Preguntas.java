package com.juliovazquez.hsclinic.Activities.Historias;

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
import android.widget.Toast;

import com.github.tibolte.agendacalendarview.widgets.FloatingActionButton;
import com.juliovazquez.hsclinic.Adapters.Adapter_Preguntas;
import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
import com.juliovazquez.hsclinic.Pojos.RetrofitPreguntas;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.HistoriasService;
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

public class Activity_Preguntas extends AppCompatActivity implements Adapter_Preguntas.OnPreguntaListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    String idHC = "", clinica = "", historia = "";
    Adapter_Preguntas adapter_preguntas;
    RecyclerView rvPreguntas;
    List<RetrofitPreguntas> preguntas = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    Toolbar toolbar_preguntas;
    TextView txtSinPreguntas, title;
    Tool_Progress_Dialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);
        historia = getIntent().getStringExtra("HC");
        progress_dialog = new Tool_Progress_Dialog(Activity_Preguntas.this);
        idHC = String.valueOf(getIntent().getIntExtra("id", 0));
        clinica = Tools_Users_Defaults.user_clinic(Activity_Preguntas.this);
        txtSinPreguntas = findViewById(R.id.txtSinPreguntas);
        toolbar_preguntas = findViewById(R.id.toolbar_preguntas);
        title  = toolbar_preguntas.findViewById(R.id.title_preguntas);
        rvPreguntas = findViewById(R.id.rvPreguntas);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Activity_Preguntas.this, 1);
        rvPreguntas.setLayoutManager(gridLayoutManager);
        refreshLayout = findViewById(R.id.refreshPreguntas);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setAlpha(0.80f);
        this.setSupportActionBar(toolbar_preguntas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        title.setText(historia);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        fab.setOnClickListener(this::onClick);
        internetGet();
    }

    @Override
    public void onPreguntaClick(int position) {
        edit_pregunta(preguntas.get(position).getPregunta(), preguntas.get(position).getStatus(), String.valueOf(preguntas.get(position).getId()));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: add_pregunta(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    public void showPreguntas() {
        if (RetrofitHistorias.recuperar_instancia() != null   ) {
            preguntas.clear();
            preguntas = RetrofitPreguntas.recuperar_instancia();
            adapter_preguntas = new Adapter_Preguntas(preguntas, this);
            rvPreguntas.setAdapter(adapter_preguntas);
            rvPreguntas.setHasFixedSize(true);
            txtSinPreguntas.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinPreguntas.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            preguntasService();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Preguntas.this, SweetAlertDialog.WARNING_TYPE);
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

    public void preguntasService() {
        progress_dialog.ShowProgressDialog();
        HistoriasService service = RetrofitClientInstance.getRetrofitInstance().create(HistoriasService.class);
        Call<List<RetrofitPreguntas>> call = service.getPreguntas(clinica, idHC);
        call.enqueue(new Callback<List<RetrofitPreguntas>>() {
            @Override
            public void onResponse(Call<List<RetrofitPreguntas>> call, Response<List<RetrofitPreguntas>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitPreguntas.borrar_instancia();
                    RetrofitPreguntas.crear_instancia(response.body());
                    refreshLayout.setRefreshing(false);
                    showPreguntas();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitPreguntas>> call, Throwable t) {
                progress_dialog.dismissDialog();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void edit_pregunta(String pregunta, String status, String id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment_Editar_Pregunta d = new Fragment_Editar_Pregunta();
        d.status = status;
        d.pregunta = pregunta;
        d.id = id;
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyPregunta) preguntasService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void add_pregunta() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment_Agregar_Pregunta d = new Fragment_Agregar_Pregunta();
        d.idHC = idHC;
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyPregunta) preguntasService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }
}