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
import com.juliovazquez.hsclinic.Adapters.Adapter_Signos;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.Pojos.RetrofitSignos;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.SignosServices;
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
 * Use the {@link Fragment_Signos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Signos extends Fragment implements Adapter_Signos.OnSignosListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String clinica = "";
    Adapter_Signos adapter_signos;
    RecyclerView rvSignos;
    List<RetrofitSignos> signos = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    TextView txtSinSignos;
    String idPaciente = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Signos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Signos.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Signos newInstance(String param1, String param2) {
        Fragment_Signos fragment = new Fragment_Signos();
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
        return inflater.inflate(R.layout.fragment_signos, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        rvSignos = getView().findViewById(R.id.rvSignos);
        txtSinSignos = getView().findViewById(R.id.txtSinSignos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvSignos.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshSignos);
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
            case R.id.fab: agregar_signos(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    @Override
    public void onSignosClick(int position, int id) {
        switch (id) {
            case R.id.btnSignos: Toasty.info(getContext(), "Por el momento no se pueden editar signos vitales", Toasty.LENGTH_SHORT).show(); break;
        }
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            signosService();
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

    public void signosService() {
        SignosServices service = RetrofitClientInstance.getRetrofitInstance().create(SignosServices.class);
        Call<List<RetrofitSignos>> call = service.getSignos(clinica, idPaciente);
        call.enqueue(new Callback<List<RetrofitSignos>>() {
            @Override
            public void onResponse(Call<List<RetrofitSignos>> call, Response<List<RetrofitSignos>> response) {
                refreshLayout.setRefreshing(false);
                if (response.code() == 200){
                    RetrofitSignos.borrar_instancia();
                    RetrofitSignos.crear_instancia(response.body());
                    showSignos();
                } else if (response.code() == 404) {
                    Toasty.info(getContext(), "No se encontraron signos vitales", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitSignos>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void showSignos() {
        if (RetrofitServices.recuperar_instancia() != null   ) {
            signos.clear();
            signos = RetrofitSignos.recuperar_instancia();
            adapter_signos = new Adapter_Signos(getContext(), signos, this::onSignosClick);
            rvSignos.setAdapter(adapter_signos);
            rvSignos.setHasFixedSize(true);
            txtSinSignos.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinSignos.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void agregar_signos() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Signos agregar_signos = new Fragment_Agregar_Signos();
        agregar_signos.show(fragmentManager, "tag");
        agregar_signos.idPaciente = idPaciente;
        agregar_signos.clinica = clinica;
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (agregar_signos.anySignos) internetGet();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }
}