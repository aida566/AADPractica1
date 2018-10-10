package com.example.aidas.practica1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.InterfaceAddress;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MITAG";
    private WebView webView;
    private String user;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.wvPrincipal);

        webView.getSettings().setJavaScriptEnabled(true);

        final InterfazWebView iaw = new InterfazWebView();

        webView.addJavascriptInterface(iaw, "puente");

        webView.loadUrl("http://www.juntadeandalucia.es/averroes/centros-tic/18700098/moodle2/login/index.php");

        webView.setWebViewClient(new WebViewClient(){

            int contador = 0;

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);

                Log.v(TAG,"Accedemos a : " + url);

                //Primera vez que se inicia la app.

                if(contador == 0) {

                    Log.v(TAG, "on page finished");

                    final String javaScript = "" +
                            "   var boton = document.getElementById('loginbtn');" +
                            "   boton.addEventListener('click', function() {" +
                            "    var nombre = document.getElementById('username').value;" +
                            "    var clave  = document.getElementById('password').value;" +
                            "    puente.sendData(nombre, clave);" +
                            "});";

                    webView.loadUrl("javascript: " + javaScript);

                    //Obtenemos los valores que ha almacenado la interfaz.
                    user = iaw.getUsuario();
                    pass = iaw.getPass();

                    //Los guardamos en sharedPreferences.
                    setShPref();

                    contador++;

                }else{

                    final String javaScript = "" +
                            "   var boton = document.getElementById('loginbtn');" +
                            "   boton.addEventListener('click', function() {" +
                            "    var nombre = document.getElementById('username').value;" +
                            "    var clave  = document.getElementById('password').value;" +
                            "    puente.sendData(nombre, clave);" +
                            "});";

                    webView.loadUrl("javascript: " + javaScript);

                }

                //Mostramos la url a la que estamos accediendo.
                Log.v(TAG, "final: " + webView.getUrl());
            }
        });
    }

    //Metodo para guardar las preferencias compartidas
    public void setShPref() {

        SharedPreferences pref = getSharedPreferences(getString(R.string.archivoSP), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("usuario", user);
        editor.putString("pass", pass);

        editor.apply();
    }

    //Metodo para obtener los datos almacenados en las p.c.
    public void getShPref() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String userP = pref.getString(user, "No existe la info");
        String passP = pref.getString(pass,"No existe la info");


    }
}
