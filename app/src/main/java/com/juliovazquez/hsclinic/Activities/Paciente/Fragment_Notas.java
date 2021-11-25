package com.juliovazquez.hsclinic.Activities.Paciente;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tibolte.agendacalendarview.widgets.FloatingActionButton;
import com.juliovazquez.hsclinic.Adapters.Adapter_Notas;
import com.juliovazquez.hsclinic.Pojos.RetrofitNotas;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.NotasServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Notas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Notas extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String clinica = "";
    Adapter_Notas adapter_notas;
    RecyclerView rvNotas;
    List<RetrofitNotas> notas = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    TextView txtSinNotas;
    String idPaciennte = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Notas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Notas.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Notas newInstance(String param1, String param2) {
        Fragment_Notas fragment = new Fragment_Notas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notas, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        rvNotas = getView().findViewById(R.id.rvNotas);
        txtSinNotas = getView().findViewById(R.id.txtSinNotas);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvNotas.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshNotas);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setAlpha(0.80f);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        fab.setOnClickListener(this::onClick);
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Practicante")) fab.setVisibility(View.GONE);
        notasService();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: agregar_notas(); break;
        }
    }

    @Override
    public void onRefresh() {

    }

    public void notasService() {
        NotasServices service = RetrofitClientInstance.getRetrofitInstance().create(NotasServices.class);
        Call<List<RetrofitNotas>> call = service.getNotas(clinica, idPaciennte);
        call.enqueue(new Callback<List<RetrofitNotas>>() {
            @Override
            public void onResponse(Call<List<RetrofitNotas>> call, Response<List<RetrofitNotas>> response) {
                refreshLayout.setRefreshing(false);
                if (response.code() == 200){
                    RetrofitNotas.borrar_instancia();
                    RetrofitNotas.crear_instancia(response.body());
                    showNotas();
                } else if (response.code() == 404) {
                    Toasty.info(getContext(), "No se encontraron notas", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitNotas>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                showNotas();
            }
        });
    }

    public void showNotas() {
        if (RetrofitServices.recuperar_instancia() != null   ) {
            notas.clear();
            notas = RetrofitNotas.recuperar_instancia();
            adapter_notas = new Adapter_Notas(getContext(), notas);
            rvNotas.setAdapter(adapter_notas);
            rvNotas.setHasFixedSize(true);
            txtSinNotas.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinNotas.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void agregar_notas() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Notas agregar_notas = new Fragment_Agregar_Notas();
        agregar_notas.clinica = clinica;
        agregar_notas.idPaciente = idPaciennte;
        agregar_notas.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (agregar_notas.anyNota) notasService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            notasService();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
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
}