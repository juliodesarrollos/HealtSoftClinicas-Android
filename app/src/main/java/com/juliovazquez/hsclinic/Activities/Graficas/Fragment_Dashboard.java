package com.juliovazquez.hsclinic.Activities.Graficas;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.juliovazquez.hsclinic.Pojos.RetrofitDashboard;
import com.juliovazquez.hsclinic.Pojos.RetrofitEvents;
import com.juliovazquez.hsclinic.Pojos.RetrofitIngresosCharts;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.GraficasServices;
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
 * Use the {@link Fragment_Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Dashboard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView txtCitas1;
    TextView txtCitas2;
    TextView txtPacientes;
    TextView txtIngresos;
    BarChart chartDashboard;
    String clinica = "", idUser = "", rol = "Admin";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Dashboard newInstance(String param1, String param2) {
        Fragment_Dashboard fragment = new Fragment_Dashboard();
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
        txtCitas1 = getView().findViewById(R.id.txtCitas1);
        txtCitas2 = getView().findViewById(R.id.txtCitas2);
        txtPacientes = getView().findViewById(R.id.txtPacientesD);
        txtIngresos = getView().findViewById(R.id.txtIngresos);
        chartDashboard = getView().findViewById(R.id.chartDashboard);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        idUser = String.valueOf(Tools_Users_Defaults.user_id(getContext()));
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Individual")) rol = "Otro";
        showDashboard();
        showIngresos();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Tools_Users_Defaults.getEventChartFlag(getContext())) {
            Tools_Users_Defaults.setEventChartFlag(getContext(), false);
            internetGet();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    public void showDashboard() {
        if (RetrofitDashboard.recuperar_instancia() != null) {
            txtCitas1.setText(RetrofitDashboard.recuperar_instancia().get(0).getCitas().toString());
            txtCitas2.setText(RetrofitDashboard.recuperar_instancia().get(0).getCitas2().toString());
            txtPacientes.setText(RetrofitDashboard.recuperar_instancia().get(0).getPacientes().toString());
            txtIngresos.setText("$" + RetrofitDashboard.recuperar_instancia().get(0).getIngresos() + ".00");
        }
    }

    public void showIngresos() {
        ArrayList ingresos = new ArrayList();
        ArrayList meses = new ArrayList();

        if (RetrofitIngresosCharts.recuperar_instancia() != null) {
            for (int i = 0; i<RetrofitIngresosCharts.recuperar_instancia().size(); i++) {
                ingresos.add(new BarEntry(RetrofitIngresosCharts.recuperar_instancia().get(i).getTotal(), i));
                meses.add(RetrofitIngresosCharts.recuperar_instancia().get(i).getMes());
            }
        }

        BarDataSet bardataset = new BarDataSet(ingresos, "Ingresos por mes");
        bardataset.setColor(getActivity().getResources().getColor(R.color.primaryBlueColor));
        chartDashboard.animateY(5000);
        BarData data = new BarData(meses, bardataset);
        chartDashboard.setData(data);
    }

    public void getDashboard() {
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitDashboard>> call = service.getDashboard(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitDashboard>>() {
            @Override
            public void onResponse(Call<List<RetrofitDashboard>> call, Response<List<RetrofitDashboard>> response) {
                if (response.code() == 200){
                    RetrofitDashboard.borrar_instancia();
                    RetrofitDashboard.crear_instancia(response.body());
                    showDashboard();
                    showIngresos();
                } else {
                    RetrofitEvents.borrar_instancia();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitDashboard>> call, Throwable t) {
                Toasty.error(getContext(), "Ups! Algo salió mal", Toasty.LENGTH_SHORT).show();
            }
        });
    }

    public void internetGet() {
        if (Tool_Internet.isOnlineNet()) {
            getDashboard();
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