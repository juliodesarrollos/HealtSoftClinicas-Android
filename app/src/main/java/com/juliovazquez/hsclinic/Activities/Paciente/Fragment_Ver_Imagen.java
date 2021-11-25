package com.juliovazquez.hsclinic.Activities.Paciente;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.github.tibolte.agendacalendarview.widgets.FloatingActionButton;
import com.juliovazquez.hsclinic.R;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Ver_Imagen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Ver_Imagen extends DialogFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String url = "";
    Button btnClose, btnReload;
    WebView webView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Ver_Imagen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Ver_Imagen.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Ver_Imagen newInstance(String param1, String param2) {
        Fragment_Ver_Imagen fragment = new Fragment_Ver_Imagen();
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
        return inflater.inflate(R.layout.fragment__ver__imagen, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getDialog().getWindow().setLayout((int) (width*.95), (int) (height*.95));
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnClose = getView().findViewById(R.id.btnClose);
        btnReload = getView().findViewById(R.id.btnReload);
        btnReload.setOnClickListener(this::onClick);
        btnClose.setOnClickListener(this::onClick);
        webView = getView().findViewById(R.id.imgFoto);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.loadUrl(url);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose: dismiss(); break;
            case R.id.btnReload:
                webView.reload();
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setInitialScale(1);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                break;
        }
    }
}