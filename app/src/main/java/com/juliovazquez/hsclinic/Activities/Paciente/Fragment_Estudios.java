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
import com.juliovazquez.hsclinic.Adapters.Adapter_Estudios;
import com.juliovazquez.hsclinic.Pojos.RetrofitEstudios;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.EstudiosServices;
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
 * Use the {@link Fragment_Estudios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Estudios extends Fragment implements Adapter_Estudios.OnEstudiosListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String clinica = "";
    Adapter_Estudios adapter_estudios;
    RecyclerView rvEstudios;
    List<RetrofitEstudios> estudios = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    TextView txtSinEstudios;
    String idPaciente = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Estudios() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Estudios.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Estudios newInstance(String param1, String param2) {
        Fragment_Estudios fragment = new Fragment_Estudios();
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
        return inflater.inflate(R.layout.fragment_estudios, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        rvEstudios = getView().findViewById(R.id.rvEstudios);
        txtSinEstudios = getView().findViewById(R.id.txtSinEstudios);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvEstudios.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshEstudios);
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
            case R.id.fab: agregar_estudio(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    @Override
    public void onEstudioClick(int position, int id) {
        switch (id) {
            case R.id.btnHistoria: Toasty.info(getContext(), "Por el momento no se pueden editar estudios", Toasty.LENGTH_SHORT).show(); break;
            case R.id.btnVer: ver_estudio(position); break;
        }
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            estudiosService();
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

    public void ver_estudio (int position) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Ver_Imagen ver_imagen = new Fragment_Ver_Imagen();
        ver_imagen.url = estudios.get(position).getEstudioUrl();
        ver_imagen.show(fragmentManager, "tag");
    }

    public void estudiosService() {
        EstudiosServices service = RetrofitClientInstance.getRetrofitInstance().create(EstudiosServices.class);
        Call<List<RetrofitEstudios>> call = service.getEstudios(clinica, idPaciente);
        call.enqueue(new Callback<List<RetrofitEstudios>>() {
            @Override
            public void onResponse(Call<List<RetrofitEstudios>> call, Response<List<RetrofitEstudios>> response) {
                refreshLayout.setRefreshing(false);
                if (response.code() == 200){
                    RetrofitEstudios.borrar_instancia();
                    RetrofitEstudios.crear_instancia(response.body());
                    showEstudios();
                } else if (response.code() == 404) {
                    Toasty.info(getContext(), "No se encontraron estudios", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitEstudios>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                showEstudios();
            }
        });
    }

    public void showEstudios() {
        if (RetrofitServices.recuperar_instancia() != null   ) {
            estudios.clear();
            estudios = RetrofitEstudios.recuperar_instancia();
            adapter_estudios = new Adapter_Estudios(getContext(), estudios, this::onEstudioClick);
            rvEstudios.setAdapter(adapter_estudios);
            rvEstudios.setHasFixedSize(true);
            txtSinEstudios.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinEstudios.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void agregar_estudio() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Estudio agregar_estudio = new Fragment_Agregar_Estudio();
        agregar_estudio.idPaciente = idPaciente;
        agregar_estudio.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (agregar_estudio.anyEstudio) internetGet();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }
}