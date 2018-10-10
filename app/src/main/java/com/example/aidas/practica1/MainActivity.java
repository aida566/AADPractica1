package com.example.aidas.practica1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.InterfaceAddress;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MITAG";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.wvPrincipal);

        webView.getSettings().setJavaScriptEnabled(true);

        InterfazWebView iaw = new InterfazWebView();

        webView.addJavascriptInterface(iaw, "puente");

        webView.loadUrl("http://www.juntadeandalucia.es/averroes/centros-tic/18700098/moodle2/login/index.php");

        webView.setWebViewClient(new WebViewClient(){

            int contador = 0;

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                //Mostramos la url a la que estamos accediendo.
                Log.v(TAG, url);

                if(contador == 0) {

                    Log.v(TAG, "on page finished");

                    /*final String javaScript =
                            "var nombre = document.getElementById('nombre');" +
                            "var btEnviar = document.getElementById('btEnviar');" +
                            "nombre.value = 'pepito';" +
                            "btEnviar.addEventListener('click', function() {Android: var data = nombre.value})" +
                            "btEnviar.click();";
                            webView.loadUrl("javascript: " + javaScript);*/

                    final String javaScript = "" +
                            "   var boton = document.getElementById('loginbtn');" +
                            "   boton.addEventListener('click', function() {" +
                            "    var nombre = document.getElementById('username').value;" +
                            "    var clave  = document.getElementById('password').value;" +
                            "    puente.sendData(nombre, clave);" +
                            "});";

                    webView.loadUrl("javascript: " + javaScript);

                }

                contador++;
            }
        });


        //Mostramos la url a la que estamos accediendo.
        Log.v(TAG, webView.getUrl());
    }
}
