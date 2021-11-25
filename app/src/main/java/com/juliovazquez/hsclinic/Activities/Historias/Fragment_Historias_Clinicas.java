package com.juliovazquez.hsclinic.Activities.Historias;

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
import com.juliovazquez.hsclinic.Adapters.Adapter_Historia;
import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Historias_Clinicas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Historias_Clinicas extends Fragment implements Adapter_Historia.OnHistoriaListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Adapter_Historia adapter_historia;
    RecyclerView rvHistorias;
    List<RetrofitHistorias> historias = new ArrayList<>();
    List<RetrofitHistorias> historiasBusqueda = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    TextView txtSinHistorias;
    String clinica = "", idUser = "", rol = "Admin";
    Tool_Progress_Dialog progress_dialog;

    public Fragment_Historias_Clinicas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Historias_Clinicas.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Historias_Clinicas newInstance(String param1, String param2) {
        Fragment_Historias_Clinicas fragment = new Fragment_Historias_Clinicas();
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
        return inflater.inflate(R.layout.fragment_historias_clinicas, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Individual")) rol = "Otro";
        idUser = String.valueOf(Tools_Users_Defaults.user_id(getContext()));
        txtSinHistorias = getView().findViewById(R.id.txtSinHistorias);
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        rvHistorias = getView().findViewById(R.id.rvHistorias);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvHistorias.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshHistorias);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setAlpha(0.80f);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        fab.setOnClickListener(this::onClick);
        showHistorias();
    }

    @Override
    public void onHistoriaClick(int position, int id) {
        switch (id) {
            case R.id.btnVer:
                ver_preguntas(position);
                break;
            case R.id.btnHistoria:
                edit_historia(historiasBusqueda.get(position).getNombre(), String.valueOf(historiasBusqueda.get(position).getId()));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: add_historia(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }

    public void showHistorias() {
        if (RetrofitHistorias.recuperar_instancia() != null   ) {
            historias.clear();
            historias = RetrofitHistorias.recuperar_instancia();
            llenar_busqueda();
            adapter_historia = new Adapter_Historia(historiasBusqueda, this);
            rvHistorias.setAdapter(adapter_historia);
            rvHistorias.setHasFixedSize(true);
            txtSinHistorias.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinHistorias.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void historiasService() {
        progress_dialog.ShowProgressDialog();
        HistoriasService service = RetrofitClientInstance.getRetrofitInstance().create(HistoriasService.class);
        Call<List<RetrofitHistorias>> call = service.getHistorias(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitHistorias>>() {
            @Override
            public void onResponse(Call<List<RetrofitHistorias>> call, Response<List<RetrofitHistorias>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitHistorias.borrar_instancia();
                    RetrofitHistorias.crear_instancia(response.body());
                    refreshLayout.setRefreshing(false);
                    showHistorias();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitHistorias>> call, Throwable t) {
                progress_dialog.dismissDialog();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            historiasService();
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

    public void ver_preguntas (int position) {
        Intent i =  new Intent(getContext(), Activity_Preguntas.class);
        i.putExtra("id", historiasBusqueda.get(position).getId());
        i.putExtra("HC", historiasBusqueda.get(position).getNombre());
        startActivity(i);
    }

    public void edit_historia(String historia, String id) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Editar_Historias d = new Fragment_Editar_Historias();
        d.historia = historia;
        d.id = id;
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyHistoria) historiasService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void add_historia() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Historias d = new Fragment_Agregar_Historias();
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyHistoria) historiasService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void llenar_busqueda() {
        historiasBusqueda.clear();
        for (int i = 0; i < historias.size(); i++) {
            historiasBusqueda.add(historias.get(i));
        }
    }

    public void buscar(String busqueda) {
        if (historias.size() > 0) {
            historiasBusqueda.clear();
            for (int i = 0; i < historias.size(); i++) {
                if (historias.get(i).getNombre().toLowerCase().contains(busqueda.toLowerCase())) {
                    historiasBusqueda.add(historias.get(i));
                }
            }
            adapter_historia.notifyDataSetChanged();
        }
    }
}