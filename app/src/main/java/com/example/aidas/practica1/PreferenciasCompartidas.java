package com.example.aidas.practica1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class PreferenciasCompartidas extends AppCompatActivity {

    private String user;
    private String pass;

    public PreferenciasCompartidas(String user, String pass) {

        this.user = user;
        this.pass = pass;

    }

    public void setShPref() {

        SharedPreferences pref = getSharedPreferences(getString(R.string.archivoSP), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(user, pass);
        editor.apply();
    }

    public void getShPref() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String v = pref.getString(user, pass);

    }
}
