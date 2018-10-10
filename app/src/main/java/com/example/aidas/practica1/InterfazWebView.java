package com.example.aidas.practica1;

import android.util.Log;
import android.webkit.JavascriptInterface;

import static com.example.aidas.practica1.MainActivity.TAG;

public class InterfazWebView {

    private String usuario;
    private String pass;
    private Object error;

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

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    @JavascriptInterface
    public void sendData(String usuario, String pass){

        setUsuario(usuario);
        setPass(pass);

        Log.v(TAG, "sendData - Usuario: " + usuario + " Pass: " + pass);

    }

    @JavascriptInterface
    public void sendData(Object error){

        setError(error);

        Log.v(TAG, " Error: " + error);

    }

}
