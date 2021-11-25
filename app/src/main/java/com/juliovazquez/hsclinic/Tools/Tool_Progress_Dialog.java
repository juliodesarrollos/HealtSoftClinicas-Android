package com.juliovazquez.hsclinic.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.juliovazquez.hsclinic.R;

public class Tool_Progress_Dialog {

    Activity activity;
    AlertDialog dialog;

    public Tool_Progress_Dialog(Activity myActivity){
        activity = myActivity;
    }

    public void ShowProgressDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
