package com.juliovazquez.hsclinic.Activities.Agenda;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.github.tibolte.agendacalendarview.widgets.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.juliovazquez.hsclinic.Pojos.Drawable_Event_Instance;
import com.juliovazquez.hsclinic.Pojos.RetrofitEvents;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.Pojos.RetrofitPacient;
import com.juliovazquez.hsclinic.Pojos.RetrofitServices;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.EventsServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Services.TokenService;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
import com.juliovazquez.hsclinic.Tools.Tool_Sweet_Alert;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;
import com.riontech.calendar.CustomCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Agenda#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agenda extends Fragment implements CalendarPickerController, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String idEvent = null;
    AgendaCalendarView mAgendaCalendarView;
    Tool_Progress_Dialog progress_dialog;
    Calendar maxDate;
    Calendar minDate;
    CalendarPickerController calendarPickerController;
    List<CalendarEvent> eventList = new ArrayList<>();
    String clinica = "";
    int idUser = 0;
    String address = "";
    String token;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private CustomCalendar customCalendar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mRootView;

    public Fragment_Agenda() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agenda.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agenda newInstance(String param1, String param2) {
        Fragment_Agenda fragment = new Fragment_Agenda();
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
    public void onResume() {
        super.onResume();
        if (Tools_Users_Defaults.getEventState(getContext())) {
            internetEvents();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        minDate = Calendar.getInstance();
        maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        mAgendaCalendarView = getView().findViewById(R.id.agenda_calendar_view);
        mAgendaCalendarView.setSaveEnabled(false);
        calendarPickerController = this;

        clinica = Tools_Users_Defaults.user_clinic(getContext());
        idUser = Tools_Users_Defaults.user_id(getContext());
        address = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        FloatingActionButton fab = getView().findViewById(R.id.fab);
        fab.setAlpha(0.80f);

        fab.setOnClickListener(this);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                token = task.getResult();
                tokenActions();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Failed to get the token : " + e.getLocalizedMessage());
            }
        });
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Practicante")) fab.setVisibility(View.GONE);
        getEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_agenda, container, false);
        }
        return mRootView;
    }

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        if (Drawable_Event_Instance.recuperar_instancia() != null && event.getId() > 0)
            detalles_evento(String.valueOf(event.getId()));
    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }

    public void getEvents() {
        progress_dialog.ShowProgressDialog();
        Tools_Users_Defaults.setEventFlag(getContext(), false);
        EventsServices service = RetrofitClientInstance.getRetrofitInstance().create(EventsServices.class);
        Call<List<RetrofitEvents>> call;
        if (Tools_Users_Defaults.user_rol(getContext()).equals("Admin") || Tools_Users_Defaults.user_rol(getContext()).equals("Secretaria")) {
            call = service.getAllEvents(clinica);
        } else {
            call = service.getEventsEspecialista(clinica, String.valueOf(idUser));
        }
        call.enqueue(new Callback<List<RetrofitEvents>>() {
            @Override
            public void onResponse(Call<List<RetrofitEvents>> call, Response<List<RetrofitEvents>> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    RetrofitEvents.borrar_instancia();
                    RetrofitEvents.crear_instancia(response.body());
                    if (RetrofitEvents.recuperar_instancia() != null) showEvents();
                } else if (response.code() == 404){
                    Toasty.error(getActivity(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
                }
            }
            @Override
            public void onFailure(Call<List<RetrofitEvents>> call, Throwable t) {
                progress_dialog.dismissDialog();
                Toasty.error(getActivity(), "Ups! Algo salio mal", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    public void detalles_evento(String id) {
        Intent i = new Intent(getContext(), Activity_Detalles_Evento.class);
        i.putExtra("IDEVENTO", id);
        getContext().startActivity(i);
    }

    public void showEvents() {
        eventList.clear();
        Drawable_Event_Instance.borar_instancia();
        List<DrawableCalendarEvent> events = new ArrayList<>();
        Object ls = new Object();
        for (int i = 0; i < RetrofitEvents.recuperar_instancia().size(); i++){
            long id = (RetrofitEvents.recuperar_instancia().get(i).getId());
            int color = (Color.parseColor(RetrofitEvents.recuperar_instancia().get(i).getColor()));
            String title = (RetrofitEvents.recuperar_instancia().get(i).getTitle());
            String desc = (RetrofitEvents.recuperar_instancia().get(i).getDoctor());
            String location = RetrofitEvents.recuperar_instancia().get(i).getPacient();
            Calendar cds = Calendar.getInstance();
            long numero = 222222222;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                cds.setTime(sdf.parse(RetrofitEvents.recuperar_instancia().get(i).getStart()));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DrawableCalendarEvent event = new DrawableCalendarEvent(id, color, title, desc, location, cds.getTimeInMillis(), numero, 0, desc, R.layout.item_event_calendar);
            events.add(event);
            Drawable_Event_Instance.crear_instancia(events);
            ls = events;
        }
        eventList = ((List<CalendarEvent>) ls);
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), calendarPickerController);
        mAgendaCalendarView.addEventRenderer(new DrawableEventRenderer());
        if (idEvent != null) detalles_evento(idEvent);
        idEvent = null;
    }

    public void internetEvents() {
        if (Tool_Internet.isOnlineNet()) {
            getEvents();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetEvents();
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

    public void tokenActions() {
        TokenService service = RetrofitClientInstance.getRetrofitInstance().create(TokenService.class);
        Call<RetrofitMessage> call = service.tokenActions(clinica, String.valueOf(idUser), address, token, "ANDROID");
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
            }
            @Override
            public void onFailure(Call<RetrofitMessage> call, Throwable t) {
            }
        });
    }

    public void addEvent() {
        if (RetrofitPacient.recuperar_instancia() != null && RetrofitServices.recuperar_instancia() != null) {
            FragmentManager fragmentManager = getFragmentManager();
            Fragment_Agregar_Evento d = new Fragment_Agregar_Evento();
            d.show(fragmentManager, "tag");
            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    super.onFragmentViewDestroyed(fm, f);
                    if (d.anyEvent) internetEvents();
                    Tools_Users_Defaults.setEventChartFlag(getContext(), true);
                    fragmentManager.unregisterFragmentLifecycleCallbacks(this);
                }
            }, false);
        } else {
            Tool_Sweet_Alert.ERROR_TYPE(getContext(), getString(R.string.crear_evento_sin_pacientes));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: addEvent(); break;
        }
    }
}