package com.juliovazquez.hsclinic.Activities.Servicios;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.juliovazquez.hsclinic.Activities.Historias.Activity_Preguntas;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.ServicesServices;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Agregar_Servicios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Servicios extends DialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public boolean anyService = false;
    Button btnAgregar, btnClose;
    EditText txtServicio, txtDescripcionS, txtCosto;
    String clinica = "";
    Tool_Progress_Dialog progress_dialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Fragment_Agregar_Servicios() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Servicios.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Servicios newInstance(String param1, String param2) {
        Fragment_Agregar_Servicios fragment = new Fragment_Agregar_Servicios();
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
        return inflater.inflate(R.layout.fragment_agregar_servicios, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        getDialog().getWindow().setLayout((int) (width*.85), getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnAgregar = getView().findViewById(R.id.btnAgregar);
        btnClose = getView().findViewById(R.id.btnClose);
        txtCosto = getView().findViewById(R.id.txtCostoS);
        txtDescripcionS = getView().findViewById(R.id.txtDescripcionS);
        txtServicio = getView().findViewById(R.id.txtServicio);
        btnClose.setOnClickListener(this::onClick);
        btnAgregar.setOnClickListener(this::onClick);
        clinica = Tools_Users_Defaults.user_clinic(getActivity());
        progress_dialog = new Tool_Progress_Dialog(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAgregar:
                btnAgregar.setEnabled(false);
                if (check_fields()) {
                    internetAdd();
                } else {
                    btnAgregar.setEnabled(true);
                    progress_dialog.dismissDialog();
                }
                break;
            case R.id.btnClose:
                dismiss();
                break;
        }
    }

    public void internetAdd() {
        if (Tool_Internet.isOnlineNet()) {
            progress_dialog.dismissDialog();
            addService();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetAdd();
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

    public void addService() {
        progress_dialog.ShowProgressDialog();
        ServicesServices service = RetrofitClientInstance.getRetrofitInstance().create(ServicesServices.class);
        Call<RetrofitMessage> call = service.addService(clinica,
                txtServicio.getText().toString(),
                String.valueOf(Tools_Users_Defaults.user_id(getContext())),
                txtDescripcionS.getText().toString(),
                txtCosto.getText().toString());
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    Toasty.success(getContext(), "Se ha creado un servicio", Toasty.LENGTH_LONG).show();
                    anyService =  true;
                    dismiss();
                } else if (response.code() == 404){
                    Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                } else {
                    Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<RetrofitMessage> call, Throwable t){
                progress_dialog.dismissDialog();
                Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public boolean check_fields () {
        progress_dialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtServicio.getText())) {
            txtServicio.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtDescripcionS.getText())) {
            txtDescripcionS.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtCosto.getText())) {
            txtCosto.setError(getString(R.string.campo_requerido));
            return false;
        }
        return true;
    }
}