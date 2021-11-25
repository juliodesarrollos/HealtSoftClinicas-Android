package com.juliovazquez.hsclinic.Activities.Graficas;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.juliovazquez.hsclinic.Pojos.RetrofitCitasCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitIngresosCharts;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacientesCharts;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.GraficasServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
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
 * Use the {@link Fragment_Graficas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Graficas extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    BarChart chartPacientes;
    BarChart chartCitas;
    BarChart chartIngresos;
    Button btnPacientes;
    Button btnCitas;
    Button btnIngresos;
    String clinica = "", rol = "Admin", idUser = "";
    Tool_Progress_Dialog progress_dialog;

    public Fragment_Graficas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Graficas.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Graficas newInstance(String param1, String param2) {
        Fragment_Graficas fragment = new Fragment_Graficas();
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
        return inflater.inflate(R.layout.fragment_graficas, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Individual")) rol = "Otro";
        idUser = String.valueOf(Tools_Users_Defaults.user_id(getContext()));
        chartPacientes = getView().findViewById(R.id.chartPacientes);
        chartCitas = getView().findViewById(R.id.chartCitas);
        chartIngresos = getView().findViewById(R.id.chartIngresos);
        btnPacientes = getView().findViewById(R.id.btnPacientes);
        btnCitas = getView().findViewById(R.id.btnCitas);
        btnIngresos = getView().findViewById(R.id.btnIngresos);
        btnPacientes.setOnClickListener(this);
        btnCitas.setOnClickListener(this);
        btnIngresos.setOnClickListener(this);
        showCitas();
        showPacientes();
        showIngresos();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPacientes: setButtons(view); setCharts(chartPacientes);break;
            case R.id.btnCitas: setButtons(view);  setCharts(chartCitas);break;
            case R.id.btnIngresos: setButtons(view);  setCharts(chartIngresos);break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Tools_Users_Defaults.getEventChartFlag(getContext())) {
            Tools_Users_Defaults.setEventChartFlag(getContext(), false);
            internetCitas();
            internetIngresos();
        }
        if (Tools_Users_Defaults.getPacientChartFlag(getContext())) {
            Tools_Users_Defaults.setPacientChartFlag(getContext(), false);
            internetPacientes();
        }
    }

    public void setCharts(BarChart barChart) {
        chartPacientes.setVisibility(View.GONE);
        chartCitas.setVisibility(View.GONE);
        chartIngresos.setVisibility(View.GONE);
        barChart.setVisibility(View.VISIBLE);
    }

    public void setButtons (View  view) {
        Button current = (Button) view;
        btnPacientes.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        btnPacientes.setTextColor(getActivity().getResources().getColor(R.color.black));
        btnCitas.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        btnCitas.setTextColor(getActivity().getResources().getColor(R.color.black));
        btnIngresos.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        btnIngresos.setTextColor(getActivity().getResources().getColor(R.color.black));
        current.setBackgroundColor(getActivity().getResources().getColor(R.color.grayBackground));
        current.setTextColor(getActivity().getResources().getColor(R.color.primaryBlueColor));
    }
    public void showCitas() {
        ArrayList citas1 = new ArrayList();
        ArrayList citas2 = new ArrayList();
        ArrayList citas3 = new ArrayList();
        ArrayList meses = new ArrayList();

        if (RetrofitCitasCharts.recuperar_instancia() != null) {
            for (int i = 0; i<RetrofitCitasCharts.recuperar_instancia().size(); i++) {
                citas1.add(new BarEntry(RetrofitCitasCharts.recuperar_instancia().get(i).getSuma(), i));
                citas2.add(new BarEntry(RetrofitCitasCharts.recuperar_instancia().get(i).getSuma2(), i));
                citas3.add(new BarEntry(RetrofitCitasCharts.recuperar_instancia().get(i).getSuma3(), i));
                meses.add(RetrofitCitasCharts.recuperar_instancia().get(i).getMes());
            }
        }

        List<IBarDataSet> dataSets =  new ArrayList<>();
        BarDataSet bardataset = new BarDataSet(citas1, "Citas registradas");
        BarDataSet bardataset2 = new BarDataSet(citas2, "Citas concluidas");
        BarDataSet bardataset3 = new BarDataSet(citas3, "Citas canceladas");
        bardataset.setColor(getActivity().getResources().getColor(R.color.primaryBlueColor));
        bardataset2.setColor(getActivity().getResources().getColor(R.color.green));
        bardataset3.setColor(getActivity().getResources().getColor(R.color.red));
        dataSets.add(bardataset);
        dataSets.add(bardataset2);
        dataSets.add(bardataset3);
        chartCitas.animateY(5000);
        BarData data = new BarData(meses, dataSets);
        chartCitas.setData(null);
        chartCitas.setData(data);
    }

    public void showPacientes() {
        ArrayList pacientes = new ArrayList();
        ArrayList meses = new ArrayList();

        if (RetrofitPacientesCharts.recuperar_instancia() != null) {
            for (int i = 0; i<RetrofitPacientesCharts.recuperar_instancia().size(); i++) {
                pacientes.add(new BarEntry(RetrofitPacientesCharts.recuperar_instancia().get(i).getSuma(), i));
                meses.add(RetrofitPacientesCharts.recuperar_instancia().get(i).getMes());
            }
        }

        BarDataSet bardataset = new BarDataSet(pacientes, "Pacientes registrados por mes");
        bardataset.setColor(getActivity().getResources().getColor(R.color.primaryBlueColor));
        chartPacientes.animateY(5000);
        BarData data = new BarData(meses, bardataset);
        chartPacientes.setData(data);
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
        chartIngresos.animateY(5000);
        BarData data = new BarData(meses, bardataset);
        chartIngresos.setData(data);
    }

    public void getChartsPacientes() {
        progress_dialog.ShowProgressDialog();
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitPacientesCharts>> call = service.getChartsPacients(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitPacientesCharts>>() {
            @Override
            public void onResponse(Call<List<RetrofitPacientesCharts>> call, Response<List<RetrofitPacientesCharts>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitPacientesCharts.borrar_instancia();
                    RetrofitPacientesCharts.crear_instancia(response.body());
                    showPacientes();
                } else {
                    RetrofitPacientesCharts.borrar_instancia();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitPacientesCharts>> call, Throwable t) {
                Toasty.error(getContext(), "No se pudo recuperar los datos", Toast.LENGTH_SHORT, true).show();
                progress_dialog.dismissDialog();
            }
        });
    }

    public void getChartsCitas() {
        progress_dialog.ShowProgressDialog();
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitCitasCharts>> call = service.getChartsCitas(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitCitasCharts>>() {
            @Override
            public void onResponse(Call<List<RetrofitCitasCharts>> call, Response<List<RetrofitCitasCharts>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitCitasCharts.borrar_instancia();
                    RetrofitCitasCharts.crear_instancia(response.body());
                    showCitas();
                } else {
                    RetrofitCitasCharts.borrar_instancia();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitCitasCharts>> call, Throwable t) {
                progress_dialog.dismissDialog();
                Toasty.error(getContext(), "No se pudo recuperar los datos", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void getChartsIngresos() {
        progress_dialog.ShowProgressDialog();
        GraficasServices service = RetrofitClientInstance.getRetrofitInstance().create(GraficasServices.class);
        Call<List<RetrofitIngresosCharts>> call = service.getChartsIngresos(clinica, idUser, rol);
        call.enqueue(new Callback<List<RetrofitIngresosCharts>>() {
            @Override
            public void onResponse(Call<List<RetrofitIngresosCharts>> call, Response<List<RetrofitIngresosCharts>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitIngresosCharts.borrar_instancia();
                    RetrofitIngresosCharts.crear_instancia(response.body());
                    showIngresos();
                } else {
                    RetrofitIngresosCharts.borrar_instancia();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitIngresosCharts>> call, Throwable t) {
                progress_dialog.dismissDialog();
                Toasty.error(getContext(), "No se pudo recuperar los datos", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void internetIngresos() {
        if (Tool_Internet.isOnlineNet()) {
            getChartsIngresos();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetIngresos();
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

    public void internetPacientes() {
        if (Tool_Internet.isOnlineNet()) {
            getChartsPacientes();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetPacientes();
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

    public void internetCitas() {
        if (Tool_Internet.isOnlineNet()) {
            getChartsCitas();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetCitas();
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