package com.juliovazquez.hsclinic.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.juliovazquez.hsclinic.Pojos.RetrofitLogin;

public class Tools_Users_Defaults {
    public static void guardar_sesion(Context context, RetrofitLogin user, String clinic) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context. MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("ISUSERLOGGEDIN", true);
        editor.putInt("USERID", user.getId());
        editor.putString("USERNAME", user.getName());
        editor.putString("USERROL", user.getRol());
        editor.putString("USEREMAIL", user.getEmail());
        editor.putString("USERCLINIC",  clinic);
        editor.apply();
    }

    public static void borrar_sesion(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context. MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("ISUSERLOGGEDIN", false);
        editor.putInt("USERID", 0);
        editor.putString("USERNAME", "");
        editor.putString("USERROL", "");
        editor.putString("USEREMAIL", "");
        editor.putString("USERCLINIC", "");
        editor.apply();
    }

    public static boolean is_user_logged_in(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context.MODE_PRIVATE);
        return settings.getBoolean("ISUSERLOGGEDIN", false);
    }

    public static int user_id(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context.MODE_PRIVATE);
        return settings.getInt("USERID", 0);
    }

    public static String user_name(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context.MODE_PRIVATE);
        return settings.getString("USERNAME", "");
    }

    public static String user_name_rol(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context.MODE_PRIVATE);
        String name = settings.getString("USERNAME", "");
        String rol = settings.getString("USERROL", "");
        String[] parts = name.split(" ");
        String nameRol = parts[0] + " ("+rol+")";
        return nameRol;
    }

    public static String user_rol(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context.MODE_PRIVATE);
        return settings.getString("USERROL", "");
    }

    public static String user_email(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context.MODE_PRIVATE);
        return settings.getString("USEREMAIL", "");
    }

    public static String user_clinic(Context context) {
        SharedPreferences settings = context.getSharedPreferences("USERINFORMATION", context.MODE_PRIVATE);
        return settings.getString("USERCLINIC", "");
    }

    public static boolean getEventState(Context context) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context.MODE_PRIVATE);
        return settings.getBoolean("EVENTFLAG", false);
    }

    public static void setEventFlag (Context context, Boolean flag) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context. MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("EVENTFLAG", flag);
        editor.apply();
    }

    public static boolean getEventChartFlag(Context context) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context.MODE_PRIVATE);
        return settings.getBoolean("EVENTCHARTFLAG", false);
    }

    public static void setEventChartFlag (Context context, Boolean flag) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context. MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("EVENTCHARTFLAG", flag);
        editor.apply();
    }

    public static boolean getPacientChartFlag(Context context) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context.MODE_PRIVATE);
        return settings.getBoolean("PACIENTCHARTFLAG", false);
    }

    public static void setPacientChartFlag (Context context, Boolean flag) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context. MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("PACIENTCHARTFLAG", flag);
        editor.apply();
    }

    public static boolean getPacientFlag(Context context) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context.MODE_PRIVATE);
        return settings.getBoolean("PACIENTFLAG", false);
    }

    public static void setPacientFlag (Context context, Boolean flag) {
        SharedPreferences settings = context.getSharedPreferences("FLAGS", context. MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("PACIENTFLAG", flag);
        editor.apply();
    }
}
