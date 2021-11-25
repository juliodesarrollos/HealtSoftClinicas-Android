package com.juliovazquez.hsclinic.Activities.Agenda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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

import com.juliovazquez.hsclinic.Activities.Paciente.Activity_Menu_Paciente;
import com.juliovazquez.hsclinic.Pojos.RetrofitFullEvent;
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
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Detalles_Evento extends AppCompatActivity implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener, AdapterView.OnItemClickListener {
    Toolbar toolbar;
    Calendar date;
    EditText txtDateTime, txtColor, txtComentario, txtServicio, txtPaciente, txtEspecialista;
    ListView lvPacientes, lvServicios,  lvEspecialistas;
    String color = "",  clinica = "", end = "", eventid = "";
    RadioGroup colorGroup1, colorGroup2, colorGroup3;
    LinearLayout layoutColor;
    Button btnVerPX, btnWhatsApp, btnLlamar, btnEliminar, btnEditar, btnPagar, btnCancelar;
    RetrofitFullEvent event = null;
    RadioButton c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15;
    int idPaciente = 0, idServicio = 0, idEspecialista = 0;
    RetrofitPacient pacient;
    List<RetrofitPacient> pacientes = new ArrayList<>();
    List<RetrofitPacient> busquedaPacientes = new ArrayList<>();
    List<RetrofitServices>  servicios = new ArrayList<>();
    List<RetrofitServices>  busquedaServicios = new ArrayList<>();
    List<RetrofitUser>  especialistas = new ArrayList<>();
    List<RetrofitUser>  busquedaEspecialistas = new ArrayList<>();
    Tool_Progress_Dialog progressDoalog;
    ArrayAdapter<String> adapterPacientes, adapterServicios, adapterEspecialistas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_evento);
        toolbar = findViewById(R.id.toolbar_evento);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        clinica = Tools_Users_Defaults.user_clinic(Activity_Detalles_Evento.this);
        if (getIntent().getStringExtra("IDEVENTO") != null) {
            eventid = getIntent().getStringExtra("IDEVENTO");
        }
        if (eventid != null) {
            btnPagar = findViewById(R.id.btnPagar);
            btnCancelar = findViewById(R.id.btnCancelar);
            btnEditar = findViewById(R.id.btnEditar);
            btnEliminar = findViewById(R.id.btnEliminar);
            btnLlamar = findViewById(R.id.btnLlamar);
            btnWhatsApp = findViewById(R.id.btnWhatsApp);
            btnVerPX = findViewById(R.id.btnVerPX);
            txtDateTime = findViewById(R.id.txtDateTime);
            txtPaciente = findViewById(R.id.txtPaciente);
            txtEspecialista =  findViewById(R.id.txtEspecialista);
            lvPacientes = findViewById(R.id.lvPacientes);
            lvServicios = findViewById(R.id.lvServicios);
            lvEspecialistas  = findViewById(R.id.lvEspecialistas);
            txtColor = findViewById(R.id.txtColor);
            txtComentario = findViewById(R.id.txtComentario);
            txtServicio = findViewById(R.id.txtTitulo);
            colorGroup1 = findViewById(R.id.colorGroup1);
            colorGroup2 = findViewById(R.id.colorGroup2);
            colorGroup3 = findViewById(R.id.colorGroup3);
            layoutColor = findViewById(R.id.layoutColor);
            progressDoalog = new Tool_Progress_Dialog(Activity_Detalles_Evento.this);
            //btnClose = findViewById(R.id.btnClose);
            c1 = findViewById(R.id.color1);
            c2 = findViewById(R.id.color2);
            c3 = findViewById(R.id.color3);
            c4 = findViewById(R.id.color4);
            c5 = findViewById(R.id.color5);
            c6 = findViewById(R.id.color6);
            c7 = findViewById(R.id.color7);
            c8 = findViewById(R.id.color8);
            c9 = findViewById(R.id.color9);
            c10 = findViewById(R.id.color10);
            c11 = findViewById(R.id.color11);
            c12 = findViewById(R.id.color12);
            c13 = findViewById(R.id.color13);
            c14 = findViewById(R.id.color14);
            c15 = findViewById(R.id.color15);
            clinica = Tools_Users_Defaults.user_clinic(Activity_Detalles_Evento.this);

            btnCancelar.setOnClickListener(this::onClick);
            btnPagar.setOnClickListener(this::onClick);
            btnEditar.setOnClickListener(this::onClick);
            btnLlamar.setOnClickListener(this::onClick);
            btnWhatsApp.setOnClickListener(this::onClick);
            btnVerPX.setOnClickListener(this::onClick);
            btnEliminar.setOnClickListener(this::onClick);
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
            //btnAgregar.setOnClickListener(this::onClick);
            //btnClose.setOnClickListener(this::onClick);
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
            servicios = RetrofitServices.recuperar_instancia();
            configuracion_inicial();
            llenar_busquedas();
            internetEvento();
        }
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
            case R.id.btnVerPX:
                goPaciente();
                break;
            case R.id.btnWhatsApp:
                goWhatsApp();
                break;
            case R.id.btnEditar:
                internetUpdate();
                break;
            case R.id.btnEliminar:
                internetEliminar();
                break;
            case R.id.btnLlamar:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+event.getTelefono().toString())));
                break;
            case R.id.btnPagar:
                internetStatus("Pagado", "Se agrego el pago a la cita");
                break;
            case R.id.btnCancelar:
                internetStatus("Cancelado", "Se cancelo la cita");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    public void getEvent() {
        progressDoalog.ShowProgressDialog();
        EventsServices service = RetrofitClientInstance.getRetrofitInstance().create(EventsServices.class);
        Call<List<RetrofitFullEvent>> call = service.getEvent(clinica, eventid);
        call.enqueue(new Callback<List<RetrofitFullEvent>>() {
            @Override
            public void onResponse(Call<List<RetrofitFullEvent>> call, Response<List<RetrofitFullEvent>> response) {
                progressDoalog.dismissDialog();
                if (response.code() == 200){
                    event = response.body().get(0);
                    llenar_datos();
                } else {
                    Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salió mal", Toasty.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitFullEvent>> call, Throwable t) {
                progressDoalog.dismissDialog();
                Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salió mal", Toasty.LENGTH_LONG).show();
                //dismiss();
            }
        });
    }

    private void eliminar_evento() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Detalles_Evento.this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(getString(R.string.desea_eliminar_cita))
                .setConfirmButton(getString(R.string.no), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton(getString(R.string.si), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        deleteEvent();
                    }
                })
                .show();
    }

    public void updateStatus(String status, String message) {
        progressDoalog.ShowProgressDialog();
        EventsServices service = RetrofitClientInstance.getRetrofitInstance().create(EventsServices.class);
        Call<RetrofitMessage> call = service.updateStatus(clinica, status, eventid);
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progressDoalog.dismissDialog();
                if (response.code() == 200){
                    Toasty.info(Activity_Detalles_Evento.this, message, Toast.LENGTH_LONG, true).show();
                    Tools_Users_Defaults.setEventChartFlag(Activity_Detalles_Evento.this, true);
                    finish();
                } else {
                    Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<RetrofitMessage> call, Throwable t){
                progressDoalog.dismissDialog();
                Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void deleteEvent() {
        progressDoalog.ShowProgressDialog();
        EventsServices service = RetrofitClientInstance.getRetrofitInstance().create(EventsServices.class);
        Call<RetrofitMessage> call = service.deleteEvent(clinica, eventid);
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                if (response.code() == 200){
                    Toasty.info(Activity_Detalles_Evento.this, "Se ha eliminado una cita", Toast.LENGTH_LONG, true).show();
                    Tools_Users_Defaults.setEventFlag(Activity_Detalles_Evento.this, true);
                    Tools_Users_Defaults.setEventChartFlag(Activity_Detalles_Evento.this, true);
                    finish();
                } else {
                    Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<RetrofitMessage> call, Throwable t){
                progressDoalog.dismissDialog();
                Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void updateEvent() {
        progressDoalog.ShowProgressDialog();
        EventsServices service = RetrofitClientInstance.getRetrofitInstance().create(EventsServices.class);
        Call<RetrofitMessage> call = service.updateEvent(clinica,
                txtDateTime.getText().toString(),
                String.valueOf(idServicio),
                txtComentario.getText().toString(),
                color,
                end,
                String.valueOf(idPaciente),
                String.valueOf(idEspecialista),
                eventid);
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progressDoalog.dismissDialog();
                if (response.code() == 200){
                    Toasty.info(Activity_Detalles_Evento.this, "Se ha actualizado una cita", Toast.LENGTH_LONG, true).show();
                    Tools_Users_Defaults.setEventFlag(Activity_Detalles_Evento.this, true);
                    Tools_Users_Defaults.setEventChartFlag(Activity_Detalles_Evento.this, true);
                    for (int i = 0; i < pacientes.size(); i++) {
                        if (pacientes.get(i).getId() == idPaciente) pacient = pacientes.get(i);
                    }
                    finish();
                } else {
                    Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<RetrofitMessage> call, Throwable t){
                progressDoalog.dismissDialog();
                Toasty.error(Activity_Detalles_Evento.this, "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void llenar_pacientes () {
        String[] nombres = new String[busquedaPacientes.size()];
        for (int i = 0; i < busquedaPacientes.size(); i++) {
            nombres[i] = busquedaPacientes.get(i).getNombre();
        }
        adapterPacientes = null;
        adapterPacientes  = new ArrayAdapter<String>(Activity_Detalles_Evento.this, android.R.layout.simple_spinner_dropdown_item, nombres);
        lvPacientes.setAdapter(null);
        lvPacientes.setAdapter(adapterPacientes);
    }

    public void llenar_servicios () {
        String[] servicios = new String[busquedaServicios.size()];
        for (int i = 0; i < busquedaServicios.size(); i++) {
            servicios[i] = busquedaServicios.get(i).getServicio();
        }
        adapterServicios = null;
        adapterServicios  = new ArrayAdapter<String>(Activity_Detalles_Evento.this, android.R.layout.simple_spinner_dropdown_item, servicios);
        lvServicios.setAdapter(null);
        lvServicios.setAdapter(adapterServicios);
    }

    public void llenar_especialistas () {
        String[] especialistas = new String[busquedaEspecialistas.size()];
        for (int i = 0; i < busquedaEspecialistas.size(); i++) {
            especialistas[i] = busquedaEspecialistas.get(i).getName();
        }
        adapterEspecialistas = null;
        adapterEspecialistas  = new ArrayAdapter<String>(Activity_Detalles_Evento.this, android.R.layout.simple_spinner_dropdown_item, especialistas);
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

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(Activity_Detalles_Evento.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(Activity_Detalles_Evento.this, new TimePickerDialog.OnTimeSetListener() {
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

    private void llenar_datos() {
        setStatus(event.getEstatus());
        color = event.getColor();
        txtServicio.setText(event.getTitle());
        txtComentario.setText(event.getDescription());
        txtDateTime.setText(event.getStart());
        Calendar date = Calendar.getInstance();
        date.setTime(Tool_Fecha.getDateTimeFromString(event.getStart()));
        date.add(Calendar.MINUTE, 45);
        end = Tool_Fecha.getStringDateTime(date.getTime());
        limpiar_colores();
        idPaciente = event.getIdPaciente();
        idEspecialista = event.getIdDoctor();
        for (int i = 0; i < pacientes.size(); i++) {
            if (pacientes.get(i).getId() == event.getIdPaciente()) {
                txtPaciente.setText(pacientes.get(i).getNombre());
                pacient = pacientes.get(i);
            }
        }
        for (int i = 0; i < especialistas.size(); i++) {
            if (especialistas.get(i).getId() == event.getIdDoctor()) txtEspecialista.setText(especialistas.get(i).getName());
        }
        for (int i = 0; i < servicios.size(); i++) {
            if (servicios.get(i).getServicio().equals(event.getTitle())) idServicio = servicios.get(i).getId();
        }
    }

    private void goWhatsApp() {
        String mensaje = "Hola!, el especialista " + Tools_Users_Defaults.user_name(Activity_Detalles_Evento.this) + " le recuerda su cita con fecha " + event.getStart();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String uri = "https://api.whatsapp.com/send?phone=521" + event.getTelefono() + "&text=" + mensaje;
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    private void goPaciente() {
        int id = Integer.valueOf(event.getIdPaciente());
        Intent i = new Intent(Activity_Detalles_Evento.this, Activity_Menu_Paciente.class);
        i.putExtra("IDPACIENTE", pacient.getId());
        i.putExtra("NOMBREPACIENTE", pacient.getNombre());
        i.putExtra("CORREOPACIENTE", pacient.getCorreo());
        i.putExtra("TELEFONOPACIENTE", pacient.getTelefono());
        i.putExtra("EDADPACIENTE", pacient.getFechaNacimiento());
        i.putExtra("URLPACIENTE", pacient.getFotoUrl());
        startActivity(i);
    }

    public void setStatus (String status) {
        if (status.equals("Pagado")) {
            btnPagar.setText("Cita pagada");
            btnCancelar.getBackground().setAlpha(60);
            btnPagar.setEnabled(false);
            btnCancelar.setEnabled(false);
        } else if (status.equals("Cancelado")) {
            btnCancelar.setText("Cita cancelada");
            btnPagar.getBackground().setAlpha(60);
            btnPagar.setEnabled(false);
            btnCancelar.setEnabled(false);
        } else if (status.equals("Creado"))  {
            btnPagar.setText("Agregar pago");
            btnCancelar.setText("Cancelar cita");
            btnPagar.setEnabled(true);
            btnCancelar.setEnabled(true);
        }
    }

    public void internetEliminar() {
        if (Tool_Internet.isOnlineNet()) {
            eliminar_evento();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Detalles_Evento.this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetEliminar();
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

    public void internetEvento() {
        if (Tool_Internet.isOnlineNet()) {
            getEvent();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Detalles_Evento.this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetEvento();
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

    public void internetStatus(String status, String message) {
        if (Tool_Internet.isOnlineNet()) {
            updateStatus(status, message);
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Detalles_Evento.this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetStatus(status, message);
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

    public void internetUpdate() {
        if (Tool_Internet.isOnlineNet()) {
            updateEvent();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Detalles_Evento.this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        internetUpdate();
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

    public void configuracion_inicial() {
        if (Tools_Users_Defaults.user_rol(Activity_Detalles_Evento.this).equals("Practicante")) {
            btnEliminar.setVisibility(View.GONE);
            btnEditar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
            btnPagar.setVisibility(View.GONE);
            btnLlamar.setVisibility(View.GONE);
            btnWhatsApp.setVisibility(View.GONE);
        } else if (Tools_Users_Defaults.user_rol(Activity_Detalles_Evento.this).equals("Especialista")) {
            btnEliminar.setVisibility(View.GONE);
            btnEditar.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
            btnPagar.setVisibility(View.GONE);
        }
    }
}