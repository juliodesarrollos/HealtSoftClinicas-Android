    package com.juliovazquez.hsclinic.Activities.Usuario;

    import android.content.Intent;
    import android.content.res.Configuration;
    import android.os.Build;
    import android.os.Bundle;
    import android.view.Menu;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.SearchView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.ActionBarDrawerToggle;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;
    import androidx.core.view.GravityCompat;
    import androidx.core.view.MenuItemCompat;
    import androidx.drawerlayout.widget.DrawerLayout;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    import com.google.android.material.navigation.NavigationView;
    import com.juliovazquez.hsclinic.Activities.Agenda.Fragment_Agenda;
    import com.juliovazquez.hsclinic.Activities.Agenda.Fragment_Agregar_Evento;
    import com.juliovazquez.hsclinic.Activities.Graficas.Fragment_Dashboard;
    import com.juliovazquez.hsclinic.Activities.Especialista.Fragment_Especialistas;
    import com.juliovazquez.hsclinic.Activities.Graficas.Fragment_Graficas;
    import com.juliovazquez.hsclinic.Activities.Historias.Activity_Preguntas;
    import com.juliovazquez.hsclinic.Activities.Historias.Fragment_Historias_Clinicas;
    import com.juliovazquez.hsclinic.Activities.Paciente.Fragment_Agregar_Paciente;
    import com.juliovazquez.hsclinic.Activities.Paciente.Fragment_Pacientes;
    import com.juliovazquez.hsclinic.Activities.Servicios.Fragment_Servicios;
    import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
    import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
    import com.juliovazquez.hsclinic.R;
    import com.juliovazquez.hsclinic.Tools.Tool_Internet;
    import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;

    import cn.pedant.SweetAlert.SweetAlertDialog;
    import es.dmoral.toasty.Toasty;

    public class Activity_Menu_Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
        DrawerLayout drawer;
        ActionBarDrawerToggle toggle;
        NavigationView navigationView;
        TextView txtHeaderName;
        TextView title;
        MenuItem searchItem;
        MenuItem reloadItem;
        MenuInflater inflater;
        String idEvent =  null;
        private static final int INTERVALO = 2000; //2 segundos para salir
        private long tiempoPrimerClick;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_menu_principal);
            if (getIntent().getStringExtra("ID_EVENT") != null) {
                idEvent = getIntent().getStringExtra("ID_EVENT");
            }
            Toolbar toolbar = findViewById(R.id.toolbar_home);
            title = toolbar.findViewById(R.id.title_principal);
            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            setSupportActionBar(toolbar);

            navigationView.setNavigationItemSelectedListener(this);
            toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            drawer.addDrawerListener(toggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            View header_view = navigationView.getHeaderView(0);
            txtHeaderName = header_view.findViewById(R.id.nav_header_textView);
            txtHeaderName.setText(Tools_Users_Defaults.user_name_rol(Activity_Menu_Principal.this));
            setTitle("");
            title.setText(getString(R.string.agenda));
            goAgenda();
            setMenu();
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_item_dashboard:
                    title.setText(getString(R.string.dashboard_mensual));
                    goDashboard();
                    reloadItem.setVisible(false);
                    searchItem.setVisible(false);
                    break;
                case R.id.nav_item_usuarios:
                    title.setText(getString(R.string.usuarios));
                    goEspecialistas();
                    reloadItem.setVisible(false);
                    searchItem.setVisible(true);
                    break;
                case R.id.nav_item_servicios:
                    reloadItem.setVisible(false);
                    title.setText(getString(R.string.servicios));
                    searchItem.setVisible(true);
                    goServicios();
                    break;
                case R.id.nav_item_historias:
                    reloadItem.setVisible(false);
                    title.setText(getString(R.string.historias_clinicas));
                    goHistorias();
                    searchItem.setVisible(true);
                    break;
                case R.id.nav_item_estadisticas:
                    title.setText(getString(R.string.estadisticas));
                    goGraficas();
                    reloadItem.setVisible(false);
                    searchItem.setVisible(false);
                    break;
                case R.id.nav_item_agenda:
                    title.setText(getString(R.string.agenda));
                    goAgenda();
                    reloadItem.setVisible(true);
                    searchItem.setVisible(false);
                    break;
                case R.id.nav_item_agendar:
                    title.setText(getString(R.string.agendar));
                    agendar();
                    break;
                case R.id.nav_item_pacientes:
                    title.setText(getString(R.string.pacientes));
                    reloadItem.setVisible(false);
                    goPacientes();
                    searchItem.setVisible(true);
                    break;
                case R.id.nav_item_crear_paciente:
                    agregar_paciente();
                    break;
                case R.id.nav_item_logout:
                    goLoginScreen();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        @Override
        protected void onPostCreate(@Nullable Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            toggle.syncState();
        }

        @Override
        public void onConfigurationChanged(@NonNull Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            toggle.onConfigurationChanged(newConfig);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.action_reload) {
                Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
                if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis() == false) {
                    fragment_agenda.internetEvents();
                }
                tiempoPrimerClick = System.currentTimeMillis();
            }
            if (toggle.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onBackPressed() {
            if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(this, getString(R.string.presionar_otra_vez), Toast.LENGTH_SHORT).show();
            }
            tiempoPrimerClick = System.currentTimeMillis();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_buscar, menu);
            reloadItem = menu.findItem(R.id.action_reload);
            searchItem = menu.findItem(R.id.action_search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            //permite modificar el hint que el EditText muestra por defecto
            searchView.setQueryHint("Buscar");
            searchItem.setVisible(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
                    Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
                    Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
                    Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
                    if (fragment_pacientes.isVisible()) {
                        fragment_pacientes.buscar(newText);
                        return true;
                    } else if (fragment_especialistas.isVisible()) {
                        fragment_especialistas.buscar(newText);
                        return true;
                    } else if (fragment_servicios.isVisible()) {
                        fragment_servicios.buscar(newText);
                        return true;
                    }else if (fragment_historias.isVisible()) {
                        fragment_historias.buscar(newText);
                        return true;
                    }
                    return true;
                }
            });
            return super.onCreateOptionsMenu(menu);
        }

        public void goPacientes() {
            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
            Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
            Fragment_Dashboard fragment_dashboard = (Fragment_Dashboard) getSupportFragmentManager().findFragmentById(R.id.fragmentDashboard);
            Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
            Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment_agenda);
            fragmentTransaction.hide(fragment_especialistas);
            fragmentTransaction.hide(fragment_dashboard);
            fragmentTransaction.hide(fragment_servicios);
            fragmentTransaction.hide(fragment_historias);
            fragmentTransaction.hide(fragment_graficas);
            fragmentTransaction.show(fragment_pacientes);
            fragmentTransaction.commitNow();
        }

        public void goAgenda() {
            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
            Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
            Fragment_Dashboard fragment_dashboard = (Fragment_Dashboard) getSupportFragmentManager().findFragmentById(R.id.fragmentDashboard);
            Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
            Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(fragment_agenda);
            fragmentTransaction.hide(fragment_especialistas);
            fragmentTransaction.hide(fragment_historias);
            fragmentTransaction.hide(fragment_servicios);
            fragmentTransaction.hide(fragment_dashboard);
            fragmentTransaction.hide(fragment_graficas);
            fragmentTransaction.hide(fragment_pacientes);
            fragmentTransaction.commitNow();
            if (idEvent != null) fragment_agenda.idEvent = idEvent;
            idEvent = null;
        }

        public void goEspecialistas() {
            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
            Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
            Fragment_Dashboard fragment_dashboard = (Fragment_Dashboard) getSupportFragmentManager().findFragmentById(R.id.fragmentDashboard);
            Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
            Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment_agenda);
            fragmentTransaction.show(fragment_especialistas);
            fragmentTransaction.hide(fragment_servicios);
            fragmentTransaction.hide(fragment_dashboard);
            fragmentTransaction.hide(fragment_graficas);
            fragmentTransaction.hide(fragment_pacientes);
            fragmentTransaction.hide(fragment_historias);
            fragmentTransaction.commitNow();
        }

        public void goServicios() {
            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
            Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
            Fragment_Dashboard fragment_dashboard = (Fragment_Dashboard) getSupportFragmentManager().findFragmentById(R.id.fragmentDashboard);
            Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
            Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment_agenda);
            fragmentTransaction.hide(fragment_especialistas);
            fragmentTransaction.hide(fragment_dashboard);
            fragmentTransaction.hide(fragment_graficas);
            fragmentTransaction.show(fragment_servicios);
            fragmentTransaction.hide(fragment_pacientes);
            fragmentTransaction.hide(fragment_historias);
            fragmentTransaction.commitNow();
        }

        public void goDashboard() {
            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
            Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
            Fragment_Dashboard fragment_dashboard = (Fragment_Dashboard) getSupportFragmentManager().findFragmentById(R.id.fragmentDashboard);
            Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
            Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment_agenda);
            fragmentTransaction.hide(fragment_especialistas);
            fragmentTransaction.show(fragment_dashboard);
            fragmentTransaction.hide(fragment_historias);
            fragmentTransaction.hide(fragment_servicios);
            fragmentTransaction.hide(fragment_graficas);
            fragmentTransaction.hide(fragment_pacientes);
            fragmentTransaction.commitNow();
        }

        public void goHistorias() {
            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
            Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
            Fragment_Dashboard fragment_dashboard = (Fragment_Dashboard) getSupportFragmentManager().findFragmentById(R.id.fragmentDashboard);
            Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
            Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment_agenda);
            fragmentTransaction.hide(fragment_especialistas);
            fragmentTransaction.hide(fragment_dashboard);
            fragmentTransaction.show(fragment_historias);
            fragmentTransaction.hide(fragment_graficas);
            fragmentTransaction.hide(fragment_servicios);
            fragmentTransaction.hide(fragment_pacientes);
            fragmentTransaction.commitNow();
        }

        public void goGraficas() {
            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
            Fragment_Historias_Clinicas fragment_historias = (Fragment_Historias_Clinicas) getSupportFragmentManager().findFragmentById(R.id.fragmentHistorias);
            Fragment_Dashboard fragment_dashboard = (Fragment_Dashboard) getSupportFragmentManager().findFragmentById(R.id.fragmentDashboard);
            Fragment_Servicios fragment_servicios = (Fragment_Servicios) getSupportFragmentManager().findFragmentById(R.id.fragmentServicios);
            Fragment_Especialistas fragment_especialistas = (Fragment_Especialistas) getSupportFragmentManager().findFragmentById(R.id.fragmentEspecialistas);
            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.hide(fragment_agenda);
            fragmentTransaction.hide(fragment_especialistas);
            fragmentTransaction.hide(fragment_dashboard);
            fragmentTransaction.hide(fragment_historias);
            fragmentTransaction.show(fragment_graficas);
            fragmentTransaction.hide(fragment_servicios);
            fragmentTransaction.hide(fragment_pacientes);
            fragmentTransaction.commitNow();
        }


        public void goLoginScreen() {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Activity_Menu_Principal.this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("¿Desea cerrar sesión?")
                .setConfirmButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("SALIR", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Tools_Users_Defaults.borrar_sesion(Activity_Menu_Principal.this);
                        Toasty.error(Activity_Menu_Principal.this, "Se ha cerrado su sesión", Toasty.LENGTH_LONG).show();
                        Intent intent = new Intent(Activity_Menu_Principal.this, Activity_Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
        }

        public void agregar_paciente() {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment_Agregar_Paciente d = new Fragment_Agregar_Paciente();
            Fragment_Pacientes fragment_pacientes = (Fragment_Pacientes) getSupportFragmentManager().findFragmentById(R.id.fragmentPacientes);
            d.show(fragmentManager, "tag");
            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    super.onFragmentViewDestroyed(fm, f);
                    if (d.anyPaciente) fragment_pacientes.internetGet();
                    fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                }
            }, false);
        }

        public void agendar() {
            if (RetrofitPacient.recuperar_instancia() != null && RetrofitServices.recuperar_instancia() != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment_Agregar_Evento d = new Fragment_Agregar_Evento();
                d.show(fragmentManager, "tag");
                fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                        super.onFragmentViewDestroyed(fm, f);
                        if (d.anyEvent) {
                            Fragment_Agenda fragment_agenda = (Fragment_Agenda) getSupportFragmentManager().findFragmentById(R.id.fragmentAgenda);
                            Fragment_Graficas fragment_graficas = (Fragment_Graficas) getSupportFragmentManager().findFragmentById(R.id.fragmentGraficas);
                            fragment_graficas.getChartsCitas();
                            fragment_agenda.getEvents();
                        }
                        fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                    }
                }, false);
            } else
                Toasty.error(Activity_Menu_Principal.this, getString(R.string.crear_evento_sin_pacientes), Toasty.LENGTH_LONG).show();
        }

        public void setMenu() {
            if (Tools_Users_Defaults.user_rol(Activity_Menu_Principal.this).equals("Especialista") || Tools_Users_Defaults.user_rol(Activity_Menu_Principal.this).equals("Secretaria")) {
                //navigationView.getMenu().getItem(0).setVisible(false);
                navigationView.getMenu().getItem(1).setVisible(false);
                navigationView.getMenu().getItem(2).setVisible(false);
                //navigationView.getMenu().getItem(3).setVisible(false);
                //navigationView.getMenu().getItem(4).setVisible(false);
                navigationView.getMenu().getItem(5).setVisible(false);
            } else if (Tools_Users_Defaults.user_rol(Activity_Menu_Principal.this).equals("Practicante")) {
                navigationView.getMenu().getItem(0).setVisible(false);
                navigationView.getMenu().getItem(1).setVisible(false);
                navigationView.getMenu().getItem(2).setVisible(false);
                navigationView.getMenu().getItem(3).setVisible(false);
                navigationView.getMenu().getItem(4).setVisible(false);
                navigationView.getMenu().getItem(5).setVisible(false);
                navigationView.getMenu().getItem(8).setVisible(false);
                navigationView.getMenu().getItem(9).setVisible(false);
                navigationView.getMenu().getItem(10).setVisible(false);
                navigationView.getMenu().getItem(11).setVisible(false);
            } else if (Tools_Users_Defaults.user_rol(Activity_Menu_Principal.this).equals("Individual")) {
                navigationView.getMenu().getItem(2).setVisible(false);
            }
        }
    }