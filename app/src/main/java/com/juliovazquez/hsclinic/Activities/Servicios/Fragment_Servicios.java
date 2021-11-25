package com.juliovazquez.hsclinic.Activities.Servicios;

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
import com.juliovazquez.hsclinic.Adapters.Adapter_Servicio;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.ServicesServices;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Servicios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Servicios extends Fragment implements Adapter_Servicio.OnServiceListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Adapter_Servicio adapter_servicio;
    RecyclerView rvServicios;
    List<RetrofitServices> servicios = new ArrayList<>();
    List<RetrofitServices> serviciosBusqueda = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    TextView txtSinServicios;
    String clinica = "", idUser = "", rol = "Admin";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Servicios() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Servicios.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Servicios newInstance(String param1, String param2) {
        Fragment_Servicios fragment = new Fragment_Servicios();
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
        return inflater.inflate(R.layout.fragment_servicios, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        idUser = String.valueOf(Tools_Users_Defaults.user_id(getContext()));
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Individual")) rol = "Otro";
        txtSinServicios = getView().findViewById(R.id.txtSinServicios);
        rvServicios = getView().findViewById(R.id.rvServicios);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvServicios.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshServicios);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setAlpha(0.80f);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        fab.setOnClickListener(this::onClick);
        showServices();
    }

    @Override
    public void onServiceClick(int position) {
        editService(position);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: addService(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    public void showServices() {
        if (RetrofitServices.recuperar_instancia() != null   ) {
            servicios.clear();
            servicios = RetrofitServices.recuperar_instancia();
            llenar_busqueda();
            adapter_servicio = new Adapter_Servicio(serviciosBusqueda, this);
            rvServicios.setAdapter(adapter_servicio);
            rvServicios.setHasFixedSize(true);
            txtSinServicios.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinServicios.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            servicesService();
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

    public void servicesService() {
        ServicesServices service = RetrofitClientInstance.getRetrofitInstance().create(ServicesServices.class);
        Call<List<RetrofitServices>> call = service.getAllServices(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitServices>>() {
            @Override
            public void onResponse(Call<List<RetrofitServices>> call, Response<List<RetrofitServices>> response) {
                if (response.code() == 200){
                    RetrofitServices.borrar_instancia();
                    RetrofitServices.crear_instancia(response.body());
                    refreshLayout.setRefreshing(false);
                    showServices();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitServices>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void editService(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Editar_Servicio d = new Fragment_Editar_Servicio();
        d.id = String.valueOf(serviciosBusqueda.get(position).getId());
        d.nombre = serviciosBusqueda.get(position).getServicio();
        d.desc = serviciosBusqueda.get(position).getDescripcion();
        d.costo = String.valueOf(serviciosBusqueda.get(position).getCosto());
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyService) internetGet();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void addService() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Servicios d = new Fragment_Agregar_Servicios();
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyService) internetGet();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void llenar_busqueda() {
        serviciosBusqueda.clear();
        for (int i = 0; i < servicios.size(); i++) {
            serviciosBusqueda.add(servicios.get(i));
        }
    }

    public void buscar(String busqueda) {
        if (servicios.size() > 0) {
            serviciosBusqueda.clear();
            for (int i = 0; i < servicios.size(); i++) {
                if (servicios.get(i).getServicio().toLowerCase().contains(busqueda.toLowerCase())) {
                    serviciosBusqueda.add(servicios.get(i));
                }
            }
            adapter_servicio.notifyDataSetChanged();
        }
    }
}