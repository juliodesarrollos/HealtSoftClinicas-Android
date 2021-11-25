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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.juliovazquez.hsclinic.Pojos.RetrofitHistorias;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitUser;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.ArchivoServices;
import com.juliovazquez.hsclinic.Services.EventsServices;
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
 * Use the {@link Fragment_Agregar_Archivo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Archivo extends DialogFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public boolean anyArchivo = false;
    String idHc = "", idPaciente = "", clinica = "", idUser = "";
    ListView lvHistorias;
    Button btnAgrgar, btnClose;
    TextView txtHistorias;
    ArrayAdapter<String> adapterHistorias;
    List<RetrofitHistorias> historias = new ArrayList<>();
    Tool_Progress_Dialog progress_dialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Agregar_Archivo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Archivo.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Archivo newInstance(String param1, String param2) {
        Fragment_Agregar_Archivo fragment = new Fragment_Agregar_Archivo();
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
        return inflater.inflate(R.layout.fragment_agregar_archivo, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        getDialog().getWindow().setLayout((int) (width*.85), getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        lvHistorias = getView().findViewById(R.id.lvHistorias);
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        btnAgrgar = getView().findViewById(R.id.btnAgregar);
        btnClose = getView().findViewById(R.id.btnClose);
        txtHistorias = getView().findViewById(R.id.txtHistoria);
        btnClose.setOnClickListener(this::onClick);
        btnAgrgar.setOnClickListener(this::onClick);
        lvHistorias.setOnItemClickListener(this::onItemClick);
        historias = RetrofitHistorias.recuperar_instancia();
        idUser = String.valueOf(Tools_Users_Defaults.user_id(getContext()));
        llenar_historias();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        txtHistorias.setText(historias.get(i).getNombre());
        idHc = historias.get(i).getId().toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose: dismiss(); break;
            case R.id.btnAgregar:
                btnAgrgar.setEnabled(false);
               if (check_fields()) {
                   internetAdd();
               } else {
                   btnAgrgar.setEnabled(true);
                   progress_dialog.dismissDialog();
               }
                break;
        }
    }

    public boolean check_fields () {
        progress_dialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtHistorias.getText())) {
            Toasty.error(getContext(), "Seleccione una historia clinica de la lista", Toasty.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void llenar_historias () {
        String[] nombres = new String[historias.size()];
        for (int i = 0; i < historias.size(); i++) {
            nombres[i] = historias.get(i).getNombre();
        }
        adapterHistorias = null;
        adapterHistorias  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombres);
        lvHistorias.setAdapter(null);
        lvHistorias.setAdapter(adapterHistorias);
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
        ArchivoServices service = RetrofitClientInstance.getRetrofitInstance().create(ArchivoServices.class);
        Call<RetrofitMessage> call = service.addHCPX(clinica, idUser, idPaciente, idHc);
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    Toasty.success(getContext(), "Se agrego una historia clinica", Toast.LENGTH_LONG, true).show();
                    anyArchivo =  true;
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