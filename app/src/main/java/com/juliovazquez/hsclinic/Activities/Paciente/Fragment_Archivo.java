package com.juliovazquez.hsclinic.Activities.Paciente;

import android.content.Intent;
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
import com.juliovazquez.hsclinic.Activities.Historias.Activity_Preguntas;
import com.juliovazquez.hsclinic.Adapters.Adapter_HCPX;
import com.juliovazquez.hsclinic.Pojos.RetrofitHCPX;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.ArchivoServices;
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
 * Use the {@link Fragment_Archivo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Archivo extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, Adapter_HCPX.OnHCPXListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String clinica = "";
    Adapter_HCPX adapter_hcpx;
    RecyclerView rvHcpxes;
    List<RetrofitHCPX> hcpxes = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    TextView txtSinHcpxes;
    String idPaciente = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Archivo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Archivo.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Archivo newInstance(String param1, String param2) {
        Fragment_Archivo fragment = new Fragment_Archivo();
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
        return inflater.inflate(R.layout.fragment_archivo, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        rvHcpxes = getView().findViewById(R.id.rvHcpxes);
        txtSinHcpxes = getView().findViewById(R.id.txtSinHcpxes);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvHcpxes.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshHcpxes);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setAlpha(0.80f);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        fab.setOnClickListener(this::onClick);
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Practicante")) fab.setVisibility(View.GONE);
        internetGet();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: agregar_archivo(); break;
        }
    }

    @Override
    public void onHCPXClick(int position, int id) {
        switch (id) {
            case R.id.btnVer: ver_respuestas(position); break;
            case R.id.btnHistoria: Toasty.info(getContext(), "Acción no disponible", Toasty.LENGTH_SHORT).show(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            hcpxService();
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

    public void hcpxService() {
        ArchivoServices service = RetrofitClientInstance.getRetrofitInstance().create(ArchivoServices.class);
        Call<List<RetrofitHCPX>> call = service.getHCPX(clinica, idPaciente);
        call.enqueue(new Callback<List<RetrofitHCPX>>() {
            @Override
            public void onResponse(Call<List<RetrofitHCPX>> call, Response<List<RetrofitHCPX>> response) {
                refreshLayout.setRefreshing(false);
                if (response.code() == 200){
                    RetrofitHCPX.borrar_instancia();
                    RetrofitHCPX.crear_instancia(response.body());
                    showArchivo();
                } else if (response.code() == 404) {
                    Toasty.info(getContext(), "No se encontraron historias clinicas", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitHCPX>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void ver_respuestas (int position) {
        Intent i =  new Intent(getContext(), Activity_Ver_Respuestas.class);
        i.putExtra("id", String.valueOf(hcpxes.get(position).getId()));
        i.putExtra("historia", hcpxes.get(position).getNombre());
        i.putExtra("clinic", clinica);
        startActivity(i);
    }

    public void showArchivo() {
        if (RetrofitHCPX.recuperar_instancia() != null   ) {
            hcpxes.clear();
            hcpxes = RetrofitHCPX.recuperar_instancia();
            adapter_hcpx = new Adapter_HCPX(getContext(), hcpxes, this);
            rvHcpxes.setAdapter(adapter_hcpx);
            rvHcpxes.setHasFixedSize(true);
            txtSinHcpxes.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinHcpxes.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void agregar_archivo() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Archivo agregar_archivo = new Fragment_Agregar_Archivo();
        agregar_archivo.clinica = clinica;
        agregar_archivo.idPaciente = idPaciente;
        agregar_archivo.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (agregar_archivo.anyArchivo) internetGet();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }
}