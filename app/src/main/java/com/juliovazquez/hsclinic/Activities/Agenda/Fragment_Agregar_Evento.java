package com.juliovazquez.hsclinic.Activities.Agenda;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.Pojos.RetrofitUser;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.EventsServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Tools.Tool_Fecha;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Agregar_Evento#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Evento extends DialogFragment implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener, AdapterView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public boolean anyEvent = false;
    Calendar date;
    EditText txtDateTime, txtColor, txtComentario, txtServicio, txtPaciente, txtEspecialista;
    ListView lvPacientes, lvServicios,  lvEspecialistas;
    String color = "",  clinica = "", end = "";
    RadioGroup colorGroup1, colorGroup2, colorGroup3;
    LinearLayout layoutColor;
    Button btnAgregar, btnClose;
    RadioButton c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15;
    int idPaciente = 0, idServicio = 0, idEspecialista = 0;
    List<RetrofitPacient> pacientes = new ArrayList<>();
    List<RetrofitPacient> busquedaPacientes = new ArrayList<>();
    List<RetrofitServices>  servicios = new ArrayList<>();
    List<RetrofitServices>  busquedaServicios = new ArrayList<>();
    List<RetrofitUser>  especialistas = new ArrayList<>();
    List<RetrofitUser>  busquedaEspecialistas = new ArrayList<>();
    Tool_Progress_Dialog progressDialog;
    Date event_date, current;
    ArrayAdapter<String> adapterPacientes, adapterServicios, adapterEspecialistas;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Agregar_Evento() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Evento.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Evento newInstance(String param1, String param2) {
        Fragment_Agregar_Evento fragment = new Fragment_Agregar_Evento();
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
        //setStyle(STYLE_NO_TITLE, R.style.DialogTheme_transparent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        getDialog().getWindow().setLayout((int) (width*.85), getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog = new Tool_Progress_Dialog(getActivity());
        txtDateTime = getView().findViewById(R.id.txtDateTime);
        txtPaciente = getView().findViewById(R.id.txtPaciente);
        txtEspecialista =  getView().findViewById(R.id.txtEspecialista);
        lvPacientes = getView().findViewById(R.id.lvPacientes);
        lvServicios = getView().findViewById(R.id.lvServicios);
        lvEspecialistas  = getView().findViewById(R.id.lvEspecialistas);
        txtColor = getView().findViewById(R.id.txtColor);
        txtComentario = getView().findViewById(R.id.txtComentario);
        txtServicio = getView().findViewById(R.id.txtTitulo);
        colorGroup1 = getView().findViewById(R.id.colorGroup1);
        colorGroup2 = getView().findViewById(R.id.colorGroup2);
        colorGroup3 = getView().findViewById(R.id.colorGroup3);
        layoutColor = getView().findViewById(R.id.layoutColor);
        btnClose = getView().findViewById(R.id.btnClose);
        c1 = getView().findViewById(R.id.color1);
        c2 = getView().findViewById(R.id.color2);
        c3 = getView().findViewById(R.id.color3);
        c4 = getView().findViewById(R.id.color4);
        c5 = getView().findViewById(R.id.color5);
        c6 = getView().findViewById(R.id.color6);
        c7 = getView().findViewById(R.id.color7);
        c8 = getView().findViewById(R.id.color8);
        c9 = getView().findViewById(R.id.color9);
        c10 = getView().findViewById(R.id.color10);
        c11 = getView().findViewById(R.id.color11);
        c12 = getView().findViewById(R.id.color12);
        c13 = getView().findViewById(R.id.color13);
        c14 = getView().findViewById(R.id.color14);
        c15 = getView().findViewById(R.id.color15);
        btnAgregar = getView().findViewById(R.id.btnAgregar);
        clinica = Tools_Users_Defaults.user_clinic(getActivity());

        c1.setOnClickListener(this::onClick);
        c2.setOnClickListener(this::onClick);
        c3.setOnClickListener(this::onClick);
        c4.setOnClickListener(this::onClick);
        c5.setOnClickListener(this::onClick);
        c6.setOnClickListener(this::onClick);
        c7.setOnClickListener(this::onClick);
        c8.setOnClickListener(this::onClick);
        c9.setOnClickListener(this::onClick);
        c10.setOnClickListener(this::onClick);
        c11.setOnClickListener(this::onClick);
        c12.setOnClickListener(this::onClick);
        c13.setOnClickListener(this::onClick);
        c14.setOnClickListener(this::onClick);
        c15.setOnClickListener(this::onClick);
        txtDateTime.setOnClickListener(this::onClick);
        txtColor.setOnClickListener(this::onClick);
        btnAgregar.setOnClickListener(this::onClick);
        btnClose.setOnClickListener(this::onClick);
        txtPaciente.addTextChangedListener(this);
        txtPaciente.setOnFocusChangeListener(this);
        txtServicio.addTextChangedListener(this);
        txtServicio.setOnFocusChangeListener(this);
        txtEspecialista.setOnFocusChangeListener(this);
        txtEspecialista.addTextChangedListener(this);
        lvPacientes.setOnItemClickListener(this);
        lvServicios.setOnItemClickListener(this);
        lvEspecialistas.setOnItemClickListener(this);

        especialistas = RetrofitUser.recuperar_instancia();
        pacientes = RetrofitPacient.recuperar_instancia();
        servicios =RetrofitServices.recuperar_instancia();
        llenar_busquedas();
        configuracion_incial();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agregar_evento, container, false);
    }

    @Override
    public void onClick(View v) {
        boolean checked = false;
        if (v instanceof RadioButton) checked = ((RadioButton) v).isChecked();
        switch (v.getId()) {
            case R.id.txtDateTime:
                showDateTimePicker();
                break;
            case R.id.txtColor:
                layoutColor.setVisibility(View.VISIBLE);
                break;
            case R.id.color1:
                if (checked) color = getString(R.string.color1);
                limpiar_colores();
                break;
            case R.id.color2:
                if (checked) color = getString(R.string.color2);
                limpiar_colores();
                break;
            case R.id.color3:
                if (checked) color = getString(R.string.color3);
                limpiar_colores();
                break;
            case R.id.color4:
                if (checked) color = getString(R.string.color4);
                limpiar_colores();
                break;
            case R.id.color5:
                if (checked) color = getString(R.string.color5);
                limpiar_colores();
                break;
            case R.id.color6:
                if (checked) color = getString(R.string.color6);
                limpiar_colores();
                break;
            case R.id.color7:
                if (checked) color = getString(R.string.color7);
                limpiar_colores();
                break;
            case R.id.color8:
                if (checked) color = getString(R.string.color8);
                limpiar_colores();
                break;
            case R.id.color9:
                if (checked) color = getString(R.string.color9);
                limpiar_colores();
                break;
            case R.id.color10:
                if (checked) color = getString(R.string.color10);
                limpiar_colores();
                break;
            case R.id.color11:
                if (checked) color = getString(R.string.color11);
                limpiar_colores();
                break;
            case R.id.color12:
                if (checked) color = getString(R.string.color12);
                limpiar_colores();
                break;
            case R.id.color13:
                if (checked) color = getString(R.string.color13);
                limpiar_colores();
                break;
            case R.id.color14:
                if (checked) color = getString(R.string.color14);
                limpiar_colores();
                break;
            case R.id.color15:
                if (checked) color = getString(R.string.color15);
                limpiar_colores();
                break;
            case R.id.btnAgregar:
                btnAgregar.setEnabled(false);
                if (verificar_campos()) {
                    internetAdd();
                } else {
                    btnAgregar.setEnabled(true);
                } break;
            case R.id.btnClose:
                dismiss();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (txtPaciente.isFocused()) {
            lvPacientes.setVisibility(View.VISIBLE);
            if (pacientes.size() > 0 && txtPaciente.getText() != null) {
                busquedaPacientes.clear();
                for (int contador = 0; contador < pacientes.size(); contador++) {
                    if (pacientes.get(contador).getNombre().toLowerCase().contains(txtPaciente.getText().toString().toLowerCase())) {
                        busquedaPacientes.add(pacientes.get(contador));
                    }
                }
                llenar_pacientes();
            }
        }  else if (txtServicio.isFocused()) {
            lvServicios.setVisibility(View.VISIBLE);
            if (servicios.size() > 0 && txtServicio.getText() != null) {
                busquedaServicios.clear();
                for (int contador = 0; contador < servicios.size(); contador++) {
                    if (servicios.get(contador).getServicio().toLowerCase().contains(txtServicio.getText().toString().toLowerCase())) {
                        busquedaServicios.add(servicios.get(contador));
                    }
                }
                llenar_servicios();
            }
        } else if (txtEspecialista.isFocused()) {
            lvEspecialistas.setVisibility(View.VISIBLE);
            if (especialistas.size() > 0 && txtEspecialista.getText() != null) {
                busquedaEspecialistas.clear();
                for (int contador = 0; contador < especialistas.size(); contador++) {
                    if (especialistas.get(contador).getName().toLowerCase().contains(txtEspecialista.getText().toString().toLowerCase())) {
                        busquedaEspecialistas.add(especialistas.get(contador));
                    }
                }
                llenar_especialistas();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (txtPaciente.isFocused()) {
            lvPacientes.setVisibility(View.VISIBLE);
            llenar_busquedas();
            txtPaciente.setText(null);
            if (lvServicios.getVisibility() != View.GONE) {
                txtServicio.setText(null);
                lvServicios.setVisibility(View.GONE);
            }
            if (lvEspecialistas.getVisibility() != View.GONE) {
                txtEspecialista.setText(null);
                lvEspecialistas.setVisibility(View.GONE);
            }
        }else  if (txtServicio.isFocused()) {
            lvServicios.setVisibility(View.VISIBLE);
            llenar_busquedas();
            txtServicio.setText(null);
            if (lvPacientes.getVisibility() != View.GONE) {
                txtPaciente.setText(null);
                lvPacientes.setVisibility(View.GONE);
            }
            if (lvEspecialistas.getVisibility() != View.GONE) {
                txtEspecialista.setText(null);
                lvEspecialistas.setVisibility(View.GONE);
            }
        }else if (txtEspecialista.isFocused()) {
            lvEspecialistas.setVisibility(View.VISIBLE);
            llenar_busquedas();
            txtEspecialista.setText(null);
            if (lvPacientes.getVisibility() != View.GONE) {
                txtPaciente.setText(null);
                lvPacientes.setVisibility(View.GONE);
            }
            if (lvServicios.getVisibility() != View.GONE) {
                txtServicio.setText(null);
                lvServicios.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (txtPaciente.isFocused()) {
            idPaciente = busquedaPacientes.get(i).getId();
            txtPaciente.setText(busquedaPacientes.get(i).getNombre());
            txtPaciente.clearFocus();
            lvPacientes.setVisibility(View.GONE);
        } else  if (txtServicio.isFocused())  {
            idServicio = busquedaServicios.get(i).getId();
            txtServicio.setText(busquedaServicios.get(i).getServicio());
            txtServicio.clearFocus();
            lvServicios.setVisibility(View.GONE);
        }  else  if (txtEspecialista.isFocused())  {
            idEspecialista = busquedaEspecialistas.get(i).getId();
            txtEspecialista.setText(busquedaEspecialistas.get(i).getName());
            txtEspecialista.clearFocus();
            lvEspecialistas.setVisibility(View.GONE);
        }
    }

    public void llenar_pacientes () {
        String[] nombres = new String[busquedaPacientes.size()];
        for (int i = 0; i < busquedaPacientes.size(); i++) {
            nombres[i] = busquedaPacientes.get(i).getNombre();
        }
        adapterPacientes = null;
        adapterPacientes  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombres);
        lvPacientes.setAdapter(null);
        lvPacientes.setAdapter(adapterPacientes);
    }

    public void llenar_servicios () {
        String[] servicios = new String[busquedaServicios.size()];
        for (int i = 0; i < busquedaServicios.size(); i++) {
            servicios[i] = busquedaServicios.get(i).getServicio();
        }
        adapterServicios = null;
        adapterServicios  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, servicios);
        lvServicios.setAdapter(null);
        lvServicios.setAdapter(adapterServicios);
    }

    public void llenar_especialistas () {
        String[] especialistas = new String[busquedaEspecialistas.size()];
        for (int i = 0; i < busquedaEspecialistas.size(); i++) {
            especialistas[i] = busquedaEspecialistas.get(i).getName();
        }
        adapterEspecialistas = null;
        adapterEspecialistas  = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, especialistas);
        lvEspecialistas.setAdapter(null);
        lvEspecialistas.setAdapter(adapterEspecialistas);
    }

    public void llenar_busquedas() {
        busquedaPacientes.clear();
        busquedaServicios.clear();
        for (int i = 0; i < pacientes.size(); i++) {
            busquedaPacientes.add(pacientes.get(i));
        }
        for (int i = 0; i < servicios.size(); i++) {
            busquedaServicios.add(servicios.get(i));
        }
        for (int i = 0; i < especialistas.size(); i++) {
            busquedaEspecialistas.add(especialistas.get(i));
        }
        llenar_pacientes();
        llenar_servicios();
        llenar_especialistas();
    }

    public void addEvent() {
        progressDialog.ShowProgressDialog();
        EventsServices service = RetrofitClientInstance.getRetrofitInstance().create(EventsServices.class);
        Call<RetrofitMessage> call = service.addEvent(clinica,
                txtDateTime.getText().toString(),
                String.valueOf(idServicio),
                txtComentario.getText().toString(),
                txtColor.getText().toString(),
                end,
                String.valueOf(idPaciente),
                String.valueOf(idEspecialista));
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progressDialog.dismissDialog();
                if (response.code() == 200){
                    Toasty.success(getContext(), "Se agendó una cita", Toast.LENGTH_LONG, true).show();
                    anyEvent =  true;
                    dismiss();
                } else {
                    dismiss();
                    Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<RetrofitMessage> call, Throwable t){
                Toasty.error(getContext(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                progressDialog.dismissDialog();
                dismiss();
            }
        });
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        date.set(Calendar.SECOND, 0);
                        int month = monthOfYear + 1;
                        txtDateTime.setText(Tool_Fecha.getStringDateTime(date.getTime()));
                        date.add(Calendar.MINUTE, 45);
                        end = Tool_Fecha.getStringDateTime(date.getTime());
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void limpiar_colores() {
        txtColor.setText(color);
        int myColor = Color.parseColor(color);
        txtColor.setBackgroundColor(myColor);
        txtColor.setTextColor(Color.WHITE);
        colorGroup1.clearCheck();
        colorGroup2.clearCheck();
        colorGroup3.clearCheck();
        layoutColor.setVisibility(View.GONE);
    }

    private boolean verificar_campos() {
        progressDialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtServicio.getText())) {
            txtServicio.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtColor.getText())) {
            txtColor.setError(getString(R.string.campo_requerido));
            return false;
        }  else if (TextUtils.isEmpty(txtDateTime.getText())) {
            txtDateTime.setError(getString(R.string.campo_requerido));
            return false;
        }if (TextUtils.isEmpty(txtEspecialista.getText())) {
            txtEspecialista.setError(getString(R.string.campo_requerido));
            return false;
        }if (TextUtils.isEmpty(txtPaciente.getText())) {
            txtPaciente.setError(getString(R.string.campo_requerido));
            return false;
        }else if (TextUtils.isEmpty(txtComentario.getText())) {
            txtComentario.setText(R.string.sin_comentarios);
            return true;
        }
        return true;
    }

    public void internetAdd() {
        if (Tool_Internet.isOnlineNet()) {
            progressDialog.dismissDialog();
            addEvent();
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
    public void configuracion_incial () {
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Especialista") || Tools_Users_Defaults.user_rol(getContext()).equals("Individual")) {
            txtEspecialista.setText(Tools_Users_Defaults.user_name(getContext()));
            idEspecialista = Tools_Users_Defaults.user_id(getContext());
            txtEspecialista.setClickable(false);
            txtEspecialista.setFocusable(false);
        }
    }
}