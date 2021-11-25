package com.juliovazquez.hsclinic.Activities.Especialista;

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
import com.juliovazquez.hsclinic.Adapters.Adapter_User;
import com.juliovazquez.hsclinic.Pojos.RetrofitUser;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.UsersServices;
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
 * Use the {@link Fragment_Especialistas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Especialistas extends Fragment implements Adapter_User.OnUserListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Adapter_User adapter_user;
    RecyclerView rvUsers;
    List<RetrofitUser> users = new ArrayList<>();
    List<RetrofitUser> usersBusqueda = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;
    Tool_Progress_Dialog progress_dialog;
    TextView txtSinUsuarios;
    String clinica = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Especialistas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_especialistas.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Especialistas newInstance(String param1, String param2) {
        Fragment_Especialistas fragment = new Fragment_Especialistas();
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
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        txtSinUsuarios = getView().findViewById(R.id.txtSinUsuarios);
        rvUsers = getView().findViewById(R.id.rvUsuarios);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        rvUsers.setLayoutManager(gridLayoutManager);
        refreshLayout = getView().findViewById(R.id.refreshUsuarios);
        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setAlpha(0.80f);
        refreshLayout.setOnRefreshListener(this::onRefresh);
        fab.setOnClickListener(this::onClick);
        showUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_especialistas, container, false);
    }

    @Override
    public void onUserClick(int position) {
        edit_especialista(position);
    }

    @Override
    public void onLongUserClick(int position) {
       edit_permisos(position);
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            usersService();
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

    public void usersService() {
        progress_dialog.ShowProgressDialog();
        UsersServices service = RetrofitClientInstance.getRetrofitInstance().create(UsersServices.class);
        Call<List<RetrofitUser>> call = service.getUsers(clinica);
        call.enqueue(new Callback<List<RetrofitUser>>() {
            @Override
            public void onResponse(Call<List<RetrofitUser>> call, Response<List<RetrofitUser>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitUser.borrar_instancia();
                    RetrofitUser.crear_instancia(response.body());
                    refreshLayout.setRefreshing(false);
                    showUsers();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitUser>> call, Throwable t) {
                progress_dialog.dismissDialog();
                showUsers();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void showUsers() {
        if (RetrofitUser.recuperar_instancia() != null   ) {
            users.clear();
            users = RetrofitUser.recuperar_instancia();
            llenar_busqueda();
            adapter_user = new Adapter_User(usersBusqueda, this);
            rvUsers.setAdapter(adapter_user);
            rvUsers.setHasFixedSize(true);
            txtSinUsuarios.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        } else {
            txtSinUsuarios.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        }
    }

    public void edit_especialista(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Editar_Especialista d = new Fragment_Editar_Especialista();
        d.idEspe = String.valueOf(usersBusqueda.get(position).getId());
        d.nombre = usersBusqueda.get(position).getName();
        d.rol = usersBusqueda.get(position).getRol();
        d.correo = usersBusqueda.get(position).getEmail();
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyUser) usersService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void edit_permisos(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Editar_Permisos d = new Fragment_Editar_Permisos();
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                //if (d.anyUser) userService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void add_especialista() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment_Agregar_Especialista d = new Fragment_Agregar_Especialista();
        d.show(fragmentManager, "tag");
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (d.anyUser) usersService();
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }

    public void llenar_busqueda() {
        usersBusqueda.clear();
        for (int i = 0; i < users.size(); i++) {
            usersBusqueda.add(users.get(i));
        }
    }

    public void buscar(String busqueda) {
        if (users.size() > 0) {
            usersBusqueda.clear();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getName().toLowerCase().contains(busqueda.toLowerCase()) || users.get(i).getRol().toLowerCase().contains(busqueda.toLowerCase())) {
                    usersBusqueda.add(users.get(i));
                }
            }
            adapter_user.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: add_especialista(); break;
        }
    }

    @Override
    public void onRefresh() {
        internetGet();
    }
}