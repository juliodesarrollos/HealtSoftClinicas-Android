package com.juliovazquez.hsclinic.Activities.Historias;

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
import com.juliovazquez.hsclinic.Services.HistoriasService;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
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
 * Use the {@link Fragment_Agregar_Historias#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Historias extends DialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public boolean anyHistoria;
    Button btnAgregar, btnClose;
    EditText txtHistoria;
    String clinica = "";
    int id = 0;
    Tool_Progress_Dialog progress_dialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Agregar_Historias() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Historias.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Historias newInstance(String param1, String param2) {
        Fragment_Agregar_Historias fragment = new Fragment_Agregar_Historias();
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
        return inflater.inflate(R.layout.fragment_agregar_historias, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        getDialog().getWindow().setLayout((int) (width*.85), getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnAgregar = getView().findViewById(R.id.btnAgregar);
        btnClose = getView().findViewById(R.id.btnClose);
        txtHistoria = getView().findViewById(R.id.txtHistoria);
        btnAgregar.setOnClickListener(this::onClick);
        btnClose.setOnClickListener(this::onClick);
        clinica = Tools_Users_Defaults.user_clinic(getActivity());
        id = Tools_Users_Defaults.user_id(getContext());
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
            addHistoria();
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

    public void addHistoria() {
        progress_dialog.ShowProgressDialog();
        HistoriasService service = RetrofitClientInstance.getRetrofitInstance().create(HistoriasService.class);
        Call<RetrofitMessage> call = service.addHistoria(clinica,
                String.valueOf(id),
                txtHistoria.getText().toString());
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200) {
                    Toasty.success(getContext(), "Se ha creado una historia", Toasty.LENGTH_LONG).show();
                    anyHistoria =  true;
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
        if (TextUtils.isEmpty(txtHistoria.getText())) {
            txtHistoria.setError(getString(R.string.campo_requerido));
            return false;
        }
        return true;
    }
}