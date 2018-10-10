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
    private Object error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user="";
        pass="";

        webView = findViewById(R.id.wvPrincipal);
        webView.getSettings().setJavaScriptEnabled(true);
        final InterfazWebView iaw = new InterfazWebView();
        webView.addJavascriptInterface(iaw, "puente");
        webView.loadUrl("http://www.juntadeandalucia.es/averroes/centros-tic/18700098/moodle2/login/index.php");

        webView.setWebViewClient(new WebViewClient(){

            //contador = 0: primera vez que carga la pagina / no hay datos en SP / los datos en SP son incorrectos
            //contador > 0: segunda vez / datos validos en SP
            int contador = 0;

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                Log.v(TAG, "Ha cargado la página");

                if(contador == 0){

                    //Si es la primera vez que carga la página:

                    /*Establecemos un listener en el boton para que al hacer login captemos los datos
                    introducidos.
                    Ademas intentamos coger el elemento loginerrormessag, que solo aparece cuando los
                    datos introducidos son erroneos
                    */
                    final String javaScript = "" +
                            "   var boton = document.getElementById('loginbtn');" +
                            "   boton.addEventListener('click', function() {" +
                            "    var nombre = document.getElementById('username').value;" +
                            "    var clave  = document.getElementById('password').value;" +
                            "    var error  = document.getElementById('loginerrormessag');" +
                            "    puente.sendData(nombre, clave, error);" +
                            "});";

                    webView.loadUrl("javascript: " + javaScript);

                    //Obtenemos los datos que ha almacenado previamente la interfaz.
                    user = iaw.getUsuario();
                    pass = iaw.getPass();
                    error = iaw.getError();

                    //Si los valores son distintos de cadena nula los guardamos en las SP,
                    //y aumentamos el contador
                    if(user != "" && pass !=""){

                        setShPref();

                        Log.v(TAG, "Usuario guardado en SP: " + user);
                        Log.v(TAG, "Pass guardada en SP: " + pass);


                        contador++;

                    } //En caso de ser cadena nula dejamos contador = 0 y no guardamos los datos.


                }else{

                    //Si no es la primera vez que cargamos la pagina pasamos a comprobar si
                    //tenemos valores almacenados en las SP

                    getShPref();

                    if(user != "" && pass != ""){

                        //Si es así introducimos esos valores en los campos del formulario
                        //e iniciamos sesion automáticamente con ellos

                        final String javaScript = "" +
                                "    document.getElementById('username').value = \""+ user + "\";" +
                                "    document.getElementById('password').value = \""+ pass + "\";" +
                                "    document.getElementById('loginbtn').click();";

                        webView.loadUrl("javascript: " + javaScript);

                    }




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
        editor.putString("user", user);
        editor.putString("pass", pass);

        editor.apply();
    }

    //Metodo para obtener los datos almacenados en las p.c.

    public void getShPref() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        user = pref.getString("user", "No existe la info");
        pass = pref.getString("pass","No existe la info");


    }
}
