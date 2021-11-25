package com.juliovazquez.hsclinic.Activities.Paciente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Tools.Tool_Images;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class Activity_Menu_Paciente extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    ImageView imgPaciente;
    TextView txtNombre;
    TextView txtEdad;
    TextView txtCorreo;
    TextView txtTelefono;
    int id = 0;
    String nombre = "";
    String edad = "";
    String correo ="";
    Long telefono = Long.valueOf(0);
    String fotoUrl = "";
    Button btnArchivo;
    Button btnNotas;
    Button btnSignos;
    Button btnEstudios;
    TextView title;
    Button btnUpdateImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_paciente);
        toolbar = findViewById(R.id.toolbar_pacientes);
        title  = toolbar.findViewById(R.id.title_preguntas);
        imgPaciente = findViewById(R.id.imgPaciente);
        txtCorreo  = findViewById(R.id.txtCorreo);
        txtEdad = findViewById(R.id.txtEdad);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        btnArchivo =  findViewById(R.id.btnArchivo);
        btnNotas = findViewById(R.id.btnNotas);
        btnSignos = findViewById(R.id.btnSignos);
        btnEstudios = findViewById(R.id.btnEstudios);
        btnUpdateImage  = findViewById(R.id.btnUpdateImage);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        id = getIntent().getExtras().getInt("IDPACIENTE");
        nombre = getIntent().getExtras().getString("NOMBREPACIENTE");
        edad = getIntent().getExtras().getString("EDADPACIENTE");
        correo = getIntent().getExtras().getString("CORREOPACIENTE");
        telefono = getIntent().getExtras().getLong("TELEFONOPACIENTE");
        fotoUrl = getIntent().getExtras().getString("URLPACIENTE");
        btnEstudios.setOnClickListener(this);
        btnSignos.setOnClickListener(this);
        btnNotas.setOnClickListener(this);
        imgPaciente.setOnClickListener(this::onClick);
        btnArchivo.setOnClickListener(this);
        btnUpdateImage.setOnClickListener(this);
        goArchivo();
        startConfig();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnArchivo: goArchivo(); setButtons(view); break;
            case R.id.btnNotas: goNotas(); setButtons(view); break;
            case R.id.btnSignos: goSignos(); setButtons(view); break;
            case R.id.btnEstudios: goEstudios(); setButtons(view); break;
            case R.id.btnUpdateImage: updateImage(); break;
            case R.id.imgPaciente: ver_paciente(); break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("editar_accion").setIcon(R.drawable.ic_editar).setVisible(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (!Tools_Users_Defaults.user_rol(Activity_Menu_Paciente.this).equals("Practicante")) editar_paciente();
                else Toasty.error(Activity_Menu_Paciente.this, "No tienes los permisos para realizar esta acción", Toasty.LENGTH_SHORT).show();
                return false;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    public void ver_paciente() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment_Ver_Imagen d = new Fragment_Ver_Imagen();
        d.url = fotoUrl;
        d.show(fragmentManager, "tag");
    }

    public void startConfig () {
        txtTelefono.setText(telefono.toString());
        txtNombre.setText(nombre);
        txtEdad.setText(edad);
        txtCorreo.setText(correo);
        Picasso.with(Activity_Menu_Paciente.this)
            .load(fotoUrl)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .resize(90 , 90)
            .centerCrop()
            .transform(new Tool_Images())
            .into(imgPaciente);
    }

    public void updateImage() {
        Picasso.with(Activity_Menu_Paciente.this).invalidate(fotoUrl);
        Picasso.with(Activity_Menu_Paciente.this)
                .load(fotoUrl)
                .placeholder(R.drawable.ic_menu_pacientes_icon)
                .error(R.drawable.ic_menu_pacientes_icon)
                .resize(90 , 90)
                .centerCrop()
                .transform(new Tool_Images())
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imgPaciente);
    }

    public void goArchivo() {
        Fragment_Archivo fragment_archivo = (Fragment_Archivo) getSupportFragmentManager().findFragmentById(R.id.fragmentArchivo);
        Fragment_Notas fragment_notas = (Fragment_Notas) getSupportFragmentManager().findFragmentById(R.id.fragmentNotas);
        Fragment_Signos fragment_signos = (Fragment_Signos) getSupportFragmentManager().findFragmentById(R.id.fragmentSignos);
        Fragment_Estudios fragment_estudios = (Fragment_Estudios) getSupportFragmentManager().findFragmentById(R.id.fragmentEstudios);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(fragment_archivo);
        fragmentTransaction.hide(fragment_notas);
        fragmentTransaction.hide(fragment_signos);
        fragmentTransaction.hide(fragment_estudios);
        fragment_archivo.idPaciente = String.valueOf(id);
        fragment_notas.idPaciennte = String.valueOf(id);
        fragment_signos.idPaciente = String.valueOf(id);
        fragment_estudios.idPaciente = String.valueOf(id);
        fragmentTransaction.commitNow();
        title.setText("Archivo");
        setButtons(btnArchivo);
    }

    public void goNotas() {
        Fragment_Archivo fragment_archivo = (Fragment_Archivo) getSupportFragmentManager().findFragmentById(R.id.fragmentArchivo);
        Fragment_Notas fragment_notas = (Fragment_Notas) getSupportFragmentManager().findFragmentById(R.id.fragmentNotas);
        Fragment_Signos fragment_signos = (Fragment_Signos) getSupportFragmentManager().findFragmentById(R.id.fragmentSignos);
        Fragment_Estudios fragment_estudios = (Fragment_Estudios) getSupportFragmentManager().findFragmentById(R.id.fragmentEstudios);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment_archivo);
        fragmentTransaction.show(fragment_notas);
        fragmentTransaction.hide(fragment_signos);
        fragmentTransaction.hide(fragment_estudios);
        fragmentTransaction.commitNow();
        title.setText("Notas  terapeuticas");
    }

    public void goSignos() {
        Fragment_Archivo fragment_archivo = (Fragment_Archivo) getSupportFragmentManager().findFragmentById(R.id.fragmentArchivo);
        Fragment_Notas fragment_notas = (Fragment_Notas) getSupportFragmentManager().findFragmentById(R.id.fragmentNotas);
        Fragment_Signos fragment_signos = (Fragment_Signos) getSupportFragmentManager().findFragmentById(R.id.fragmentSignos);
        Fragment_Estudios fragment_estudios = (Fragment_Estudios) getSupportFragmentManager().findFragmentById(R.id.fragmentEstudios);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment_archivo);
        fragmentTransaction.hide(fragment_notas);
        fragmentTransaction.show(fragment_signos);
        fragmentTransaction.hide(fragment_estudios);
        fragmentTransaction.commitNow();
        title.setText("Signos vitales");
    }

    public void goEstudios() {
        Fragment_Archivo fragment_archivo = (Fragment_Archivo) getSupportFragmentManager().findFragmentById(R.id.fragmentArchivo);
        Fragment_Notas fragment_notas = (Fragment_Notas) getSupportFragmentManager().findFragmentById(R.id.fragmentNotas);
        Fragment_Signos fragment_signos = (Fragment_Signos) getSupportFragmentManager().findFragmentById(R.id.fragmentSignos);
        Fragment_Estudios fragment_estudios = (Fragment_Estudios) getSupportFragmentManager().findFragmentById(R.id.fragmentEstudios);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(fragment_archivo);
        fragmentTransaction.hide(fragment_notas);
        fragmentTransaction.hide(fragment_signos);
        fragmentTransaction.show(fragment_estudios);
        fragmentTransaction.commitNow();
        title.setText("Estudios");
    }

    public void setButtons(View view) {
        Button current = (Button) view;
        btnEstudios.setBackgroundColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.white));
        btnEstudios.setTextColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.black));
        btnArchivo.setBackgroundColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.white));
        btnArchivo.setTextColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.black));
        btnNotas.setBackgroundColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.white));
        btnNotas.setTextColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.black));
        btnSignos.setBackgroundColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.white));
        btnSignos.setTextColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.black));
        current.setBackgroundColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.grayBackground));
        current.setTextColor(Activity_Menu_Paciente.this.getResources().getColor(R.color.primaryBlueColor));
    }void

    editar_paciente() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment_Editar_Paciente editar_paciente = new Fragment_Editar_Paciente();
        editar_paciente.show(fragmentManager, "tag");
        editar_paciente.fnac = edad;
        editar_paciente.nombre = nombre;
        editar_paciente.correo = correo;
        editar_paciente.telefono = telefono;
        editar_paciente.fotoUrl = fotoUrl;
        editar_paciente.idPa = String.valueOf(id);
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                if (editar_paciente.anyEdit) {
                    nombre = editar_paciente.txtNombre.getText().toString();
                    edad = editar_paciente.txtFechaNacimiento.getText().toString();
                    correo = editar_paciente.txtCorreo.getText().toString();
                    telefono = Long.valueOf(editar_paciente.txtTelefono.getText().toString());
                    startConfig();
                    updateImage();
                }
                fragmentManager.unregisterFragmentLifecycleCallbacks(this);
            }
        }, false);
    }
}