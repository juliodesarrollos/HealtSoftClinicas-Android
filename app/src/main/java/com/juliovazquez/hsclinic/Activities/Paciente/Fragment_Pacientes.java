package com.juliovazquez.hsclinic.Activities.Paciente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.tibolte.agendacalendarview.widgets.FloatingActionButton;
import com.juliovazquez.hsclinic.Activities.Usuario.Activity_Splash_Screen;
import com.juliovazquez.hsclinic.Adapters.Adapter_Paciente;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.PacientsServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
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
 * Use the {@link Fragment_Pacientes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Pacientes extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView rvPAcientes;
    Adapter_Paciente adapterPaciente;
    List<RetrofitPacient> pacientes = new ArrayList<>();
    List<RetrofitPacient> pacientesBusqueda = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    TextView txtSinPacientes;
    String clinica = "", idUser = "";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mRootView;

    public Fragment_Pacientes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Pacientes.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Pacientes newInstance(String param1, String param2) {
        Fragment_Pacientes fragment = new Fragment_Pacientes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        idUser = String.valueOf(Tools_Users_Defaults.user_id(getContext()));
        txtSinPacientes = getView().findViewById(R.id.txtSinPacientes);
        rvPAcientes = getView().findViewById(R.id.rvPacientes);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvPAcientes.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshPacientes);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setAlpha(0.80f);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        fab.setOnClickListener(this::onClick);
        showPacients();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Tools_Users_Defaults.getPacientFlag(getContext())) {
            internetGet();
            Tools_Users_Defaults.setPacientFlag(getContext(), false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: agregar_paciente(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    public void showPacients() {
        if (RetrofitPacient.recuperar_instancia() != null   ) {
            pacientes.clear();
            pacientes = RetrofitPacient.recuperar_instancia();
            llenar_busqueda();
            adapterPaciente = new Adapter_Paciente(getContext(), pacientesBusqueda);
            rvPAcientes.setAdapter(adapterPaciente);
            rvPAcientes.setHasFixedSize(true);
            txtSinPacientes.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinPacientes.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void pacientsService() {
        PacientsServices service = RetrofitClientInstance.getRetrofitInstance().create(PacientsServices.class);
        Call<List<RetrofitPacient>> call;
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Individual")) {
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
                    refreshLayout.setRefreshing(false);
                    showPacients();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitPacient>> call, Throwable t) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            pacientsService();
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

    public void agregar_paciente() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Paciente d = new Fragment_Agregar_Paciente();
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyPaciente) internetGet();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void llenar_busqueda() {
        pacientesBusqueda.clear();
        for (int i = 0; i < pacientes.size(); i++) {
            pacientesBusqueda.add(pacientes.get(i));
        }
    }

    public void buscar(String busqueda) {
        if (pacientes.size() > 0) {
            pacientesBusqueda.clear();
            for (int i = 0; i < pacientes.size(); i++) {
                if (pacientes.get(i).getNombre().toLowerCase().contains(busqueda.toLowerCase())) {
                    pacientesBusqueda.add(pacientes.get(i));
                }
            }
            adapterPaciente.notifyDataSetChanged();
        }
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
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_pacientes, container, false);
        }
        return mRootView;
    }
}