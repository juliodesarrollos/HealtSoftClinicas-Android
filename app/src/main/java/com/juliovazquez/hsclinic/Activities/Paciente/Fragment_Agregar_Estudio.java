package com.juliovazquez.hsclinic.Activities.Paciente;

import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.juliovazquez.hsclinic.Pojos.RetrofitMessage;
import com.juliovazquez.hsclinic.R;
import com.juliovazquez.hsclinic.Services.EstudiosServices;
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
 * Use the {@link Fragment_Agregar_Estudio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Agregar_Estudio extends DialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public boolean anyEstudio = false;
    String clinica = "", idPaciente = "";
    Button btnAgregar, btnFoto, btnClose;
    ImageView imgEstudio, imgContorno;
    EditText txtEstudio, txtDesc;
    File shareFile = null;
    OutputStream output = null;
    Long telefono = Long.valueOf(0);
    Tool_Progress_Dialog progress_dialog;
    boolean anyImage = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Agregar_Estudio() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Agregar_Estudio.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Agregar_Estudio newInstance(String param1, String param2) {
        Fragment_Agregar_Estudio fragment = new Fragment_Agregar_Estudio();
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
        return inflater.inflate(R.layout.fragment__agregar__estudio, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        getDialog().getWindow().setLayout((int) (width*.85),getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        txtDesc = getView().findViewById(R.id.txtDesc);
        txtEstudio = getView().findViewById(R.id.txtEstudio);
        btnClose = getView().findViewById(R.id.btnClose);
        progress_dialog = new Tool_Progress_Dialog(getActivity());
        imgEstudio = getView().findViewById(R.id.imgEstudio);
        imgContorno = getView().findViewById(R.id.imgContorno);
        btnAgregar = getView().findViewById(R.id.btnAgregar);
        btnFoto = getView().findViewById(R.id.btnFoto);
        imgEstudio.setOnClickListener(this::onClick);
        imgContorno.setOnClickListener(this::onClick);
        btnClose.setOnClickListener(this::onClick);
        btnFoto.setOnClickListener(this::onClick);
        btnAgregar.setOnClickListener(this::onClick);
        clinica = Tools_Users_Defaults.user_clinic(getContext());
        Picasso.with(getContext())
                .load("https://healtsoft.com/API/AClinic/default-images/estudios_icon.png")
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .resize(90, 90)
                .centerCrop()
                .transform(new Tool_Images())
                .into(imgEstudio);
        Picasso.with(getContext()).load("https://healtsoft.com/API/AClinic/default-images/estudios_icon.png").into(new Target() {
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnAgregar:
                btnAgregar.setEnabled(false);
                if (verificar_campos()) {
                    internetAdd();
                } else {
                    btnAgregar.setEnabled(true);
                    progress_dialog.dismissDialog();
                } break;
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
                Toasty.error(getContext(), "Ups! algo salió mal", Toasty.LENGTH_SHORT).show();
            }
        } else {
            Toasty.error(getContext(),"Debe seleccionar una imagen", Toasty.LENGTH_SHORT).show();
            selectPhoto();
        }
    }

    public void internetAdd() {
        if (Tool_Internet.isOnlineNet()) {
            progress_dialog.dismissDialog();
            addEstudio();
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

    public void fillImage () {
        Picasso.with(getContext())
                .load(shareFile)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .resize(90, 90)
                .centerCrop()
                .transform(new Tool_Images())
                .into(imgEstudio);
        anyImage = true;
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
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
                }
            }).show();
    }

    private boolean verificar_campos() {
        progress_dialog.ShowProgressDialog();
        if (TextUtils.isEmpty(txtEstudio.getText())) {
            txtEstudio.setError(getString(R.string.campo_requerido));
            return false;
        } else if (TextUtils.isEmpty(txtDesc.getText())) {
            txtDesc.setError(getString(R.string.campo_requerido));
            return false;
        } else if (!anyImage) {
            Toasty.error(getContext(),"Debe seleccionar una imagen", Toasty.LENGTH_SHORT).show();
            selectPhoto();
            return false;
        }
        return true;
    }

    public void addEstudio() {
        progress_dialog.ShowProgressDialog();
        RequestBody requestFile = RequestBody.create(MediaType.parse(".jpeg"),shareFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("file", shareFile.getName(), requestFile);
        RequestBody clinic = RequestBody.create(okhttp3.MultipartBody.FORM, clinica);
        RequestBody idPacient = RequestBody.create(okhttp3.MultipartBody.FORM, idPaciente);
        RequestBody nombre = RequestBody.create(okhttp3.MultipartBody.FORM, txtDesc.getText().toString());
        RequestBody descripcion = RequestBody.create(okhttp3.MultipartBody.FORM, txtDesc.getText().toString());
        EstudiosServices service = RetrofitClientInstance.getRetrofitInstance().create(EstudiosServices.class);
        Call<RetrofitMessage> call = service.addEstudio(clinic, idPacient, nombre, descripcion, file);
        call.enqueue(new Callback<RetrofitMessage>() {
            @Override
            public void onResponse(Call<RetrofitMessage> call, Response<RetrofitMessage> response) {
                progress_dialog.dismissDialog();
                if (response.code() == 200){
                    Toasty.success(getContext(), "Se ha guardado un estudio", Toasty.LENGTH_LONG).show();
                    anyEstudio =  true;
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
}