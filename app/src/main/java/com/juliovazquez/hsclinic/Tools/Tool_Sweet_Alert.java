package com.juliovazquez.hsclinic.Tools;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Tool_Sweet_Alert {
    public static void SUCCESS_TYPE (Context context, String mensaje) {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(mensaje)
                .setConfirmText("Ok!")
                .show();
    }

    public static void ERROR_TYPE (Context context, String mensaje) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(mensaje)
                .setConfirmText("Ok!")
                .show();
    }

    public static void ERROR_TYPE_CONTENT (Context context, String mensaje, String content) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(mensaje)
                .setContentText(content)
                .setConfirmText("Ok!")
                .show();
    }

    public static void WARNING_TYPE (Context context, String mensaje) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(mensaje)
                .setConfirmText("Ok!")
                .show();
    }

    public static boolean YES_NO_TYPE (Context context, String title, String yes, String no ) {
        final boolean[] yes_no = new boolean[1];
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText(title)
                .setConfirmButton(yes, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        yes_no[0] = true;
                    }
                })
                .setCancelButton(no, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        yes_no[0] = false;
                    }
                })
                .show();
        return  yes_no[0];
    }
}
