package com.juliovazquez.hsclinic.Activities.Especialista;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.UsersServices;
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
 * Use the {@link Fragment_Agregar_Especialista#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Especialista extends DialogFragment implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener, AdapterView.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public boolean anyUser = false;
    EditText txtNombre, txtRol, txtCorreo, txtPass, txtConfirmPass;
    Button btnAgregar, btnClose;
    ListView lvRoles;
    List <String> roles = new ArrayList<>();
    List <String> buscarRoles = new ArrayList<>();
    ArrayAdapter<String> adapterRoles;
    Tool_Progress_Dialog progress_dialog;
    String clinica = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Agregar_Especialista() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Especialista.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Especialista newInstance(String param1, String param2) {
        Fragment_Agregar_Especialista fragment = new Fragment_Agregar_Especialista();
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
        return inflater.inflate(R.layout.fragment_agregar_especialista, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        getDialog().getWindow().setLayout((int) (width*.85), getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        roles.add("Admin");
        roles.add("Especialista");
        roles.add("Practicante");
        roles.add("Secretaria");
        txtRol = getView().findViewById(R.id.txtRol);
        txtConfirmPass = getView().findViewById(R.id.txtConfirmPass);
        txtCorreo = getView().findViewById(R.id.txtCorreo);
        txtPass = getView().findViewById(R.id.txtPass);
        txtNombre = getView().findViewById(R.id.txtNombre);
        btnAgregar = getView().findViewById(R.id.btnAgregar);
        lvRoles = getView().findViewById(R.id.lvRoles);
        btnClose = getView().findViewById(R.id.btnClose);
        txtRol.addTextChangedListener(this);
        txtRol.setOnFocusChangeListener(this::onFocusChange);
        lvRoles.setOnItemClickListener(this::onItemClick);
        btnAgregar.setOnClickListener(this);
        btnClose.setOnClickListener(this::onClick);
        clinica = Tools_Users_Defaults.user_clinic(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                dismiss();
                break;
            case R.id.btnAgregar:
                btnAgregar.setEnabled(false);
                if (check_fields()) {
                    internetAdd();
                } else {
                    progress_dialog.dismissDialog();
                    btnAgregar.setEnabled(true);
                }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (txtRol.isFocused()) {
            lvRoles.setVisibility(View.VISIBLE);
            if (roles.size() > 0 && txtRol.getText() != null) {
                buscarRoles.clear();
                for (int contador = 0; contador < roles.size(); contador++) {
                    if (roles.get(contador).toLowerCase().contains(txtRol.getText().toString().toLowerCase())) {
                        buscarRoles.add(roles.get(contador));
                    }
                }
                llenar_roles();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (txtRol.isFocused()) {
            lvRoles.setVisibility(View.VISIBLE);
            llenar_busquedas();
            txtRol.setText(null);
        }else {
            lvRoles.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (txtRol.isFocused()) {
            txtRol.setText(buscarRoles.get(i));
            txtRol.clearFocus();
            lvRoles.setVisibility(View.GONE);
        }
    }

    public void addUser() {
        progress_dialog.ShowProgressDialog();
        UsersServices service = RetrofitClientInstance.getRetrofitInstance().create(UsersServices.class);
        Call<RetrofitMessage> call = service.addUser(clinica,
                txtNombre.getText().toString(),
                txtRol.getText().toString(),
                txtCorreo.getText().toString(),
                txtPass.getText().toString());
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    Toasty.success(getContext(), "Se ha creado un usuario", Toasty.LENGTH_LONG).show();
                    anyUser =  true;
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

    public void llenar_roles() {
        adapterRoles = null;
        adapterRoles  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, buscarRoles);
        lvRoles.setAdapter(null);
        lvRoles.setAdapter(adapterRoles);
    }

    public void llenar_busquedas() {
        buscarRoles.add("Admin");
        buscarRoles.add("Especialista");
        buscarRoles.add("Practicante");
        buscarRoles.add("Secretaria");
    }

    public boolean check_fields () {
        progress_dialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtNombre.getText())) {
            txtNombre.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtRol.getText())) {
            txtRol.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtCorreo.getText())) {
            txtCorreo.setError(getString(R.string.campo_requerido));
            return false;
        }else if (TextUtils.isEmpty(txtPass.getText())) {
            txtPass.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtConfirmPass.getText())) {
            txtConfirmPass.setError(getString(R.string.campo_requerido));
            return false;
        } else if (!txtPass.getText().toString().equals(txtConfirmPass.getText().toString())) {
            Toasty.error(getContext(), "Las contraseñas no coincides", Toasty.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void internetAdd() {
        if (Tool_Internet.isOnlineNet()) {
            progress_dialog.dismissDialog();
            addUser();
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
}