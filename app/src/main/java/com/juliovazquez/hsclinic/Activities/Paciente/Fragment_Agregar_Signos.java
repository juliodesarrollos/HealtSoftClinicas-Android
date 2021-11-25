package com.juliovazquez.hsclinic.Activities.Paciente;

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

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.SignosServices;
import com.juliovazquez.hsclinic.Services.UsersServices;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
import com.juliovazquez.hsclinic.Tools.Tool_String;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Agregar_Signos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Signos extends DialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public boolean anySignos = false;
    Button btnClose, btnAgregar;
    EditText txtFR, txtTemp, txtPS, txtPulso, txtO2;
    String idPaciente, clinica;
    Tool_Progress_Dialog progress_dialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Agregar_Signos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Signos.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Signos newInstance(String param1, String param2) {
        Fragment_Agregar_Signos fragment = new Fragment_Agregar_Signos();
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
        return inflater.inflate(R.layout.fragment__agregar__signos, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        getDialog().getWindow().setLayout((int) (width*.85),getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        btnAgregar = getView().findViewById(R.id.btnAgregar);
        btnClose = getView().findViewById(R.id.btnClose);
        txtFR = getView().findViewById(R.id.txtFR);
        txtO2 = getView().findViewById(R.id.txtO2);
        txtPS = getView().findViewById(R.id.txtPS);
        txtPulso = getView().findViewById(R.id.txtPulso);
        txtTemp = getView().findViewById(R.id.txtTemp);
        btnClose.setOnClickListener(this);
        btnAgregar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose: dismiss(); break;
            case R.id.btnAgregar:
                btnAgregar.setEnabled(false);
                if (revisar_campos()) {
                    internetAdd();
                } else {
                    btnAgregar.setEnabled(true);
                    progress_dialog.dismissDialog();
                } break;
        }
    }

    public boolean revisar_campos () {
        progress_dialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtFR.getText())) {
            txtFR.setError(getResources().getString(R.string.campo_requerido));
            txtFR.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(txtTemp.getText())) {
            txtTemp.requestFocus();
            txtTemp.setError(getResources().getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtPS.getText())) {
            txtPS.requestFocus();
            txtPS.setError(getResources().getString(R.string.campo_requerido));
            return false;
        }else if (TextUtils.isEmpty(txtPulso.getText())) {
            txtPulso.requestFocus();
            txtPulso.setError(getResources().getString(R.string.campo_requerido));
            return false;
        }else if (TextUtils.isEmpty(txtO2.getText())) {
            txtO2.requestFocus();
            txtO2.setError(getResources().getString(R.string.campo_requerido));
            return false;
        } else if (!Tool_String.ps_pattern(txtPS.getText().toString())) {
            txtPS.requestFocus();
            txtPS.setError("Presión sanguínea incorrecta");
            Toasty.error(getContext(), "La presión sanguinea no esta en el formato correcto (120/80)", Toasty.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void internetAdd() {
        if (Tool_Internet.isOnlineNet()) {
            progress_dialog.dismissDialog();
            addSignos();
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

    public void addSignos() {
        progress_dialog.ShowProgressDialog();
        String[] parts = txtPS.getText().toString().split("/");
        SignosServices service = RetrofitClientInstance.getRetrofitInstance().create(SignosServices.class);
        Call<RetrofitMessage> call = service.addSignos(clinica,
                txtTemp.getText().toString(),
                txtPulso.getText().toString(),
                txtO2.getText().toString(),
                idPaciente,
                parts[0],
                parts[1],
                txtFR.getText().toString());
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    Toasty.success(getContext(), "Signos vitales guardados", Toasty.LENGTH_LONG).show();
                    anySignos =  true;
                    dismiss();
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
}