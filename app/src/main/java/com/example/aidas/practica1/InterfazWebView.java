package com.example.aidas.practica1;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class InterfazWebView {
    private String usuario;
    private String pass;

    public InterfazWebView() {

    }

    public InterfazWebView(String usuario, String pass) {

        this.usuario = usuario;
        this.pass = pass;
    }

    public void setUsuario(String usuario) {

        this.usuario = usuario;
    }

    public void setPass(String pass) {

        this.pass = pass;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPass() {
        return pass;
    }

    @JavascriptInterface
    public void sendData(String usuario, String pass){

        setUsuario(usuario);
        setPass(pass);

        Log.v(TAG, usuario + " " + pass);

    }
}
