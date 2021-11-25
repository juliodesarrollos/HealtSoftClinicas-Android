package com.juliovazquez.hsclinic.Activities.Paciente;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.PacientsServices;
import com.juliovazquez.hsclinic.Services.RetrofitClientInstance;
import com.juliovazquez.hsclinic.Tools.Tool_Images;
import com.juliovazquez.hsclinic.Tools.Tool_Internet;
import com.juliovazquez.hsclinic.Tools.Tool_Progress_Dialog;
import com.juliovazquez.hsclinic.Tools.Tools_Users_Defaults;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.io.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Editar_Paciente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Editar_Paciente extends DialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText txtFechaNacimiento;
    DatePickerDialog picker;
    EditText txtNombre;
    EditText txtCorreo;
    EditText txtTelefono;
    Button btnEditar, btnFoto, btnClose;
    String fechaNacimiento = "";
    ImageView imgPaciente, imgContorno;
    File shareFile = null;
    OutputStream output = null;
    String clinica = "", idPa = "", nombre = "", fotoUrl = "", fnac = "", correo = "";
    Long telefono = Long.valueOf(0);
    public boolean anyEdit;
    Tool_Progress_Dialog progress_dialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Editar_Paciente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Editar_Paciente.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Editar_Paciente newInstance(String param1, String param2) {
        Fragment_Editar_Paciente fragment = new Fragment_Editar_Paciente();
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
        return inflater.inflate(R.layout.fragment_editar_paciente, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        getDialog().getWindow().setLayout((int) (width*.85), getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        txtFechaNacimiento = getView().findViewById(R.id.txtFechaNacimiento);
        txtNombre = getView().findViewById(R.id.txtNombre);
        btnClose = getView().findViewById(R.id.btnClose);
        txtCorreo = getView().findViewById(R.id.txtCorreo);
        txtTelefono = getView().findViewById(R.id.txtTelefono);
        imgPaciente = getView().findViewById(R.id.imgPaciente);
        imgContorno = getView().findViewById(R.id.imgContorno);
        btnEditar = getView().findViewById(R.id.btnEditar);
        btnFoto = getView().findViewById(R.id.btnFoto);
        imgPaciente.setOnClickListener(this::onClick);
        imgContorno.setOnClickListener(this::onClick);
        btnClose.setOnClickListener(this::onClick);
        btnFoto.setOnClickListener(this::onClick);
        btnEditar.setOnClickListener(this::onClick);
        txtFechaNacimiento.setOnClickListener(this::onClick);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
        startConfig();
    }

    public void startConfig () {
        txtTelefono.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        txtNombre.setText(nombre);
        txtCorreo.setText(correo);
        txtTelefono.setText(String.valueOf(telefono));
        txtFechaNacimiento.setText(fnac);
        Picasso.with(getContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .resize(90, 90)
                .centerCrop()
                .transform(new Tool_Images())
                .into(imgPaciente);
        Picasso.with(getContext()).load(fotoUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                fillFile(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnEditar:
                btnEditar.setEnabled(false);
                if (verificar_campos()) {
                    internetEdit();
                } else {
                    btnEditar.setEnabled(true);
                    progress_dialog.dismissDialog();
                }
                break;
            case R.id.txtFechaNacimiento:
                date_picker();
                break;
            case R.id.btnFoto:
            case R.id.imgContorno:
            case R.id.imgPaciente:
                selectPhoto();
                break;
            case R.id.btnClose:
                dismiss();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File cacheDir = new File(getContext().getCacheDir(), "images");
            cacheDir.mkdirs();
            try {
                shareFile = File.createTempFile("SHARE_", ".jpg", cacheDir);
                output = new FileOutputStream(shareFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
                output.flush();
                output.close();
                fillImage();
            } catch (IOException e) {
                Toasty.error(getContext(), "Ups! algo salió mal", Toasty.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (requestCode == 200 && data != null) {
            InputStream inputStream = null;
            try {
                inputStream = getContext().getContentResolver().openInputStream(data.getData());
                OutputStream outputStream = new FileOutputStream(shareFile);
                IOUtil.copy(inputStream, outputStream);
                fillImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void fillImage () {
        Picasso.with(getContext())
                .load(shareFile)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .resize(90, 90)
                .centerCrop()
                .transform(new Tool_Images())
                .into(imgPaciente);
    }

    public void selectPhoto() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setCancelable(true);
        sweetAlertDialog.setTitleText("¿Desea usar el albun de fotos o la camara?")
                .setConfirmButtonBackgroundColor(getResources().getColor(R.color.yellow))
                .setConfirmButton("Camara", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 100);
                    }
                })
                .setCancelButtonBackgroundColor(getResources().getColor(R.color.primaryBlueColor))
                .setCancelButton("Album", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        Intent intent = new Intent();
                        intent.setType("image/jpeg");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 200);
                    }
                }).show();
    }

    private boolean verificar_campos() {
        progress_dialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtNombre.getText())) {
            txtNombre.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtFechaNacimiento.getText())) {
            txtFechaNacimiento.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtCorreo.getText())) {
            txtCorreo.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtTelefono.getText())) {
            txtTelefono.setError(getString(R.string.campo_requerido));
            return false;
        }
        return true;
    }

    public void internetEdit() {
        if (Tool_Internet.isOnlineNet()) {
            progress_dialog.dismissDialog();
            editUser();
        } else {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Verifique su conexión a internet")
                    .setConfirmButton("Reintentar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            internetEdit();
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

    public void editUser() {
        progress_dialog.ShowProgressDialog();
        RequestBody requestFile = RequestBody.create(MediaType.parse(".jpeg"),shareFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("file", shareFile.getName(), requestFile);
        RequestBody clinic = RequestBody.create(okhttp3.MultipartBody.FORM, clinica);
        RequestBody idPaciente = RequestBody.create(okhttp3.MultipartBody.FORM, idPa);
        RequestBody nombre = RequestBody.create(okhttp3.MultipartBody.FORM, txtNombre.getText().toString());
        RequestBody correo = RequestBody.create(okhttp3.MultipartBody.FORM, txtCorreo.getText().toString());
        RequestBody telefono = RequestBody.create(okhttp3.MultipartBody.FORM, txtTelefono.getText().toString());
        RequestBody fechaNacimiento = RequestBody.create(okhttp3.MultipartBody.FORM, txtFechaNacimiento.getText().toString());
        PacientsServices service = RetrofitClientInstance.getRetrofitInstance().create(PacientsServices.class);
        Call<RetrofitMessage> call = service.editPacient(clinic, idPaciente, nombre, correo, telefono, fechaNacimiento, file);
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    Toasty.success(getContext(), "Se ha editado un paciente", Toasty.LENGTH_LONG).show();
                    anyEdit =  true;
                    Tools_Users_Defaults.setPacientFlag(getContext(), true);
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

    public void fillFile (Bitmap bitmap) {
        File cacheDir = new File(getContext().getCacheDir(), "images");
        cacheDir.mkdirs();
        try {
            shareFile = File.createTempFile("SHARE_", ".jpg", cacheDir);
            output = new FileOutputStream(shareFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
            output.flush();
            output.close();
        } catch (IOException e) {
            Toasty.error(getContext(), "Ups! algo salió mal", Toasty.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void date_picker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        picker = new DatePickerDialog(getContext(), R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String dia = dayOfMonth < 10 ? ("0" + dayOfMonth) : (dayOfMonth + "");
                String mes = month < 10 ? ("0" + month) : (month + "");
                fechaNacimiento = year + "-" + mes + "-" + dia;
                txtFechaNacimiento.setText(fechaNacimiento);
            }
        }, year, month, day);
        picker.show();
    }
}